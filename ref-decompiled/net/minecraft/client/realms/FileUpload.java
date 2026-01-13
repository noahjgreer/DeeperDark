/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.FileUpload
 *  net.minecraft.client.realms.FileUpload$ProgressInputStream
 *  net.minecraft.client.realms.dto.UploadInfo
 *  net.minecraft.client.realms.util.UploadProgress
 *  net.minecraft.client.realms.util.UploadResult
 *  net.minecraft.client.session.Session
 *  net.minecraft.util.LenientJsonParser
 *  net.minecraft.util.Util
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.FileUpload;
import net.minecraft.client.realms.dto.UploadInfo;
import net.minecraft.client.realms.util.UploadProgress;
import net.minecraft.client.realms.util.UploadResult;
import net.minecraft.client.session.Session;
import net.minecraft.util.LenientJsonParser;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class FileUpload
implements AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_ATTEMPTS = 5;
    private static final String UPLOAD_ENDPOINT = "/upload";
    private final File file;
    private final long worldId;
    private final int slotId;
    private final UploadInfo uploadInfo;
    private final String sessionId;
    private final String username;
    private final String clientVersion;
    private final String worldVersion;
    private final UploadProgress uploadStatus;
    private final HttpClient httpClient;

    public FileUpload(File file, long worldId, int slotId, UploadInfo uploadInfo, Session session, String clientVersion, String worldVersion, UploadProgress uploadStatus) {
        this.file = file;
        this.worldId = worldId;
        this.slotId = slotId;
        this.uploadInfo = uploadInfo;
        this.sessionId = session.getSessionId();
        this.username = session.getUsername();
        this.clientVersion = clientVersion;
        this.worldVersion = worldVersion;
        this.uploadStatus = uploadStatus;
        this.httpClient = HttpClient.newBuilder().executor((Executor)Util.getDownloadWorkerExecutor()).connectTimeout(Duration.ofSeconds(15L)).build();
    }

    @Override
    public void close() {
        this.httpClient.close();
    }

    public CompletableFuture<UploadResult> upload() {
        long l = this.file.length();
        this.uploadStatus.setTotalBytes(l);
        return this.requestUpload(0, l);
    }

    private CompletableFuture<UploadResult> requestUpload(int currentAttempt, long size) {
        HttpRequest.BodyPublisher bodyPublisher = FileUpload.getPublisher(() -> {
            try {
                return new ProgressInputStream((InputStream)new FileInputStream(this.file), this.uploadStatus);
            }
            catch (IOException iOException) {
                LOGGER.warn("Failed to open file {}", (Object)this.file, (Object)iOException);
                return null;
            }
        }, (long)size);
        HttpRequest httpRequest = HttpRequest.newBuilder(this.uploadInfo.uploadEndpoint().resolve("/upload/" + this.worldId + "/" + this.slotId)).timeout(Duration.ofMinutes(10L)).setHeader("Cookie", this.getCookie()).setHeader("Content-Type", "application/octet-stream").POST(bodyPublisher).build();
        return this.httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).thenCompose(response -> {
            long m = this.getRetryDelaySeconds(response);
            if (this.shouldRetry(m, currentAttempt)) {
                this.uploadStatus.clear();
                try {
                    Thread.sleep(Duration.ofSeconds(m));
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                return this.requestUpload(currentAttempt + 1, size);
            }
            return CompletableFuture.completedFuture(this.handleResponse(response));
        });
    }

    private static HttpRequest.BodyPublisher getPublisher(Supplier<@Nullable InputStream> inputSupplier, long size) {
        return HttpRequest.BodyPublishers.fromPublisher(HttpRequest.BodyPublishers.ofInputStream(inputSupplier), size);
    }

    private String getCookie() {
        return "sid=" + this.sessionId + ";token=" + this.uploadInfo.token() + ";user=" + this.username + ";version=" + this.clientVersion + ";worldVersion=" + this.worldVersion;
    }

    private UploadResult handleResponse(HttpResponse<String> response) {
        int i = response.statusCode();
        if (i == 401) {
            LOGGER.debug("Realms server returned 401: {}", response.headers().firstValue("WWW-Authenticate"));
        }
        String string = null;
        String string2 = response.body();
        if (string2 != null && !string2.isBlank()) {
            try {
                JsonElement jsonElement = LenientJsonParser.parse((String)string2).getAsJsonObject().get("errorMsg");
                if (jsonElement != null) {
                    string = jsonElement.getAsString();
                }
            }
            catch (Exception exception) {
                LOGGER.warn("Failed to parse response {}", (Object)string2, (Object)exception);
            }
        }
        return new UploadResult(i, string);
    }

    private boolean shouldRetry(long retryDelaySeconds, int currentAttempt) {
        return retryDelaySeconds > 0L && currentAttempt + 1 < 5;
    }

    private long getRetryDelaySeconds(HttpResponse<?> response) {
        return response.headers().firstValueAsLong("Retry-After").orElse(0L);
    }
}

