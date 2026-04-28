package net.noahsarch.deeperdark.autoupdate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class AutoUpdater {

    private static final Logger LOGGER = LoggerFactory.getLogger("DeeperDark/AutoUpdater");

    public record UpdateInfo(String latestVersion, String downloadUrl, long fileSize) {}

    @FunctionalInterface
    public interface ProgressCallback {
        void onProgress(long downloadedBytes, long totalBytes);
    }

    public static String getCurrentVersion() {
        return FabricLoader.getInstance()
                .getModContainer("deeperdark")
                .map(c -> c.getMetadata().getVersion().getFriendlyString())
                .orElse("unknown");
    }

    public static Optional<UpdateInfo> checkForUpdate(String repoUrl) throws IOException {
        String apiUrl = toApiUrl(repoUrl);
        HttpURLConnection conn = openConnection(apiUrl);

        int status = conn.getResponseCode();
        if (status != 200) {
            conn.disconnect();
            throw new IOException("GitHub API responded with HTTP " + status);
        }

        String json;
        try (InputStream is = conn.getInputStream()) {
            json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } finally {
            conn.disconnect();
        }

        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();

        if (!obj.has("tag_name")) {
            String message = obj.has("message") ? obj.get("message").getAsString() : "unknown error";
            throw new IOException("GitHub API error: " + message);
        }

        String latestTag = obj.get("tag_name").getAsString();
        String latestVersion = normalizeVersion(latestTag);
        String currentVersion = normalizeVersion(getCurrentVersion());

        if (currentVersion.equals(latestVersion)) {
            return Optional.empty();
        }

        // Find the JAR asset
        if (obj.has("assets")) {
            JsonArray assets = obj.getAsJsonArray("assets");
            for (JsonElement elem : assets) {
                JsonObject asset = elem.getAsJsonObject();
                String name = asset.get("name").getAsString();
                if (name.endsWith(".jar")) {
                    String url = asset.get("browser_download_url").getAsString();
                    long size = asset.has("size") ? asset.get("size").getAsLong() : -1;
                    return Optional.of(new UpdateInfo(latestVersion, url, size));
                }
            }
        }

        throw new IOException("No JAR asset found in release " + latestTag);
    }

    public static void downloadUpdate(UpdateInfo info, ProgressCallback onProgress) throws IOException {
        Path currentJar = FabricLoader.getInstance()
                .getModContainer("deeperdark")
                .orElseThrow(() -> new IOException("Could not locate deeperdark mod container"))
                .getOrigin().getPaths().get(0);

        if (Files.isDirectory(currentJar)) {
            throw new IOException("Auto-update is not supported in development environments.");
        }

        Path modsDir = currentJar.getParent();

        // Derive new filename from the download URL, decoding percent-encoding (e.g. %2B → +)
        String rawUrl = info.downloadUrl();
        String encodedName = rawUrl.substring(rawUrl.lastIndexOf('/') + 1);
        String newFileName = URLDecoder.decode(encodedName, StandardCharsets.UTF_8);
        Path newJar = modsDir.resolve(newFileName);
        Path tempJar = modsDir.resolve(newFileName + ".tmp");

        HttpURLConnection conn = openConnection(rawUrl);
        int status = conn.getResponseCode();
        if (status != 200) {
            conn.disconnect();
            throw new IOException("Download server responded with HTTP " + status);
        }

        long totalBytes = info.fileSize() > 0 ? info.fileSize() : conn.getContentLengthLong();

        try {
            try (InputStream is = conn.getInputStream();
                 FileOutputStream fos = new FileOutputStream(tempJar.toFile())) {
                byte[] buffer = new byte[8192];
                long downloaded = 0;
                int read;
                while ((read = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                    downloaded += read;
                    if (onProgress != null) {
                        onProgress.onProgress(downloaded, Math.max(totalBytes, downloaded));
                    }
                }
            }
        } catch (IOException e) {
            Files.deleteIfExists(tempJar);
            conn.disconnect();
            throw e;
        }
        conn.disconnect();

        // Atomically place the new JAR
        Files.move(tempJar, newJar, StandardCopyOption.REPLACE_EXISTING);
        LOGGER.info("[AutoUpdater] Downloaded new JAR to: {}", newJar);

        // Schedule the old JAR for removal on next startup (Windows holds the file open)
        schedulePendingDelete(modsDir, currentJar);
    }

    // ===== Pending-delete (startup cleanup) =====

    private static final String PENDING_DELETE_FILE = ".deeperdark-pending-delete";

    /**
     * Called at startup before the update check. Removes any JARs that couldn't be
     * deleted in the previous session because the JVM held them open (Windows).
     */
    public static void processPendingDeletes() {
        Path modsDir = getModsDir();
        if (modsDir == null) return;
        Path marker = modsDir.resolve(PENDING_DELETE_FILE);
        if (!Files.exists(marker)) return;

        try {
            for (String line : Files.readAllLines(marker, StandardCharsets.UTF_8)) {
                String trimmed = line.strip();
                if (trimmed.isEmpty()) continue;
                Path target = Path.of(trimmed);
                tryDelete(target);
                tryDelete(target.resolveSibling(target.getFileName().toString() + ".bak"));
            }
            Files.deleteIfExists(marker);
        } catch (IOException e) {
            LOGGER.warn("[AutoUpdater] Failed to process pending deletes: {}", e.getMessage());
        }
    }

    private static void schedulePendingDelete(Path modsDir, Path oldJar) {
        // Try immediate removal first (works on Linux/Mac and sometimes Windows)
        try {
            Files.delete(oldJar);
            LOGGER.info("[AutoUpdater] Deleted old JAR: {}", oldJar);
            return;
        } catch (IOException ignored) {}

        // On Windows the JVM memory-maps the JAR; write it to the pending-delete file
        // so the next launch cleans it up before Fabric loads any mods.
        Path marker = modsDir.resolve(PENDING_DELETE_FILE);
        try {
            Files.writeString(marker, oldJar.toAbsolutePath().toString() + "\n",
                    StandardCharsets.UTF_8,
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.APPEND);
            LOGGER.info("[AutoUpdater] Scheduled old JAR for deletion on next launch: {}", oldJar);
        } catch (IOException e) {
            LOGGER.warn("[AutoUpdater] Could not schedule pending delete: {}", e.getMessage());
        }

        // Also try rename-to-.bak so Fabric ignores it even if delete at next launch fails
        try {
            Path bak = oldJar.resolveSibling(oldJar.getFileName().toString() + ".bak");
            Files.move(oldJar, bak, StandardCopyOption.REPLACE_EXISTING);
            LOGGER.info("[AutoUpdater] Renamed old JAR to .bak: {}", bak);
        } catch (IOException ignored) {}
    }

    private static void tryDelete(Path path) {
        try {
            Files.deleteIfExists(path);
            LOGGER.info("[AutoUpdater] Deleted: {}", path);
        } catch (IOException ignored) {}
    }

    private static Path getModsDir() {
        return FabricLoader.getInstance()
                .getModContainer("deeperdark")
                .map(c -> {
                    try {
                        Path jar = c.getOrigin().getPaths().get(0);
                        return Files.isDirectory(jar) ? null : jar.getParent();
                    } catch (Exception e) {
                        return null;
                    }
                })
                .orElse(null);
    }

    private static HttpURLConnection openConnection(String urlStr) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) URI.create(urlStr).toURL().openConnection();
        conn.setRequestProperty("User-Agent", "DeeperDark-AutoUpdater/1.0");
        conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
        conn.setConnectTimeout(10_000);
        conn.setReadTimeout(120_000);
        conn.setInstanceFollowRedirects(true);
        return conn;
    }

    private static String toApiUrl(String repoUrl) {
        // https://github.com/owner/repo/releases/latest
        // -> https://api.github.com/repos/owner/repo/releases/latest
        return repoUrl.replace("https://github.com/", "https://api.github.com/repos/");
    }

    private static String normalizeVersion(String version) {
        return version != null && version.startsWith("v") ? version.substring(1) : version;
    }
}
