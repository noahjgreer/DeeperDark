/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Frustum;
import net.minecraft.world.debug.DebugDataStore;

@Environment(value=EnvType.CLIENT)
public static interface DebugRenderer.Renderer {
    public void render(double var1, double var3, double var5, DebugDataStore var7, Frustum var8, float var9);
}
