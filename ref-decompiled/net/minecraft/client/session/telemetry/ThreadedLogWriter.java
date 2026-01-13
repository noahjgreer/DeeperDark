/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.session.telemetry.SentTelemetryEvent
 *  net.minecraft.client.session.telemetry.TelemetryLogger
 *  net.minecraft.client.session.telemetry.ThreadedLogWriter
 *  net.minecraft.util.logging.LogWriter
 *  net.minecraft.util.thread.SimpleConsecutiveExecutor
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 */
package net.minecraft.client.session.telemetry;

import com.mojang.logging.LogUtils;
import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.telemetry.SentTelemetryEvent;
import net.minecraft.client.session.telemetry.TelemetryLogger;
import net.minecraft.util.logging.LogWriter;
import net.minecraft.util.thread.SimpleConsecutiveExecutor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ThreadedLogWriter
implements AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final LogWriter<SentTelemetryEvent> writer;
    private final SimpleConsecutiveExecutor executor;

    public ThreadedLogWriter(FileChannel channel, Executor executor) {
        this.writer = new LogWriter(SentTelemetryEvent.CODEC, channel);
        this.executor = new SimpleConsecutiveExecutor(executor, "telemetry-event-log");
    }

    public TelemetryLogger getLogger() {
        return event -> this.executor.send(() -> {
            try {
                this.writer.write((Object)event);
            }
            catch (IOException iOException) {
                LOGGER.error("Failed to write telemetry event to log", (Throwable)iOException);
            }
        });
    }

    @Override
    public void close() {
        this.executor.send(() -> IOUtils.closeQuietly((Closeable)this.writer));
        this.executor.close();
    }
}

