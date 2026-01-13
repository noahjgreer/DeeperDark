/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.session.telemetry.TelemetryEventProperty
 *  net.minecraft.client.session.telemetry.TelemetryEventType
 *  net.minecraft.client.session.telemetry.TelemetrySender
 *  net.minecraft.client.session.telemetry.WorldLoadTimesEvent
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.session.telemetry;

import java.time.Duration;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.telemetry.TelemetryEventProperty;
import net.minecraft.client.session.telemetry.TelemetryEventType;
import net.minecraft.client.session.telemetry.TelemetrySender;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class WorldLoadTimesEvent {
    private final boolean newWorld;
    private final @Nullable Duration worldLoadTime;

    public WorldLoadTimesEvent(boolean newWorld, @Nullable Duration worldLoadTime) {
        this.worldLoadTime = worldLoadTime;
        this.newWorld = newWorld;
    }

    public void send(TelemetrySender sender) {
        if (this.worldLoadTime != null) {
            sender.send(TelemetryEventType.WORLD_LOAD_TIMES, builder -> {
                builder.put(TelemetryEventProperty.WORLD_LOAD_TIME_MS, (Object)((int)this.worldLoadTime.toMillis()));
                builder.put(TelemetryEventProperty.NEW_WORLD, (Object)this.newWorld);
            });
        }
    }
}

