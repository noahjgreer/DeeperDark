/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.decoration.Brightness;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.LightType;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public static interface WorldRenderer.BrightnessGetter {
    public static final WorldRenderer.BrightnessGetter DEFAULT = (world, pos) -> {
        int i = world.getLightLevel(LightType.SKY, pos);
        int j = world.getLightLevel(LightType.BLOCK, pos);
        return Brightness.pack(j, i);
    };

    public int packedBrightness(BlockRenderView var1, BlockPos var2);
}
