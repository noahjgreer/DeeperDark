/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
static class BlockModelRenderer.LightmapCache {
    public final BlockPos.Mutable pos = new BlockPos.Mutable();
    public boolean field_58160;
    public boolean field_58161;
    public final float[] fs = new float[4];
    public final int[] is = new int[4];
    public int lastTintIndex = -1;
    public int colorOfLastTintIndex;
    public final BlockModelRenderer.BrightnessCache brightnessCache = BRIGHTNESS_CACHE.get();

    BlockModelRenderer.LightmapCache() {
    }
}
