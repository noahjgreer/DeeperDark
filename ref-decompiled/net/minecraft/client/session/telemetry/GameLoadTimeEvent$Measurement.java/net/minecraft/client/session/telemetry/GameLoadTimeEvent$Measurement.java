/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.session.telemetry;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public record GameLoadTimeEvent.Measurement(int millis) {
    public static final Codec<GameLoadTimeEvent.Measurement> CODEC = Codec.INT.xmap(GameLoadTimeEvent.Measurement::new, measurement -> measurement.millis);
}
