/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Fertilizable
 *  net.minecraft.block.PlantBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.ShortPlantBlock
 *  net.minecraft.block.TallPlantBlock
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldAccess
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
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

/*
 * Exception performing whole class analysis ignored.
 */
public class ShortPlantBlock
extends PlantBlock
implements Fertilizable {
    public static final MapCodec<ShortPlantBlock> CODEC = ShortPlantBlock.createCodec(ShortPlantBlock::new);
    private static final VoxelShape SHAPE = Block.createColumnShape((double)12.0, (double)0.0, (double)13.0);

    public MapCodec<ShortPlantBlock> getCodec() {
        return CODEC;
    }

    public ShortPlantBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return ShortPlantBlock.getLargeVariant((BlockState)state).getDefaultState().canPlaceAt(world, pos) && world.isAir(pos.up());
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        TallPlantBlock.placeAt((WorldAccess)world, (BlockState)ShortPlantBlock.getLargeVariant((BlockState)state).getDefaultState(), (BlockPos)pos, (int)2);
    }

    private static TallPlantBlock getLargeVariant(BlockState state) {
        return (TallPlantBlock)(state.isOf(Blocks.FERN) ? Blocks.LARGE_FERN : Blocks.TALL_GRASS);
    }
}

