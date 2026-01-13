/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.SlabBlock
 *  net.minecraft.block.SlabBlock$1
 *  net.minecraft.block.Waterloggable
 *  net.minecraft.block.enums.SlabType
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.tag.FluidTags
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
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
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

public class SlabBlock
extends Block
implements Waterloggable {
    public static final MapCodec<SlabBlock> CODEC = SlabBlock.createCodec(SlabBlock::new);
    public static final EnumProperty<SlabType> TYPE = Properties.SLAB_TYPE;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private static final VoxelShape BOTTOM_SHAPE = Block.createColumnShape((double)16.0, (double)0.0, (double)8.0);
    private static final VoxelShape TOP_SHAPE = Block.createColumnShape((double)16.0, (double)8.0, (double)16.0);

    public MapCodec<? extends SlabBlock> getCodec() {
        return CODEC;
    }

    public SlabBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.getDefaultState().with((Property)TYPE, (Comparable)SlabType.BOTTOM)).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    protected boolean hasSidedTransparency(BlockState state) {
        return state.get((Property)TYPE) != SlabType.DOUBLE;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{TYPE, WATERLOGGED});
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (1.field_11504[((SlabType)state.get((Property)TYPE)).ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> TOP_SHAPE;
            case 2 -> BOTTOM_SHAPE;
            case 3 -> VoxelShapes.fullCube();
        };
    }

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        BlockState blockState = ctx.getWorld().getBlockState(blockPos);
        if (blockState.isOf((Block)this)) {
            return (BlockState)((BlockState)blockState.with((Property)TYPE, (Comparable)SlabType.DOUBLE)).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false));
        }
        FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
        BlockState blockState2 = (BlockState)((BlockState)this.getDefaultState().with((Property)TYPE, (Comparable)SlabType.BOTTOM)).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
        Direction direction = ctx.getSide();
        if (direction == Direction.DOWN || direction != Direction.UP && ctx.getHitPos().y - (double)blockPos.getY() > 0.5) {
            return (BlockState)blockState2.with((Property)TYPE, (Comparable)SlabType.TOP);
        }
        return blockState2;
    }

    protected boolean canReplace(BlockState state, ItemPlacementContext context) {
        ItemStack itemStack = context.getStack();
        SlabType slabType = (SlabType)state.get((Property)TYPE);
        if (slabType == SlabType.DOUBLE || !itemStack.isOf(this.asItem())) {
            return false;
        }
        if (context.canReplaceExisting()) {
            boolean bl = context.getHitPos().y - (double)context.getBlockPos().getY() > 0.5;
            Direction direction = context.getSide();
            if (slabType == SlabType.BOTTOM) {
                return direction == Direction.UP || bl && direction.getAxis().isHorizontal();
            }
            return direction == Direction.DOWN || !bl && direction.getAxis().isHorizontal();
        }
        return true;
    }

    protected FluidState getFluidState(BlockState state) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (state.get((Property)TYPE) != SlabType.DOUBLE) {
            return super.tryFillWithFluid(world, pos, state, fluidState);
        }
        return false;
    }

    public boolean canFillWithFluid(@Nullable LivingEntity filler, BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        if (state.get((Property)TYPE) != SlabType.DOUBLE) {
            return super.canFillWithFluid(filler, world, pos, state, fluid);
        }
        return false;
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            tickView.scheduleFluidTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        switch (1.field_11503[type.ordinal()]) {
            case 1: {
                return false;
            }
            case 2: {
                return state.getFluidState().isIn(FluidTags.WATER);
            }
            case 3: {
                return false;
            }
        }
        return false;
    }
}

