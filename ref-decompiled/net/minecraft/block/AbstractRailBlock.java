/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.AbstractRailBlock
 *  net.minecraft.block.AbstractRailBlock$1
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.RailPlacementHelper
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.Waterloggable
 *  net.minecraft.block.enums.RailShape
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.property.BooleanProperty
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
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.block.WireOrientation
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RailPlacementHelper;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.enums.RailShape;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.BooleanProperty;
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
import net.minecraft.world.WorldView;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public abstract class AbstractRailBlock
extends Block
implements Waterloggable {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private static final VoxelShape STRAIGHT_SHAPE = Block.createColumnShape((double)16.0, (double)0.0, (double)2.0);
    private static final VoxelShape ASCENDING_SHAPE = Block.createColumnShape((double)16.0, (double)0.0, (double)8.0);
    private final boolean forbidCurves;

    public static boolean isRail(World world, BlockPos pos) {
        return AbstractRailBlock.isRail((BlockState)world.getBlockState(pos));
    }

    public static boolean isRail(BlockState state) {
        return state.isIn(BlockTags.RAILS) && state.getBlock() instanceof AbstractRailBlock;
    }

    protected AbstractRailBlock(boolean forbidCurves, AbstractBlock.Settings settings) {
        super(settings);
        this.forbidCurves = forbidCurves;
    }

    protected abstract MapCodec<? extends AbstractRailBlock> getCodec();

    public boolean cannotMakeCurves() {
        return this.forbidCurves;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return ((RailShape)state.get(this.getShapeProperty())).isAscending() ? ASCENDING_SHAPE : STRAIGHT_SHAPE;
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return AbstractRailBlock.hasTopRim((BlockView)world, (BlockPos)pos.down());
    }

    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (oldState.isOf(state.getBlock())) {
            return;
        }
        this.updateCurves(state, world, pos, notify);
    }

    protected BlockState updateCurves(BlockState state, World world, BlockPos pos, boolean notify) {
        state = this.updateBlockState(world, pos, state, true);
        if (this.forbidCurves) {
            world.updateNeighbor(state, pos, (Block)this, null, notify);
        }
        return state;
    }

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (world.isClient() || !world.getBlockState(pos).isOf((Block)this)) {
            return;
        }
        RailShape railShape = (RailShape)state.get(this.getShapeProperty());
        if (AbstractRailBlock.shouldDropRail((BlockPos)pos, (World)world, (RailShape)railShape)) {
            AbstractRailBlock.dropStacks((BlockState)state, (World)world, (BlockPos)pos);
            world.removeBlock(pos, notify);
        } else {
            this.updateBlockState(state, world, pos, sourceBlock);
        }
    }

    private static boolean shouldDropRail(BlockPos pos, World world, RailShape shape) {
        if (!AbstractRailBlock.hasTopRim((BlockView)world, (BlockPos)pos.down())) {
            return true;
        }
        switch (1.field_11372[shape.ordinal()]) {
            case 1: {
                return !AbstractRailBlock.hasTopRim((BlockView)world, (BlockPos)pos.east());
            }
            case 2: {
                return !AbstractRailBlock.hasTopRim((BlockView)world, (BlockPos)pos.west());
            }
            case 3: {
                return !AbstractRailBlock.hasTopRim((BlockView)world, (BlockPos)pos.north());
            }
            case 4: {
                return !AbstractRailBlock.hasTopRim((BlockView)world, (BlockPos)pos.south());
            }
        }
        return false;
    }

    protected void updateBlockState(BlockState state, World world, BlockPos pos, Block neighbor) {
    }

    protected BlockState updateBlockState(World world, BlockPos pos, BlockState state, boolean forceUpdate) {
        if (world.isClient()) {
            return state;
        }
        RailShape railShape = (RailShape)state.get(this.getShapeProperty());
        return new RailPlacementHelper(world, pos, state).updateBlockState(world.isReceivingRedstonePower(pos), forceUpdate, railShape).getBlockState();
    }

    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        if (moved) {
            return;
        }
        if (((RailShape)state.get(this.getShapeProperty())).isAscending()) {
            world.updateNeighbors(pos.up(), (Block)this);
        }
        if (this.forbidCurves) {
            world.updateNeighbors(pos, (Block)this);
            world.updateNeighbors(pos.down(), (Block)this);
        }
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean bl = fluidState.getFluid() == Fluids.WATER;
        BlockState blockState = super.getDefaultState();
        Direction direction = ctx.getHorizontalPlayerFacing();
        boolean bl2 = direction == Direction.EAST || direction == Direction.WEST;
        return (BlockState)((BlockState)blockState.with(this.getShapeProperty(), (Comparable)(bl2 ? RailShape.EAST_WEST : RailShape.NORTH_SOUTH))).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(bl));
    }

    public abstract Property<RailShape> getShapeProperty();

    protected RailShape rotateShape(RailShape shape, BlockRotation rotation) {
        return switch (1.field_11371[rotation.ordinal()]) {
            case 1 -> {
                switch (1.field_11372[shape.ordinal()]) {
                    default: {
                        throw new MatchException(null, null);
                    }
                    case 5: {
                        yield RailShape.NORTH_SOUTH;
                    }
                    case 6: {
                        yield RailShape.EAST_WEST;
                    }
                    case 1: {
                        yield RailShape.ASCENDING_WEST;
                    }
                    case 2: {
                        yield RailShape.ASCENDING_EAST;
                    }
                    case 3: {
                        yield RailShape.ASCENDING_SOUTH;
                    }
                    case 4: {
                        yield RailShape.ASCENDING_NORTH;
                    }
                    case 7: {
                        yield RailShape.NORTH_WEST;
                    }
                    case 8: {
                        yield RailShape.NORTH_EAST;
                    }
                    case 9: {
                        yield RailShape.SOUTH_EAST;
                    }
                    case 10: 
                }
                yield RailShape.SOUTH_WEST;
            }
            case 2 -> {
                switch (1.field_11372[shape.ordinal()]) {
                    default: {
                        throw new MatchException(null, null);
                    }
                    case 5: {
                        yield RailShape.EAST_WEST;
                    }
                    case 6: {
                        yield RailShape.NORTH_SOUTH;
                    }
                    case 1: {
                        yield RailShape.ASCENDING_NORTH;
                    }
                    case 2: {
                        yield RailShape.ASCENDING_SOUTH;
                    }
                    case 3: {
                        yield RailShape.ASCENDING_WEST;
                    }
                    case 4: {
                        yield RailShape.ASCENDING_EAST;
                    }
                    case 7: {
                        yield RailShape.NORTH_EAST;
                    }
                    case 8: {
                        yield RailShape.SOUTH_EAST;
                    }
                    case 9: {
                        yield RailShape.SOUTH_WEST;
                    }
                    case 10: 
                }
                yield RailShape.NORTH_WEST;
            }
            case 3 -> {
                switch (1.field_11372[shape.ordinal()]) {
                    default: {
                        throw new MatchException(null, null);
                    }
                    case 5: {
                        yield RailShape.EAST_WEST;
                    }
                    case 6: {
                        yield RailShape.NORTH_SOUTH;
                    }
                    case 1: {
                        yield RailShape.ASCENDING_SOUTH;
                    }
                    case 2: {
                        yield RailShape.ASCENDING_NORTH;
                    }
                    case 3: {
                        yield RailShape.ASCENDING_EAST;
                    }
                    case 4: {
                        yield RailShape.ASCENDING_WEST;
                    }
                    case 7: {
                        yield RailShape.SOUTH_WEST;
                    }
                    case 8: {
                        yield RailShape.NORTH_WEST;
                    }
                    case 9: {
                        yield RailShape.NORTH_EAST;
                    }
                    case 10: 
                }
                yield RailShape.SOUTH_EAST;
            }
            default -> shape;
        };
    }

    protected RailShape mirrorShape(RailShape shape, BlockMirror mirror) {
        return switch (1.field_11370[mirror.ordinal()]) {
            case 1 -> {
                switch (1.field_11372[shape.ordinal()]) {
                    case 3: {
                        yield RailShape.ASCENDING_SOUTH;
                    }
                    case 4: {
                        yield RailShape.ASCENDING_NORTH;
                    }
                    case 7: {
                        yield RailShape.NORTH_EAST;
                    }
                    case 8: {
                        yield RailShape.NORTH_WEST;
                    }
                    case 9: {
                        yield RailShape.SOUTH_WEST;
                    }
                    case 10: {
                        yield RailShape.SOUTH_EAST;
                    }
                }
                yield shape;
            }
            case 2 -> {
                switch (1.field_11372[shape.ordinal()]) {
                    case 1: {
                        yield RailShape.ASCENDING_WEST;
                    }
                    case 2: {
                        yield RailShape.ASCENDING_EAST;
                    }
                    case 7: {
                        yield RailShape.SOUTH_WEST;
                    }
                    case 8: {
                        yield RailShape.SOUTH_EAST;
                    }
                    case 9: {
                        yield RailShape.NORTH_EAST;
                    }
                    case 10: {
                        yield RailShape.NORTH_WEST;
                    }
                }
                yield shape;
            }
            default -> shape;
        };
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            tickView.scheduleFluidTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected FluidState getFluidState(BlockState state) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }
}

