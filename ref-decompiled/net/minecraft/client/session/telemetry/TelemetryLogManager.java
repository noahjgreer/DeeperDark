/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.session.telemetry.TelemetryLogManager
 *  net.minecraft.client.session.telemetry.TelemetryLogger
 *  net.minecraft.client.session.telemetry.ThreadedLogWriter
 *  net.minecraft.util.Util
 *  net.minecraft.util.logging.LogFileCompressor
 *  net.minecraft.util.logging.LogFileCompressor$Uncompressed
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.session.telemetry;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.telemetry.TelemetryLogger;
import net.minecraft.client.session.telemetry.ThreadedLogWriter;
import net.minecraft.util.Util;
import net.minecraft.util.logging.LogFileCompressor;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class TelemetryLogManager
implements AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String FILE_EXTENSION = ".json";
    private static final int RETENTION_DAYS = 7;
    private final LogFileCompressor compressor;
    private @Nullable CompletableFuture<Optional<ThreadedLogWriter>> writer;

    private TelemetryLogManager(LogFileCompressor compressor) {
        this.compressor = compressor;
    }

    public static CompletableFuture<Optional<TelemetryLogManager>> create(Path directory) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                LogFileCompressor logFileCompressor = LogFileCompressor.create((Path)directory, (String)FILE_EXTENSION);
                logFileCompressor.getAll().removeExpired(LocalDate.now(Clock.systemDefaultZone()), 7).compressAll();
                return Optional.of(new TelemetryLogManager(logFileCompressor));
            }
            catch (Exception exception) {
                LOGGER.error("Failed to create telemetry log manager", (Throwable)exception);
                return Optional.empty();
            }
        }, (Executor)Util.getMainWorkerExecutor());
    }

    public CompletableFuture<Optional<TelemetryLogger>> getLogger() {
        if (this.writer == null) {
            this.writer = CompletableFuture.supplyAsync(() -> {
                try {
                    LogFileCompressor.Uncompressed uncompressed = this.compressor.createLogFile(LocalDate.now(Clock.systemDefaultZone()));
                    FileChannel fileChannel = uncompressed.open();
                    return Optional.of(new ThreadedLogWriter(fileChannel, (Executor)Util.getMainWorkerExecutor()));
                }
                catch (IOException iOException) {
                    LOGGER.error("Failed to open channel for telemetry event log", (Throwable)iOException);
                    return Optional.empty();
                }
            }, (Executor)Util.getMainWorkerExecutor());
        }
        return this.writer.thenApply(writer -> writer.map(ThreadedLogWriter::getLogger));
    }

    @Override
    public void close() {
        if (this.writer != null) {
            this.writer.thenAccept(writer -> writer.ifPresent(ThreadedLogWriter::close));
        }
    }
}

