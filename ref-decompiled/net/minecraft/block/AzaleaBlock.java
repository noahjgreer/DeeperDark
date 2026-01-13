/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.AzaleaBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Fertilizable
 *  net.minecraft.block.PlantBlock
 *  net.minecraft.block.SaplingGenerator
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
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
import net.minecraft.block.Fertilizable;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.SaplingGenerator;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class AzaleaBlock
extends PlantBlock
implements Fertilizable {
    public static final MapCodec<AzaleaBlock> CODEC = AzaleaBlock.createCodec(AzaleaBlock::new);
    private static final VoxelShape SHAPE = VoxelShapes.union((VoxelShape)Block.createColumnShape((double)16.0, (double)8.0, (double)16.0), (VoxelShape)Block.createColumnShape((double)4.0, (double)0.0, (double)8.0));

    public MapCodec<AzaleaBlock> getCodec() {
        return CODEC;
    }

    public AzaleaBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isOf(Blocks.CLAY) || super.canPlantOnTop(floor, world, pos);
    }

    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return world.getFluidState(pos.up()).isEmpty();
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return (double)world.random.nextFloat() < 0.45;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        SaplingGenerator.AZALEA.generate(world, world.getChunkManager().getChunkGenerator(), pos, state, random);
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
}

