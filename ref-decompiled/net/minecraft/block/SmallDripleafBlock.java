/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.BigDripleafBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Fertilizable
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.SmallDripleafBlock
 *  net.minecraft.block.TallPlantBlock
 *  net.minecraft.block.Waterloggable
 *  net.minecraft.block.enums.DoubleBlockHalf
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
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
import net.minecraft.block.BigDripleafBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
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

/*
 * Exception performing whole class analysis ignored.
 */
public class SmallDripleafBlock
extends TallPlantBlock
implements Fertilizable,
Waterloggable {
    public static final MapCodec<SmallDripleafBlock> CODEC = SmallDripleafBlock.createCodec(SmallDripleafBlock::new);
    private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE = Block.createColumnShape((double)12.0, (double)0.0, (double)13.0);

    public MapCodec<SmallDripleafBlock> getCodec() {
        return CODEC;
    }

    public SmallDripleafBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)HALF, (Comparable)DoubleBlockHalf.LOWER)).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false))).with((Property)FACING, (Comparable)Direction.NORTH));
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isIn(BlockTags.SMALL_DRIPLEAF_PLACEABLE) || world.getFluidState(pos.up()).isEqualAndStill((Fluid)Fluids.WATER) && super.canPlantOnTop(floor, world, pos);
    }

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = super.getPlacementState(ctx);
        if (blockState != null) {
            return SmallDripleafBlock.withWaterloggedState((WorldView)ctx.getWorld(), (BlockPos)ctx.getBlockPos(), (BlockState)((BlockState)blockState.with((Property)FACING, (Comparable)ctx.getHorizontalPlayerFacing().getOpposite())));
        }
        return null;
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient()) {
            BlockPos blockPos = pos.up();
            BlockState blockState = TallPlantBlock.withWaterloggedState((WorldView)world, (BlockPos)blockPos, (BlockState)((BlockState)((BlockState)this.getDefaultState().with((Property)HALF, (Comparable)DoubleBlockHalf.UPPER)).with((Property)FACING, (Comparable)((Direction)state.get((Property)FACING)))));
            world.setBlockState(blockPos, blockState, 3);
        }
    }

    protected FluidState getFluidState(BlockState state) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (state.get((Property)HALF) == DoubleBlockHalf.UPPER) {
            return super.canPlaceAt(state, world, pos);
        }
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        return this.canPlantOnTop(blockState, (BlockView)world, blockPos);
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            tickView.scheduleFluidTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{HALF, WATERLOGGED, FACING});
    }

    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return true;
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        if (state.get((Property)TallPlantBlock.HALF) == DoubleBlockHalf.LOWER) {
            BlockPos blockPos = pos.up();
            world.setBlockState(blockPos, world.getFluidState(blockPos).getBlockState(), 18);
            BigDripleafBlock.grow((WorldAccess)world, (Random)random, (BlockPos)pos, (Direction)((Direction)state.get((Property)FACING)));
        } else {
            BlockPos blockPos = pos.down();
            this.grow(world, random, blockPos, world.getBlockState(blockPos));
        }
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with((Property)FACING, (Comparable)rotation.rotate((Direction)state.get((Property)FACING)));
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get((Property)FACING)));
    }

    protected float getVerticalModelOffsetMultiplier() {
        return 0.1f;
    }
}

