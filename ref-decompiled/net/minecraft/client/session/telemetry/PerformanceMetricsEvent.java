/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  it.unimi.dsi.fastutil.longs.LongList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.session.telemetry.PerformanceMetricsEvent
 *  net.minecraft.client.session.telemetry.SampleEvent
 *  net.minecraft.client.session.telemetry.TelemetryEventProperty
 *  net.minecraft.client.session.telemetry.TelemetryEventType
 *  net.minecraft.client.session.telemetry.TelemetrySender
 */
package net.minecraft.client.session.telemetry;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.telemetry.SampleEvent;
import net.minecraft.client.session.telemetry.TelemetryEventProperty;
import net.minecraft.client.session.telemetry.TelemetryEventType;
import net.minecraft.client.session.telemetry.TelemetrySender;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public final class PerformanceMetricsEvent
extends SampleEvent {
    private static final long MAX_MEMORY_KB = PerformanceMetricsEvent.toKilos((long)Runtime.getRuntime().maxMemory());
    private final LongList fpsSamples = new LongArrayList();
    private final LongList renderTimeSamples = new LongArrayList();
    private final LongList usedMemorySamples = new LongArrayList();

    public void tick(TelemetrySender sender) {
        if (MinecraftClient.getInstance().isOptionalTelemetryEnabled()) {
            super.tick(sender);
        }
    }

    private void clearSamples() {
        this.fpsSamples.clear();
        this.renderTimeSamples.clear();
        this.usedMemorySamples.clear();
    }

    public void sample() {
        this.fpsSamples.add((long)MinecraftClient.getInstance().getCurrentFps());
        this.sampleUsedMemory();
        this.renderTimeSamples.add(MinecraftClient.getInstance().getRenderTime());
    }

    private void sampleUsedMemory() {
        long l = Runtime.getRuntime().totalMemory();
        long m = Runtime.getRuntime().freeMemory();
        long n = l - m;
        this.usedMemorySamples.add(PerformanceMetricsEvent.toKilos((long)n));
    }

    public void send(TelemetrySender sender) {
        sender.send(TelemetryEventType.PERFORMANCE_METRICS, map -> {
            map.put(TelemetryEventProperty.FRAME_RATE_SAMPLES, (Object)new LongArrayList(this.fpsSamples));
            map.put(TelemetryEventProperty.RENDER_TIME_SAMPLES, (Object)new LongArrayList(this.renderTimeSamples));
            map.put(TelemetryEventProperty.USED_MEMORY_SAMPLES, (Object)new LongArrayList(this.usedMemorySamples));
            map.put(TelemetryEventProperty.NUMBER_OF_SAMPLES, (Object)this.getSampleCount());
            map.put(TelemetryEventProperty.RENDER_DISTANCE, (Object)MinecraftClient.getInstance().options.getClampedViewDistance());
            map.put(TelemetryEventProperty.DEDICATED_MEMORY_KB, (Object)((int)MAX_MEMORY_KB));
        });
        this.clearSamples();
    }

    private static long toKilos(long bytes) {
        return bytes / 1000L;
    }
}

