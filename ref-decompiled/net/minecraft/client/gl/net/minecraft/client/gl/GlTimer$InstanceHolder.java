/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlTimer;

@Environment(value=EnvType.CLIENT)
static class GlTimer.InstanceHolder {
    static final GlTimer INSTANCE = GlTimer.InstanceHolder.create();

    private GlTimer.InstanceHolder() {
    }

    private static GlTimer create() {
        return new GlTimer();
    }
}
