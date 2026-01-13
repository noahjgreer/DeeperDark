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
 *  net.minecraft.block.HorizontalFacingBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.StairsBlock
 *  net.minecraft.block.StairsBlock$1
 *  net.minecraft.block.Waterloggable
 *  net.minecraft.block.enums.BlockHalf
 *  net.minecraft.block.enums.StairShape
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Axis
 *  net.minecraft.util.math.DirectionTransformation
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.StairShape;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.DirectionTransformation;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

/*
 * Exception performing whole class analysis ignored.
 */
public class StairsBlock
extends Block
implements Waterloggable {
    public static final MapCodec<StairsBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)BlockState.CODEC.fieldOf("base_state").forGetter(block -> block.baseBlockState), (App)StairsBlock.createSettingsCodec()).apply((Applicative)instance, StairsBlock::new));
    public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;
    public static final EnumProperty<BlockHalf> HALF = Properties.BLOCK_HALF;
    public static final EnumProperty<StairShape> SHAPE = Properties.STAIR_SHAPE;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private static final VoxelShape OUTER_SHAPE = VoxelShapes.union((VoxelShape)Block.createColumnShape((double)16.0, (double)0.0, (double)8.0), (VoxelShape)Block.createCuboidShape((double)0.0, (double)8.0, (double)0.0, (double)8.0, (double)16.0, (double)8.0));
    private static final VoxelShape STRAIGHT_SHAPE = VoxelShapes.union((VoxelShape)OUTER_SHAPE, (VoxelShape)VoxelShapes.transform((VoxelShape)OUTER_SHAPE, (DirectionTransformation)DirectionTransformation.field_64511));
    private static final VoxelShape INNER_SHAPE = VoxelShapes.union((VoxelShape)STRAIGHT_SHAPE, (VoxelShape)VoxelShapes.transform((VoxelShape)STRAIGHT_SHAPE, (DirectionTransformation)DirectionTransformation.field_64511));
    private static final Map<Direction, VoxelShape> OUTER_BOTTOM_SHAPES = VoxelShapes.createHorizontalFacingShapeMap((VoxelShape)OUTER_SHAPE);
    private static final Map<Direction, VoxelShape> STRAIGHT_BOTTOM_SHAPES = VoxelShapes.createHorizontalFacingShapeMap((VoxelShape)STRAIGHT_SHAPE);
    private static final Map<Direction, VoxelShape> INNER_BOTTOM_SHAPES = VoxelShapes.createHorizontalFacingShapeMap((VoxelShape)INNER_SHAPE);
    private static final Map<Direction, VoxelShape> OUTER_TOP_SHAPES = VoxelShapes.createHorizontalFacingShapeMap((VoxelShape)OUTER_SHAPE, (DirectionTransformation)DirectionTransformation.INVERT_Y);
    private static final Map<Direction, VoxelShape> STRAIGHT_TOP_SHAPES = VoxelShapes.createHorizontalFacingShapeMap((VoxelShape)STRAIGHT_SHAPE, (DirectionTransformation)DirectionTransformation.INVERT_Y);
    private static final Map<Direction, VoxelShape> INNER_TOP_SHAPES = VoxelShapes.createHorizontalFacingShapeMap((VoxelShape)INNER_SHAPE, (DirectionTransformation)DirectionTransformation.INVERT_Y);
    private final Block baseBlock;
    protected final BlockState baseBlockState;

    public MapCodec<? extends StairsBlock> getCodec() {
        return CODEC;
    }

    public StairsBlock(BlockState baseBlockState, AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)FACING, (Comparable)Direction.NORTH)).with((Property)HALF, (Comparable)BlockHalf.BOTTOM)).with((Property)SHAPE, (Comparable)StairShape.STRAIGHT)).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)));
        this.baseBlock = baseBlockState.getBlock();
        this.baseBlockState = baseBlockState;
    }

    protected boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        boolean bl = state.get((Property)HALF) == BlockHalf.BOTTOM;
        Direction direction = (Direction)state.get((Property)FACING);
        return (VoxelShape)(switch (1.field_11581[((StairShape)state.get((Property)SHAPE)).ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> {
                if (bl) {
                    yield STRAIGHT_BOTTOM_SHAPES;
                }
                yield STRAIGHT_TOP_SHAPES;
            }
            case 3, 4 -> {
                if (bl) {
                    yield INNER_BOTTOM_SHAPES;
                }
                yield INNER_TOP_SHAPES;
            }
            case 2, 5 -> bl ? OUTER_BOTTOM_SHAPES : OUTER_TOP_SHAPES;
        }).get(switch (1.field_11581[((StairShape)state.get((Property)SHAPE)).ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1, 2, 3 -> direction;
            case 4 -> direction.rotateYCounterclockwise();
            case 5 -> direction.rotateYClockwise();
        });
    }

    public float getBlastResistance() {
        return this.baseBlock.getBlastResistance();
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getSide();
        BlockPos blockPos = ctx.getBlockPos();
        FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
        BlockState blockState = (BlockState)((BlockState)((BlockState)this.getDefaultState().with((Property)FACING, (Comparable)ctx.getHorizontalPlayerFacing())).with((Property)HALF, (Comparable)(direction == Direction.DOWN || direction != Direction.UP && ctx.getHitPos().y - (double)blockPos.getY() > 0.5 ? BlockHalf.TOP : BlockHalf.BOTTOM))).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
        return (BlockState)blockState.with((Property)SHAPE, (Comparable)StairsBlock.getStairShape((BlockState)blockState, (BlockView)ctx.getWorld(), (BlockPos)blockPos));
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            tickView.scheduleFluidTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (direction.getAxis().isHorizontal()) {
            return (BlockState)state.with((Property)SHAPE, (Comparable)StairsBlock.getStairShape((BlockState)state, (BlockView)world, (BlockPos)pos));
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    private static StairShape getStairShape(BlockState state, BlockView world, BlockPos pos) {
        Direction direction3;
        Direction direction2;
        Direction direction = (Direction)state.get((Property)FACING);
        BlockState blockState = world.getBlockState(pos.offset(direction));
        if (StairsBlock.isStairs((BlockState)blockState) && state.get((Property)HALF) == blockState.get((Property)HALF) && (direction2 = (Direction)blockState.get((Property)FACING)).getAxis() != ((Direction)state.get((Property)FACING)).getAxis() && StairsBlock.isDifferentOrientation((BlockState)state, (BlockView)world, (BlockPos)pos, (Direction)direction2.getOpposite())) {
            if (direction2 == direction.rotateYCounterclockwise()) {
                return StairShape.OUTER_LEFT;
            }
            return StairShape.OUTER_RIGHT;
        }
        BlockState blockState2 = world.getBlockState(pos.offset(direction.getOpposite()));
        if (StairsBlock.isStairs((BlockState)blockState2) && state.get((Property)HALF) == blockState2.get((Property)HALF) && (direction3 = (Direction)blockState2.get((Property)FACING)).getAxis() != ((Direction)state.get((Property)FACING)).getAxis() && StairsBlock.isDifferentOrientation((BlockState)state, (BlockView)world, (BlockPos)pos, (Direction)direction3)) {
            if (direction3 == direction.rotateYCounterclockwise()) {
                return StairShape.INNER_LEFT;
            }
            return StairShape.INNER_RIGHT;
        }
        return StairShape.STRAIGHT;
    }

    private static boolean isDifferentOrientation(BlockState state, BlockView world, BlockPos pos, Direction dir) {
        BlockState blockState = world.getBlockState(pos.offset(dir));
        return !StairsBlock.isStairs((BlockState)blockState) || blockState.get((Property)FACING) != state.get((Property)FACING) || blockState.get((Property)HALF) != state.get((Property)HALF);
    }

    public static boolean isStairs(BlockState state) {
        return state.getBlock() instanceof StairsBlock;
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with((Property)FACING, (Comparable)rotation.rotate((Direction)state.get((Property)FACING)));
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        Direction direction = (Direction)state.get((Property)FACING);
        StairShape stairShape = (StairShape)state.get((Property)SHAPE);
        switch (1.field_11580[mirror.ordinal()]) {
            case 1: {
                if (direction.getAxis() != Direction.Axis.Z) break;
                switch (1.field_11581[stairShape.ordinal()]) {
                    case 4: {
                        return (BlockState)state.rotate(BlockRotation.CLOCKWISE_180).with((Property)SHAPE, (Comparable)StairShape.INNER_RIGHT);
                    }
                    case 3: {
                        return (BlockState)state.rotate(BlockRotation.CLOCKWISE_180).with((Property)SHAPE, (Comparable)StairShape.INNER_LEFT);
                    }
                    case 2: {
                        return (BlockState)state.rotate(BlockRotation.CLOCKWISE_180).with((Property)SHAPE, (Comparable)StairShape.OUTER_RIGHT);
                    }
                    case 5: {
                        return (BlockState)state.rotate(BlockRotation.CLOCKWISE_180).with((Property)SHAPE, (Comparable)StairShape.OUTER_LEFT);
                    }
                }
                return state.rotate(BlockRotation.CLOCKWISE_180);
            }
            case 2: {
                if (direction.getAxis() != Direction.Axis.X) break;
                switch (1.field_11581[stairShape.ordinal()]) {
                    case 4: {
                        return (BlockState)state.rotate(BlockRotation.CLOCKWISE_180).with((Property)SHAPE, (Comparable)StairShape.INNER_LEFT);
                    }
                    case 3: {
                        return (BlockState)state.rotate(BlockRotation.CLOCKWISE_180).with((Property)SHAPE, (Comparable)StairShape.INNER_RIGHT);
                    }
                    case 2: {
                        return (BlockState)state.rotate(BlockRotation.CLOCKWISE_180).with((Property)SHAPE, (Comparable)StairShape.OUTER_RIGHT);
                    }
                    case 5: {
                        return (BlockState)state.rotate(BlockRotation.CLOCKWISE_180).with((Property)SHAPE, (Comparable)StairShape.OUTER_LEFT);
                    }
                    case 1: {
                        return state.rotate(BlockRotation.CLOCKWISE_180);
                    }
                }
                break;
            }
        }
        return super.mirror(state, mirror);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, HALF, SHAPE, WATERLOGGED});
    }

    protected FluidState getFluidState(BlockState state) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
}

