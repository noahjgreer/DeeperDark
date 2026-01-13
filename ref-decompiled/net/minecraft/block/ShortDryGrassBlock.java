/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.DryVegetationBlock
 *  net.minecraft.block.Fertilizable
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.ShortDryGrassBlock
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.AmbientDesertBlockSounds
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DryVegetationBlock;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.ShapeContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.AmbientDesertBlockSounds;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class ShortDryGrassBlock
extends DryVegetationBlock
implements Fertilizable {
    public static final MapCodec<ShortDryGrassBlock> CODEC = ShortDryGrassBlock.createCodec(ShortDryGrassBlock::new);
    private static final VoxelShape SHAPE = Block.createColumnShape((double)12.0, (double)0.0, (double)10.0);

    public MapCodec<ShortDryGrassBlock> getCodec() {
        return CODEC;
    }

    public ShortDryGrassBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        AmbientDesertBlockSounds.tryPlayDryGrassSounds((World)world, (BlockPos)pos, (Random)random);
    }

    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return true;
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        world.setBlockState(pos, Blocks.TALL_DRY_GRASS.getDefaultState());
    }
}

