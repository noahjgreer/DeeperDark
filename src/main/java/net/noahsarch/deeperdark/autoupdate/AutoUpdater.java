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
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.stream.Stream;

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

        // Delete any stale deeperdark jars from previous failed/duplicate update cycles.
        // These are not the currently-loaded jar so they are not locked by the JVM.
        purgeStaleDeeperdarkFiles(modsDir, currentJar, newJar);

        // If the target jar already exists on disk, Fabric may have picked an older duplicate
        // as the "current" version (due to lexicographic prerelease ordering). Skip the
        // download — the file is already there.
        if (!Files.exists(newJar)) {
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

            Files.move(tempJar, newJar, StandardCopyOption.REPLACE_EXISTING);
            LOGGER.info("[AutoUpdater] Downloaded new JAR to: {}", newJar);
        } else {
            LOGGER.info("[AutoUpdater] New JAR already present, skipping download: {}", newJar);
        }

        // Schedule the old JAR for removal on next startup (Windows holds the file open)
        schedulePendingDelete(modsDir, currentJar);
    }

    // ===== Pending-delete (startup cleanup) =====

    private static final String PENDING_DELETE_FILE = ".deeperdark-pending-delete";

    /**
     * Called at startup. Cleans up leftover deeperdark .bak files and any JARs that
     * couldn't be deleted in the previous session because the JVM held them open (Windows).
     */
    public static void processPendingDeletes() {
        Path modsDir = getModsDir();
        if (modsDir == null) return;

        // Always sweep for .bak files first — they are never loaded by Fabric so they
        // are never locked, and can be deleted unconditionally.
        purgeDeeperdarkBakFiles(modsDir);

        Path marker = modsDir.resolve(PENDING_DELETE_FILE);
        if (!Files.exists(marker)) return;

        try {
            boolean allCleared = true;
            for (String line : Files.readAllLines(marker, StandardCharsets.UTF_8)) {
                String trimmed = line.strip();
                if (trimmed.isEmpty()) continue;
                Path target = Path.of(trimmed);
                Path bak = target.resolveSibling(target.getFileName().toString() + ".bak");

                boolean jarExists = Files.exists(target);
                boolean bakExists = Files.exists(bak);

                if (jarExists) {
                    if (tryDelete(target)) {
                        jarExists = false;
                    } else {
                        // Direct delete failed (file still locked) — retry the .bak rename so
                        // Fabric skips it next session, then clean up the .bak the session after.
                        try {
                            Files.move(target, bak, StandardCopyOption.REPLACE_EXISTING);
                            LOGGER.info("[AutoUpdater] Renamed old JAR to .bak for next-launch cleanup: {}", bak);
                            jarExists = false;
                            bakExists = true;
                        } catch (IOException ex) {
                            LOGGER.warn("[AutoUpdater] Could not rename old JAR to .bak: {}", ex.getMessage());
                        }
                    }
                }

                if (bakExists && !tryDelete(bak)) {
                    bakExists = Files.exists(bak); // re-check; tryDelete logs on failure
                }

                if (jarExists || bakExists) {
                    allCleared = false;
                }
            }
            if (allCleared) {
                Files.deleteIfExists(marker);
            }
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
        // so the next launch cleans it up.
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

        // Also rename to .bak so Fabric ignores it next session even if the pending-delete
        // cleanup fails for any reason.
        try {
            Path bak = oldJar.resolveSibling(oldJar.getFileName().toString() + ".bak");
            Files.move(oldJar, bak, StandardCopyOption.REPLACE_EXISTING);
            LOGGER.info("[AutoUpdater] Renamed old JAR to .bak: {}", bak);
        } catch (IOException ignored) {}
    }

    /**
     * Deletes all deeperdark-*.jar and deeperdark-*.jar.bak files in the mods directory
     * except {@code currentJar} (currently loaded/locked) and {@code targetJar} (the jar
     * we are about to place). Call this before each download to prevent accumulation.
     */
    private static void purgeStaleDeeperdarkFiles(Path modsDir, Path currentJar, Path targetJar) {
        try (Stream<Path> stream = Files.list(modsDir)) {
            stream.forEach(p -> {
                String name = p.getFileName().toString();
                if (!name.startsWith("deeperdark-")) return;
                if (p.equals(currentJar) || p.equals(targetJar)) return;
                if (!name.endsWith(".jar") && !name.endsWith(".jar.bak")) return;
                tryDelete(p);
            });
        } catch (IOException e) {
            LOGGER.warn("[AutoUpdater] Could not scan for stale deeperdark files: {}", e.getMessage());
        }
    }

    /** Deletes all deeperdark-*.jar.bak files. These are never loaded by Fabric so never locked. */
    private static void purgeDeeperdarkBakFiles(Path modsDir) {
        try (Stream<Path> stream = Files.list(modsDir)) {
            stream.filter(p -> {
                String name = p.getFileName().toString();
                return name.startsWith("deeperdark-") && name.endsWith(".jar.bak");
            }).forEach(AutoUpdater::tryDelete);
        } catch (IOException e) {
            LOGGER.warn("[AutoUpdater] Could not scan for stale .bak files: {}", e.getMessage());
        }
    }

    private static boolean tryDelete(Path path) {
        try {
            boolean deleted = Files.deleteIfExists(path);
            if (deleted) LOGGER.info("[AutoUpdater] Deleted: {}", path);
            return deleted;
        } catch (IOException e) {
            LOGGER.warn("[AutoUpdater] Could not delete {}: {}", path, e.getMessage());
            return false;
        }
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
