/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Fertilizable
 *  net.minecraft.block.MushroomPlantBlock
 *  net.minecraft.block.PlantBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.RegistryKeys
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.StructureWorldAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.gen.feature.ConfiguredFeature
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.feature.ConfiguredFeature;

/*
 * Exception performing whole class analysis ignored.
 */
public class MushroomPlantBlock
extends PlantBlock
implements Fertilizable {
    public static final MapCodec<MushroomPlantBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)RegistryKey.createCodec((RegistryKey)RegistryKeys.CONFIGURED_FEATURE).fieldOf("feature").forGetter(block -> block.featureKey), (App)MushroomPlantBlock.createSettingsCodec()).apply((Applicative)instance, MushroomPlantBlock::new));
    private static final VoxelShape SHAPE = Block.createColumnShape((double)6.0, (double)0.0, (double)6.0);
    private final RegistryKey<ConfiguredFeature<?, ?>> featureKey;

    public MapCodec<MushroomPlantBlock> getCodec() {
        return CODEC;
    }

    public MushroomPlantBlock(RegistryKey<ConfiguredFeature<?, ?>> featureKey, AbstractBlock.Settings settings) {
        super(settings);
        this.featureKey = featureKey;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (random.nextInt(25) == 0) {
            int i = 5;
            int j = 4;
            for (BlockPos blockPos : BlockPos.iterate((BlockPos)pos.add(-4, -1, -4), (BlockPos)pos.add(4, 1, 4))) {
                if (!world.getBlockState(blockPos).isOf((Block)this) || --i > 0) continue;
                return;
            }
            BlockPos blockPos2 = pos.add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
            for (int k = 0; k < 4; ++k) {
                if (world.isAir(blockPos2) && state.canPlaceAt((WorldView)world, blockPos2)) {
                    pos = blockPos2;
                }
                blockPos2 = pos.add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
            }
            if (world.isAir(blockPos2) && state.canPlaceAt((WorldView)world, blockPos2)) {
                world.setBlockState(blockPos2, state, 2);
            }
        }
    }

    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isOpaqueFullCube();
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.isIn(BlockTags.MUSHROOM_GROW_BLOCK)) {
            return true;
        }
        return world.getBaseLightLevel(pos, 0) < 13 && this.canPlantOnTop(blockState, (BlockView)world, blockPos);
    }

    public boolean trySpawningBigMushroom(ServerWorld world, BlockPos pos, BlockState state, Random random) {
        Optional optional = world.getRegistryManager().getOrThrow(RegistryKeys.CONFIGURED_FEATURE).getOptional(this.featureKey);
        if (optional.isEmpty()) {
            return false;
        }
        world.removeBlock(pos, false);
        if (((ConfiguredFeature)((RegistryEntry)optional.get()).value()).generate((StructureWorldAccess)world, world.getChunkManager().getChunkGenerator(), random, pos)) {
            return true;
        }
        world.setBlockState(pos, state, 3);
        return false;
    }

    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return true;
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return (double)random.nextFloat() < 0.4;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        this.trySpawningBigMushroom(world, pos, state, random);
    }
}

