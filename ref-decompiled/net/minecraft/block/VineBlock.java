/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.ConnectingBlock
 *  net.minecraft.block.MultifaceBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.VineBlock
 *  net.minecraft.block.VineBlock$1
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Axis
 *  net.minecraft.util.math.Direction$Type
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.rule.GameRules
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.block.MultifaceBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.VineBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import net.minecraft.world.rule.GameRules;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class VineBlock
extends Block {
    public static final MapCodec<VineBlock> CODEC = VineBlock.createCodec(VineBlock::new);
    public static final BooleanProperty UP = ConnectingBlock.UP;
    public static final BooleanProperty NORTH = ConnectingBlock.NORTH;
    public static final BooleanProperty EAST = ConnectingBlock.EAST;
    public static final BooleanProperty SOUTH = ConnectingBlock.SOUTH;
    public static final BooleanProperty WEST = ConnectingBlock.WEST;
    public static final Map<Direction, BooleanProperty> FACING_PROPERTIES = (Map)ConnectingBlock.FACING_PROPERTIES.entrySet().stream().filter(entry -> entry.getKey() != Direction.DOWN).collect(Util.toMap());
    private final Function<BlockState, VoxelShape> shapeFunction;

    public MapCodec<VineBlock> getCodec() {
        return CODEC;
    }

    public VineBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)UP, (Comparable)Boolean.valueOf(false))).with((Property)NORTH, (Comparable)Boolean.valueOf(false))).with((Property)EAST, (Comparable)Boolean.valueOf(false))).with((Property)SOUTH, (Comparable)Boolean.valueOf(false))).with((Property)WEST, (Comparable)Boolean.valueOf(false)));
        this.shapeFunction = this.createShapeFunction();
    }

    private Function<BlockState, VoxelShape> createShapeFunction() {
        Map map = VoxelShapes.createFacingShapeMap((VoxelShape)Block.createCuboidZShape((double)16.0, (double)0.0, (double)1.0));
        return this.createShapeFunction(state -> {
            VoxelShape voxelShape = VoxelShapes.empty();
            for (Map.Entry entry : FACING_PROPERTIES.entrySet()) {
                if (!((Boolean)state.get((Property)entry.getValue())).booleanValue()) continue;
                voxelShape = VoxelShapes.union((VoxelShape)voxelShape, (VoxelShape)((VoxelShape)map.get(entry.getKey())));
            }
            return voxelShape.isEmpty() ? VoxelShapes.fullCube() : voxelShape;
        });
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (VoxelShape)this.shapeFunction.apply(state);
    }

    protected boolean isTransparent(BlockState state) {
        return true;
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return this.hasAdjacentBlocks(this.getPlacementShape(state, (BlockView)world, pos));
    }

    private boolean hasAdjacentBlocks(BlockState state) {
        return this.getAdjacentBlockCount(state) > 0;
    }

    private int getAdjacentBlockCount(BlockState state) {
        int i = 0;
        for (BooleanProperty booleanProperty : FACING_PROPERTIES.values()) {
            if (!((Boolean)state.get((Property)booleanProperty)).booleanValue()) continue;
            ++i;
        }
        return i;
    }

    private boolean shouldHaveSide(BlockView world, BlockPos pos, Direction side) {
        if (side == Direction.DOWN) {
            return false;
        }
        BlockPos blockPos = pos.offset(side);
        if (VineBlock.shouldConnectTo((BlockView)world, (BlockPos)blockPos, (Direction)side)) {
            return true;
        }
        if (side.getAxis() != Direction.Axis.Y) {
            BooleanProperty booleanProperty = (BooleanProperty)FACING_PROPERTIES.get(side);
            BlockState blockState = world.getBlockState(pos.up());
            return blockState.isOf((Block)this) && (Boolean)blockState.get((Property)booleanProperty) != false;
        }
        return false;
    }

    public static boolean shouldConnectTo(BlockView world, BlockPos pos, Direction direction) {
        return MultifaceBlock.canGrowOn((BlockView)world, (Direction)direction, (BlockPos)pos, (BlockState)world.getBlockState(pos));
    }

    private BlockState getPlacementShape(BlockState state, BlockView world, BlockPos pos) {
        BlockPos blockPos = pos.up();
        if (((Boolean)state.get((Property)UP)).booleanValue()) {
            state = (BlockState)state.with((Property)UP, (Comparable)Boolean.valueOf(VineBlock.shouldConnectTo((BlockView)world, (BlockPos)blockPos, (Direction)Direction.DOWN)));
        }
        BlockState blockState = null;
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BooleanProperty booleanProperty = VineBlock.getFacingProperty((Direction)direction);
            if (!((Boolean)state.get((Property)booleanProperty)).booleanValue()) continue;
            boolean bl = this.shouldHaveSide(world, pos, direction);
            if (!bl) {
                if (blockState == null) {
                    blockState = world.getBlockState(blockPos);
                }
                bl = blockState.isOf((Block)this) && (Boolean)blockState.get((Property)booleanProperty) != false;
            }
            state = (BlockState)state.with((Property)booleanProperty, (Comparable)Boolean.valueOf(bl));
        }
        return state;
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (direction == Direction.DOWN) {
            return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
        }
        BlockState blockState = this.getPlacementShape(state, (BlockView)world, pos);
        if (!this.hasAdjacentBlocks(blockState)) {
            return Blocks.AIR.getDefaultState();
        }
        return blockState;
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockState blockState4;
        BlockState blockState3;
        BlockPos blockPos2;
        BlockState blockState;
        if (!((Boolean)world.getGameRules().getValue(GameRules.SPREAD_VINES)).booleanValue()) {
            return;
        }
        if (random.nextInt(4) != 0) {
            return;
        }
        Direction direction = Direction.random((Random)random);
        BlockPos blockPos = pos.up();
        if (direction.getAxis().isHorizontal() && !((Boolean)state.get((Property)VineBlock.getFacingProperty((Direction)direction))).booleanValue()) {
            if (!this.canGrowAt((BlockView)world, pos)) {
                return;
            }
            BlockPos blockPos22 = pos.offset(direction);
            BlockState blockState2 = world.getBlockState(blockPos22);
            if (blockState2.isAir()) {
                Direction direction2 = direction.rotateYClockwise();
                Direction direction3 = direction.rotateYCounterclockwise();
                boolean bl = (Boolean)state.get((Property)VineBlock.getFacingProperty((Direction)direction2));
                boolean bl2 = (Boolean)state.get((Property)VineBlock.getFacingProperty((Direction)direction3));
                BlockPos blockPos3 = blockPos22.offset(direction2);
                BlockPos blockPos4 = blockPos22.offset(direction3);
                if (bl && VineBlock.shouldConnectTo((BlockView)world, (BlockPos)blockPos3, (Direction)direction2)) {
                    world.setBlockState(blockPos22, (BlockState)this.getDefaultState().with((Property)VineBlock.getFacingProperty((Direction)direction2), (Comparable)Boolean.valueOf(true)), 2);
                } else if (bl2 && VineBlock.shouldConnectTo((BlockView)world, (BlockPos)blockPos4, (Direction)direction3)) {
                    world.setBlockState(blockPos22, (BlockState)this.getDefaultState().with((Property)VineBlock.getFacingProperty((Direction)direction3), (Comparable)Boolean.valueOf(true)), 2);
                } else {
                    Direction direction4 = direction.getOpposite();
                    if (bl && world.isAir(blockPos3) && VineBlock.shouldConnectTo((BlockView)world, (BlockPos)pos.offset(direction2), (Direction)direction4)) {
                        world.setBlockState(blockPos3, (BlockState)this.getDefaultState().with((Property)VineBlock.getFacingProperty((Direction)direction4), (Comparable)Boolean.valueOf(true)), 2);
                    } else if (bl2 && world.isAir(blockPos4) && VineBlock.shouldConnectTo((BlockView)world, (BlockPos)pos.offset(direction3), (Direction)direction4)) {
                        world.setBlockState(blockPos4, (BlockState)this.getDefaultState().with((Property)VineBlock.getFacingProperty((Direction)direction4), (Comparable)Boolean.valueOf(true)), 2);
                    } else if ((double)random.nextFloat() < 0.05 && VineBlock.shouldConnectTo((BlockView)world, (BlockPos)blockPos22.up(), (Direction)Direction.UP)) {
                        world.setBlockState(blockPos22, (BlockState)this.getDefaultState().with((Property)UP, (Comparable)Boolean.valueOf(true)), 2);
                    }
                }
            } else if (VineBlock.shouldConnectTo((BlockView)world, (BlockPos)blockPos22, (Direction)direction)) {
                world.setBlockState(pos, (BlockState)state.with((Property)VineBlock.getFacingProperty((Direction)direction), (Comparable)Boolean.valueOf(true)), 2);
            }
            return;
        }
        if (direction == Direction.UP && pos.getY() < world.getTopYInclusive()) {
            if (this.shouldHaveSide((BlockView)world, pos, direction)) {
                world.setBlockState(pos, (BlockState)state.with((Property)UP, (Comparable)Boolean.valueOf(true)), 2);
                return;
            }
            if (world.isAir(blockPos)) {
                if (!this.canGrowAt((BlockView)world, pos)) {
                    return;
                }
                BlockState blockState2 = state;
                for (Direction direction2 : Direction.Type.HORIZONTAL) {
                    if (!random.nextBoolean() && VineBlock.shouldConnectTo((BlockView)world, (BlockPos)blockPos.offset(direction2), (Direction)direction2)) continue;
                    blockState2 = (BlockState)blockState2.with((Property)VineBlock.getFacingProperty((Direction)direction2), (Comparable)Boolean.valueOf(false));
                }
                if (this.hasHorizontalSide(blockState2)) {
                    world.setBlockState(blockPos, blockState2, 2);
                }
                return;
            }
        }
        if (pos.getY() > world.getBottomY() && ((blockState = world.getBlockState(blockPos2 = pos.down())).isAir() || blockState.isOf((Block)this)) && (blockState3 = blockState.isAir() ? this.getDefaultState() : blockState) != (blockState4 = this.getGrownState(state, blockState3, random)) && this.hasHorizontalSide(blockState4)) {
            world.setBlockState(blockPos2, blockState4, 2);
        }
    }

    private BlockState getGrownState(BlockState above, BlockState state, Random random) {
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BooleanProperty booleanProperty;
            if (!random.nextBoolean() || !((Boolean)above.get((Property)(booleanProperty = VineBlock.getFacingProperty((Direction)direction)))).booleanValue()) continue;
            state = (BlockState)state.with((Property)booleanProperty, (Comparable)Boolean.valueOf(true));
        }
        return state;
    }

    private boolean hasHorizontalSide(BlockState state) {
        return (Boolean)state.get((Property)NORTH) != false || (Boolean)state.get((Property)EAST) != false || (Boolean)state.get((Property)SOUTH) != false || (Boolean)state.get((Property)WEST) != false;
    }

    private boolean canGrowAt(BlockView world, BlockPos pos) {
        int i = 4;
        Iterable iterable = BlockPos.iterate((int)(pos.getX() - 4), (int)(pos.getY() - 1), (int)(pos.getZ() - 4), (int)(pos.getX() + 4), (int)(pos.getY() + 1), (int)(pos.getZ() + 4));
        int j = 5;
        for (BlockPos blockPos : iterable) {
            if (!world.getBlockState(blockPos).isOf((Block)this) || --j > 0) continue;
            return false;
        }
        return true;
    }

    protected boolean canReplace(BlockState state, ItemPlacementContext context) {
        BlockState blockState = context.getWorld().getBlockState(context.getBlockPos());
        if (blockState.isOf((Block)this)) {
            return this.getAdjacentBlockCount(blockState) < FACING_PROPERTIES.size();
        }
        return super.canReplace(state, context);
    }

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
        boolean bl = blockState.isOf((Block)this);
        BlockState blockState2 = bl ? blockState : this.getDefaultState();
        for (Direction direction : ctx.getPlacementDirections()) {
            boolean bl2;
            if (direction == Direction.DOWN) continue;
            BooleanProperty booleanProperty = VineBlock.getFacingProperty((Direction)direction);
            boolean bl3 = bl2 = bl && (Boolean)blockState.get((Property)booleanProperty) != false;
            if (bl2 || !this.shouldHaveSide((BlockView)ctx.getWorld(), ctx.getBlockPos(), direction)) continue;
            return (BlockState)blockState2.with((Property)booleanProperty, (Comparable)Boolean.valueOf(true));
        }
        return bl ? blockState2 : null;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{UP, NORTH, EAST, SOUTH, WEST});
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        switch (1.field_11708[rotation.ordinal()]) {
            case 1: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with((Property)NORTH, (Comparable)((Boolean)state.get((Property)SOUTH)))).with((Property)EAST, (Comparable)((Boolean)state.get((Property)WEST)))).with((Property)SOUTH, (Comparable)((Boolean)state.get((Property)NORTH)))).with((Property)WEST, (Comparable)((Boolean)state.get((Property)EAST)));
            }
            case 2: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with((Property)NORTH, (Comparable)((Boolean)state.get((Property)EAST)))).with((Property)EAST, (Comparable)((Boolean)state.get((Property)SOUTH)))).with((Property)SOUTH, (Comparable)((Boolean)state.get((Property)WEST)))).with((Property)WEST, (Comparable)((Boolean)state.get((Property)NORTH)));
            }
            case 3: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with((Property)NORTH, (Comparable)((Boolean)state.get((Property)WEST)))).with((Property)EAST, (Comparable)((Boolean)state.get((Property)NORTH)))).with((Property)SOUTH, (Comparable)((Boolean)state.get((Property)EAST)))).with((Property)WEST, (Comparable)((Boolean)state.get((Property)SOUTH)));
            }
        }
        return state;
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        switch (1.field_11707[mirror.ordinal()]) {
            case 1: {
                return (BlockState)((BlockState)state.with((Property)NORTH, (Comparable)((Boolean)state.get((Property)SOUTH)))).with((Property)SOUTH, (Comparable)((Boolean)state.get((Property)NORTH)));
            }
            case 2: {
                return (BlockState)((BlockState)state.with((Property)EAST, (Comparable)((Boolean)state.get((Property)WEST)))).with((Property)WEST, (Comparable)((Boolean)state.get((Property)EAST)));
            }
        }
        return super.mirror(state, mirror);
    }

    public static BooleanProperty getFacingProperty(Direction direction) {
        return (BooleanProperty)FACING_PROPERTIES.get(direction);
    }
}

