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
 *  net.minecraft.block.FluidFillable
 *  net.minecraft.block.PlantBlock
 *  net.minecraft.block.SeagrassBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.TallSeagrassBlock
 *  net.minecraft.block.enums.DoubleBlockHalf
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.registry.tag.FluidTags
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TallSeagrassBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

public class SeagrassBlock
extends PlantBlock
implements Fertilizable,
FluidFillable {
    public static final MapCodec<SeagrassBlock> CODEC = SeagrassBlock.createCodec(SeagrassBlock::new);
    private static final VoxelShape SHAPE = Block.createColumnShape((double)12.0, (double)0.0, (double)12.0);

    public MapCodec<SeagrassBlock> getCodec() {
        return CODEC;
    }

    public SeagrassBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isSideSolidFullSquare(world, pos, Direction.UP) && !floor.isOf(Blocks.MAGMA_BLOCK);
    }

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        if (fluidState.isIn(FluidTags.WATER) && fluidState.getLevel() == 8) {
            return super.getPlacementState(ctx);
        }
        return null;
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        BlockState blockState = super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
        if (!blockState.isAir()) {
            tickView.scheduleFluidTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return blockState;
    }

    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return world.getBlockState(pos.up()).isOf(Blocks.WATER);
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    protected FluidState getFluidState(BlockState state) {
        return Fluids.WATER.getStill(false);
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        BlockState blockState = Blocks.TALL_SEAGRASS.getDefaultState();
        BlockState blockState2 = (BlockState)blockState.with((Property)TallSeagrassBlock.HALF, (Comparable)DoubleBlockHalf.UPPER);
        BlockPos blockPos = pos.up();
        world.setBlockState(pos, blockState, 2);
        world.setBlockState(blockPos, blockState2, 2);
    }

    public boolean canFillWithFluid(@Nullable LivingEntity filler, BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        return false;
    }

    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        return false;
    }
}

