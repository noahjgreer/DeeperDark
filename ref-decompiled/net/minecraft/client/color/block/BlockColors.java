/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.MapColor
 *  net.minecraft.block.RedstoneWireBlock
 *  net.minecraft.block.StemBlock
 *  net.minecraft.block.TallPlantBlock
 *  net.minecraft.block.enums.DoubleBlockHalf
 *  net.minecraft.client.color.block.BlockColorProvider
 *  net.minecraft.client.color.block.BlockColors
 *  net.minecraft.client.color.world.BiomeColors
 *  net.minecraft.registry.Registries
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.collection.IdList
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.world.BlockRenderView
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.biome.GrassColors
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.color.block;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Property;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.biome.GrassColors;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BlockColors {
    private static final int NO_COLOR = -1;
    public static final int PLACED_LILY_PAD = -14647248;
    public static final int LILY_PAD = -9321636;
    private final IdList<BlockColorProvider> providers = new IdList(32);
    private final Map<Block, Set<Property<?>>> properties = Maps.newHashMap();

    public static BlockColors create() {
        BlockColors blockColors = new BlockColors();
        blockColors.registerColorProvider((state, world, pos, tintIndex) -> {
            if (world == null || pos == null) {
                return GrassColors.getDefaultColor();
            }
            return BiomeColors.getGrassColor((BlockRenderView)world, (BlockPos)(state.get((Property)TallPlantBlock.HALF) == DoubleBlockHalf.UPPER ? pos.down() : pos));
        }, new Block[]{Blocks.LARGE_FERN, Blocks.TALL_GRASS});
        blockColors.registerColorProperty((Property)TallPlantBlock.HALF, new Block[]{Blocks.LARGE_FERN, Blocks.TALL_GRASS});
        blockColors.registerColorProvider((state, world, pos, tintIndex) -> {
            if (world == null || pos == null) {
                return GrassColors.getDefaultColor();
            }
            return BiomeColors.getGrassColor((BlockRenderView)world, (BlockPos)pos);
        }, new Block[]{Blocks.GRASS_BLOCK, Blocks.FERN, Blocks.SHORT_GRASS, Blocks.POTTED_FERN, Blocks.BUSH});
        blockColors.registerColorProvider((state, world, pos, tintIndex) -> {
            if (tintIndex != 0) {
                if (world == null || pos == null) {
                    return GrassColors.getDefaultColor();
                }
                return BiomeColors.getGrassColor((BlockRenderView)world, (BlockPos)pos);
            }
            return -1;
        }, new Block[]{Blocks.PINK_PETALS, Blocks.WILDFLOWERS});
        blockColors.registerColorProvider((state, world, pos, tintIndex) -> -10380959, new Block[]{Blocks.SPRUCE_LEAVES});
        blockColors.registerColorProvider((state, world, pos, tintIndex) -> -8345771, new Block[]{Blocks.BIRCH_LEAVES});
        blockColors.registerColorProvider((state, world, pos, tintIndex) -> {
            if (world == null || pos == null) {
                return -12012264;
            }
            return BiomeColors.getFoliageColor((BlockRenderView)world, (BlockPos)pos);
        }, new Block[]{Blocks.OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.VINE, Blocks.MANGROVE_LEAVES});
        blockColors.registerColorProvider((state, world, pos, tintIndex) -> {
            if (world == null || pos == null) {
                return -10732494;
            }
            return BiomeColors.getDryFoliageColor((BlockRenderView)world, (BlockPos)pos);
        }, new Block[]{Blocks.LEAF_LITTER});
        blockColors.registerColorProvider((state, world, pos, tintIndex) -> {
            if (world == null || pos == null) {
                return -1;
            }
            return BiomeColors.getWaterColor((BlockRenderView)world, (BlockPos)pos);
        }, new Block[]{Blocks.WATER, Blocks.BUBBLE_COLUMN, Blocks.WATER_CAULDRON});
        blockColors.registerColorProvider((state, world, pos, tintIndex) -> RedstoneWireBlock.getWireColor((int)((Integer)state.get((Property)RedstoneWireBlock.POWER))), new Block[]{Blocks.REDSTONE_WIRE});
        blockColors.registerColorProperty((Property)RedstoneWireBlock.POWER, new Block[]{Blocks.REDSTONE_WIRE});
        blockColors.registerColorProvider((state, world, pos, tintIndex) -> {
            if (world == null || pos == null) {
                return -1;
            }
            return BiomeColors.getGrassColor((BlockRenderView)world, (BlockPos)pos);
        }, new Block[]{Blocks.SUGAR_CANE});
        blockColors.registerColorProvider((state, world, pos, tintIndex) -> -2046180, new Block[]{Blocks.ATTACHED_MELON_STEM, Blocks.ATTACHED_PUMPKIN_STEM});
        blockColors.registerColorProvider((state, world, pos, tintIndex) -> {
            int i = (Integer)state.get((Property)StemBlock.AGE);
            return ColorHelper.getArgb((int)(i * 32), (int)(255 - i * 8), (int)(i * 4));
        }, new Block[]{Blocks.MELON_STEM, Blocks.PUMPKIN_STEM});
        blockColors.registerColorProperty((Property)StemBlock.AGE, new Block[]{Blocks.MELON_STEM, Blocks.PUMPKIN_STEM});
        blockColors.registerColorProvider((state, world, pos, tintIndex) -> {
            if (world == null || pos == null) {
                return -9321636;
            }
            return -14647248;
        }, new Block[]{Blocks.LILY_PAD});
        return blockColors;
    }

    public int getParticleColor(BlockState state, World world, BlockPos pos) {
        BlockColorProvider blockColorProvider = (BlockColorProvider)this.providers.get(Registries.BLOCK.getRawId((Object)state.getBlock()));
        if (blockColorProvider != null) {
            return blockColorProvider.getColor(state, null, null, 0);
        }
        MapColor mapColor = state.getMapColor((BlockView)world, pos);
        return mapColor != null ? mapColor.color : -1;
    }

    public int getColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tintIndex) {
        BlockColorProvider blockColorProvider = (BlockColorProvider)this.providers.get(Registries.BLOCK.getRawId((Object)state.getBlock()));
        return blockColorProvider == null ? -1 : blockColorProvider.getColor(state, world, pos, tintIndex);
    }

    public void registerColorProvider(BlockColorProvider provider, Block ... blocks) {
        for (Block block : blocks) {
            this.providers.set((Object)provider, Registries.BLOCK.getRawId((Object)block));
        }
    }

    private void registerColorProperties(Set<Property<?>> properties, Block ... blocks) {
        for (Block block : blocks) {
            this.properties.put(block, properties);
        }
    }

    private void registerColorProperty(Property<?> property, Block ... blocks) {
        this.registerColorProperties((Set)ImmutableSet.of(property), blocks);
    }

    public Set<Property<?>> getProperties(Block block) {
        return (Set)this.properties.getOrDefault(block, ImmutableSet.of());
    }
}

