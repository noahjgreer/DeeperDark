/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.Hashing
 *  com.google.common.io.Files
 *  com.mojang.logging.LogUtils
 *  javax.annotation.CheckReturnValue
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.realms.FileDownload
 *  net.minecraft.client.realms.FileDownload$DownloadCountingOutputStream
 *  net.minecraft.client.realms.dto.WorldDownload
 *  net.minecraft.client.realms.exception.RealmsDefaultUncaughtExceptionHandler
 *  net.minecraft.client.realms.gui.screen.RealmsDownloadLatestWorldScreen$DownloadStatus
 *  net.minecraft.nbt.NbtCrashException
 *  net.minecraft.nbt.NbtException
 *  net.minecraft.util.Util
 *  net.minecraft.util.path.SymlinkValidationException
 *  net.minecraft.world.level.storage.LevelStorage
 *  net.minecraft.world.level.storage.LevelStorage$LevelSave
 *  net.minecraft.world.level.storage.LevelStorage$Session
 *  org.apache.commons.compress.archivers.tar.TarArchiveEntry
 *  org.apache.commons.compress.archivers.tar.TarArchiveInputStream
 *  org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.mojang.logging.LogUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.OptionalLong;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.CheckReturnValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.realms.FileDownload;
import net.minecraft.client.realms.dto.WorldDownload;
import net.minecraft.client.realms.exception.RealmsDefaultUncaughtExceptionHandler;
import net.minecraft.client.realms.gui.screen.RealmsDownloadLatestWorldScreen;
import net.minecraft.nbt.NbtCrashException;
import net.minecraft.nbt.NbtException;
import net.minecraft.util.Util;
import net.minecraft.util.path.SymlinkValidationException;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class FileDownload {
    private static final Logger LOGGER = LogUtils.getLogger();
    private volatile boolean cancelled;
    private volatile boolean finished;
    private volatile boolean error;
    private volatile boolean extracting;
    private volatile @Nullable File backupFile;
    private volatile File resourcePackPath;
    private volatile @Nullable CompletableFuture<?> future;
    private @Nullable Thread currentThread;
    private static final String[] INVALID_FILE_NAMES = new String[]{"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

    private <T> @Nullable T submit(CompletableFuture<T> future) throws Throwable {
        this.future = future;
        if (this.cancelled) {
            future.cancel(true);
            return null;
        }
        try {
            try {
                return future.join();
            }
            catch (CompletionException completionException) {
                throw completionException.getCause();
            }
        }
        catch (CancellationException cancellationException) {
            return null;
        }
    }

    private static HttpClient createClient() {
        return HttpClient.newBuilder().executor((Executor)Util.getDownloadWorkerExecutor()).connectTimeout(Duration.ofMinutes(2L)).build();
    }

    private static HttpRequest.Builder createRequestBuilder(String uri) {
        return HttpRequest.newBuilder(URI.create(uri)).timeout(Duration.ofMinutes(2L));
    }

    @CheckReturnValue
    public static OptionalLong contentLength(String uri) {
        HttpClient httpClient = FileDownload.createClient();
        try {
            HttpResponse<Void> httpResponse = httpClient.send(FileDownload.createRequestBuilder((String)uri).HEAD().build(), HttpResponse.BodyHandlers.discarding());
            OptionalLong optionalLong = httpResponse.headers().firstValueAsLong("Content-Length");
            if (httpClient != null) {
                httpClient.close();
            }
            return optionalLong;
        }
        catch (Throwable throwable) {
            try {
                if (httpClient != null) {
                    try {
                        httpClient.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
            catch (Exception exception) {
                LOGGER.error("Unable to get content length for download");
                return OptionalLong.empty();
            }
        }
    }

    public void downloadWorld(WorldDownload download, String message, RealmsDownloadLatestWorldScreen.DownloadStatus status, LevelStorage storage) {
        if (this.currentThread != null) {
            return;
        }
        this.currentThread = new Thread(() -> {
            try (HttpClient httpClient = FileDownload.createClient();){
                try {
                    this.backupFile = File.createTempFile("backup", ".tar.gz");
                    this.download(status, httpClient, download.downloadLink(), this.backupFile);
                    this.extract(message.trim(), this.backupFile, storage, status);
                }
                catch (Exception exception) {
                    LOGGER.error("Caught exception while downloading world", (Throwable)exception);
                    this.error = true;
                }
                finally {
                    this.future = null;
                    if (this.backupFile != null) {
                        this.backupFile.delete();
                    }
                    this.backupFile = null;
                }
                if (this.error) {
                    return;
                }
                String string2 = download.resourcePackUrl();
                if (!string2.isEmpty() && !download.resourcePackHash().isEmpty()) {
                    try {
                        this.backupFile = File.createTempFile("resources", ".tar.gz");
                        this.download(status, httpClient, string2, this.backupFile);
                        this.validateAndCopy(status, this.backupFile, download);
                    }
                    catch (Exception exception2) {
                        LOGGER.error("Caught exception while downloading resource pack", (Throwable)exception2);
                        this.error = true;
                    }
                    finally {
                        this.future = null;
                        if (this.backupFile != null) {
                            this.backupFile.delete();
                        }
                        this.backupFile = null;
                    }
                }
                this.finished = true;
            }
        });
        this.currentThread.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new RealmsDefaultUncaughtExceptionHandler(LOGGER));
        this.currentThread.start();
    }

    private void download(RealmsDownloadLatestWorldScreen.DownloadStatus status, HttpClient client, String uri, File output) throws IOException {
        HttpResponse httpResponse;
        HttpRequest httpRequest = FileDownload.createRequestBuilder((String)uri).GET().build();
        try {
            httpResponse = (HttpResponse)this.submit(client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofInputStream()));
        }
        catch (Error error) {
            throw error;
        }
        catch (Throwable throwable) {
            LOGGER.error("Failed to download {}", (Object)uri, (Object)throwable);
            this.error = true;
            return;
        }
        if (httpResponse == null || this.cancelled) {
            return;
        }
        if (httpResponse.statusCode() != 200) {
            this.error = true;
            return;
        }
        status.totalBytes = httpResponse.headers().firstValueAsLong("Content-Length").orElse(0L);
        try (InputStream inputStream = (InputStream)httpResponse.body();
             FileOutputStream outputStream = new FileOutputStream(output);){
            inputStream.transferTo((OutputStream)new DownloadCountingOutputStream((OutputStream)outputStream, status));
        }
    }

    public void cancel() {
        if (this.backupFile != null) {
            this.backupFile.delete();
            this.backupFile = null;
        }
        this.cancelled = true;
        CompletableFuture completableFuture = this.future;
        if (completableFuture != null) {
            completableFuture.cancel(true);
        }
    }

    public boolean isFinished() {
        return this.finished;
    }

    public boolean isError() {
        return this.error;
    }

    public boolean isExtracting() {
        return this.extracting;
    }

    public static String findAvailableFolderName(String folder) {
        folder = ((String)folder).replaceAll("[\\./\"]", "_");
        for (String string : INVALID_FILE_NAMES) {
            if (!((String)folder).equalsIgnoreCase(string)) continue;
            folder = "_" + (String)folder + "_";
        }
        return folder;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void untarGzipArchive(String name, @Nullable File archive, LevelStorage storage) throws IOException {
        Object string2;
        Pattern pattern = Pattern.compile(".*-([0-9]+)$");
        int i = 1;
        for (char c : SharedConstants.INVALID_CHARS_LEVEL_NAME) {
            name = name.replace(c, '_');
        }
        if (StringUtils.isEmpty((CharSequence)name)) {
            name = "Realm";
        }
        name = FileDownload.findAvailableFolderName((String)name);
        try {
            Object object = storage.getLevelList().iterator();
            while (object.hasNext()) {
                LevelStorage.LevelSave levelSave = (LevelStorage.LevelSave)object.next();
                String string = levelSave.getRootPath();
                if (!string.toLowerCase(Locale.ROOT).startsWith(name.toLowerCase(Locale.ROOT))) continue;
                Matcher matcher = pattern.matcher(string);
                if (matcher.matches()) {
                    int j = Integer.parseInt(matcher.group(1));
                    if (j <= i) continue;
                    i = j;
                    continue;
                }
                ++i;
            }
        }
        catch (Exception exception) {
            LOGGER.error("Error getting level list", (Throwable)exception);
            this.error = true;
            return;
        }
        if (!storage.isLevelNameValid(name) || i > 1) {
            string2 = name + (String)(i == 1 ? "" : "-" + i);
            if (!storage.isLevelNameValid((String)string2)) {
                boolean bl = false;
                while (!bl) {
                    if (!storage.isLevelNameValid((String)(string2 = name + (String)(++i == 1 ? "" : "-" + i)))) continue;
                    bl = true;
                }
            }
        } else {
            string2 = name;
        }
        TarArchiveInputStream tarArchiveInputStream = null;
        File file = new File(MinecraftClient.getInstance().runDirectory.getAbsolutePath(), "saves");
        try {
            file.mkdir();
            tarArchiveInputStream = new TarArchiveInputStream((InputStream)new GzipCompressorInputStream((InputStream)new BufferedInputStream(new FileInputStream(archive))));
            TarArchiveEntry tarArchiveEntry = tarArchiveInputStream.getNextTarEntry();
            while (tarArchiveEntry != null) {
                File file2 = new File(file, tarArchiveEntry.getName().replace("world", (CharSequence)string2));
                if (tarArchiveEntry.isDirectory()) {
                    file2.mkdirs();
                } else {
                    file2.createNewFile();
                    try (FileOutputStream fileOutputStream = new FileOutputStream(file2);){
                        IOUtils.copy((InputStream)tarArchiveInputStream, (OutputStream)fileOutputStream);
                    }
                }
                tarArchiveEntry = tarArchiveInputStream.getNextTarEntry();
            }
        }
        catch (Exception exception2) {
            LOGGER.error("Error extracting world", (Throwable)exception2);
            this.error = true;
        }
        finally {
            if (tarArchiveInputStream != null) {
                tarArchiveInputStream.close();
            }
            if (archive != null) {
                archive.delete();
            }
            try (LevelStorage.Session session = storage.createSession((String)string2);){
                session.removePlayerAndSave((String)string2);
            }
            catch (IOException | NbtCrashException | NbtException exception2) {
                LOGGER.error("Failed to modify unpacked realms level {}", string2, (Object)exception2);
            }
            catch (SymlinkValidationException symlinkValidationException) {
                LOGGER.warn("Failed to download file", (Throwable)symlinkValidationException);
            }
            this.resourcePackPath = new File(file, (String)string2 + File.separator + "resources.zip");
        }
    }

    private void extract(String name, File archive, LevelStorage storage, RealmsDownloadLatestWorldScreen.DownloadStatus status) {
        if (status.bytesWritten >= status.totalBytes && !this.cancelled && !this.error) {
            try {
                this.extracting = true;
                this.untarGzipArchive(name, archive, storage);
            }
            catch (IOException iOException) {
                LOGGER.error("Error extracting archive", (Throwable)iOException);
                this.error = true;
            }
        }
    }

    private void validateAndCopy(RealmsDownloadLatestWorldScreen.DownloadStatus status, File file, WorldDownload download) {
        if (status.bytesWritten >= status.totalBytes && !this.cancelled) {
            try {
                String string = Hashing.sha1().hashBytes(Files.toByteArray((File)file)).toString();
                if (string.equals(download.resourcePackHash())) {
                    FileUtils.copyFile((File)file, (File)this.resourcePackPath);
                    this.finished = true;
                } else {
                    LOGGER.error("Resourcepack had wrong hash (expected {}, found {}). Deleting it.", (Object)download.resourcePackHash(), (Object)string);
                    FileUtils.deleteQuietly((File)file);
                    this.error = true;
                }
            }
            catch (IOException iOException) {
                LOGGER.error("Error copying resourcepack file: {}", (Object)iOException.getMessage());
                this.error = true;
            }
        }
    }
}

