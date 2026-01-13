/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.color.world.BiomeColors
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.BlockRenderView
 *  net.minecraft.world.biome.Biome
 *  net.minecraft.world.biome.ColorResolver
 */
package net.minecraft.client.color.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.ColorResolver;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class BiomeColors {
    public static final ColorResolver GRASS_COLOR = Biome::getGrassColorAt;
    public static final ColorResolver FOLIAGE_COLOR = (biome, x, z) -> biome.getFoliageColor();
    public static final ColorResolver DRY_FOLIAGE_COLOR = (biome, x, z) -> biome.getDryFoliageColor();
    public static final ColorResolver WATER_COLOR = (biome, x, z) -> biome.getWaterColor();

    private static int getColor(BlockRenderView world, BlockPos pos, ColorResolver resolver) {
        return world.getColor(pos, resolver);
    }

    public static int getGrassColor(BlockRenderView world, BlockPos pos) {
        return BiomeColors.getColor((BlockRenderView)world, (BlockPos)pos, (ColorResolver)GRASS_COLOR);
    }

    public static int getFoliageColor(BlockRenderView world, BlockPos pos) {
        return BiomeColors.getColor((BlockRenderView)world, (BlockPos)pos, (ColorResolver)FOLIAGE_COLOR);
    }

    public static int getDryFoliageColor(BlockRenderView world, BlockPos pos) {
        return BiomeColors.getColor((BlockRenderView)world, (BlockPos)pos, (ColorResolver)DRY_FOLIAGE_COLOR);
    }

    public static int getWaterColor(BlockRenderView world, BlockPos pos) {
        return BiomeColors.getColor((BlockRenderView)world, (BlockPos)pos, (ColorResolver)WATER_COLOR);
    }
}

