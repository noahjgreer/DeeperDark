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

        // Derive new filename from the download URL
        String rawUrl = info.downloadUrl();
        String newFileName = rawUrl.substring(rawUrl.lastIndexOf('/') + 1);
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

        // Clean up the old JAR so Fabric doesn't load both on restart
        cleanupOldJar(currentJar);
    }

    private static void cleanupOldJar(Path oldJar) {
        // Direct delete works on Linux/Mac; on Windows the file is held by the JVM
        try {
            Files.delete(oldJar);
            LOGGER.info("[AutoUpdater] Deleted old JAR: {}", oldJar);
            return;
        } catch (IOException ignored) {}

        // Rename to .bak so Fabric Loader ignores it (doesn't end in .jar)
        try {
            Path renamed = oldJar.resolveSibling(oldJar.getFileName().toString() + ".bak");
            Files.move(oldJar, renamed, StandardCopyOption.REPLACE_EXISTING);
            // Also register for deletion when the JVM exits cleanly (Restart Now path)
            renamed.toFile().deleteOnExit();
            LOGGER.info("[AutoUpdater] Renamed old JAR to: {}", renamed);
            return;
        } catch (IOException ignored) {}

        // Last resort: register with the JVM for deletion on clean exit
        oldJar.toFile().deleteOnExit();
        LOGGER.warn("[AutoUpdater] Could not remove old JAR immediately; registered deleteOnExit: {}", oldJar);
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
