/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.TelemetryPropertyContainer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.session.telemetry;

import com.mojang.authlib.minecraft.TelemetryPropertyContainer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static interface TelemetryEventProperty.PropertyExporter<T> {
    public void apply(TelemetryPropertyContainer var1, String var2, T var3);
}
