/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.FenceGateBlock
 *  net.minecraft.block.PaneBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.WallBlock
 *  net.minecraft.block.WallBlock$1
 *  net.minecraft.block.Waterloggable
 *  net.minecraft.block.enums.WallShape
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.function.BooleanBiFunction
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 */
package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WallBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.enums.WallShape;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

/*
 * Exception performing whole class analysis ignored.
 */
public class WallBlock
extends Block
implements Waterloggable {
    public static final MapCodec<WallBlock> CODEC = WallBlock.createCodec(WallBlock::new);
    public static final BooleanProperty UP = Properties.UP;
    public static final EnumProperty<WallShape> EAST_WALL_SHAPE = Properties.EAST_WALL_SHAPE;
    public static final EnumProperty<WallShape> NORTH_WALL_SHAPE = Properties.NORTH_WALL_SHAPE;
    public static final EnumProperty<WallShape> SOUTH_WALL_SHAPE = Properties.SOUTH_WALL_SHAPE;
    public static final EnumProperty<WallShape> WEST_WALL_SHAPE = Properties.WEST_WALL_SHAPE;
    public static final Map<Direction, EnumProperty<WallShape>> WALL_SHAPE_PROPERTIES_BY_DIRECTION = ImmutableMap.copyOf((Map)Maps.newEnumMap(Map.of(Direction.NORTH, NORTH_WALL_SHAPE, Direction.EAST, EAST_WALL_SHAPE, Direction.SOUTH, SOUTH_WALL_SHAPE, Direction.WEST, WEST_WALL_SHAPE)));
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private final Function<BlockState, VoxelShape> outlineShapeFunction;
    private final Function<BlockState, VoxelShape> collisionShapeFunction;
    private static final VoxelShape POST_SHAPE_FOR_TALL_TEST = Block.createColumnShape((double)2.0, (double)0.0, (double)16.0);
    private static final Map<Direction, VoxelShape> WALL_SHAPES_FOR_TALL_TEST_BY_DIRECTION = VoxelShapes.createHorizontalFacingShapeMap((VoxelShape)Block.createCuboidZShape((double)2.0, (double)16.0, (double)0.0, (double)9.0));

    public MapCodec<WallBlock> getCodec() {
        return CODEC;
    }

    public WallBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)UP, (Comparable)Boolean.valueOf(true))).with((Property)NORTH_WALL_SHAPE, (Comparable)WallShape.NONE)).with((Property)EAST_WALL_SHAPE, (Comparable)WallShape.NONE)).with((Property)SOUTH_WALL_SHAPE, (Comparable)WallShape.NONE)).with((Property)WEST_WALL_SHAPE, (Comparable)WallShape.NONE)).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)));
        this.outlineShapeFunction = this.createShapeFunction(16.0f, 14.0f);
        this.collisionShapeFunction = this.createShapeFunction(24.0f, 24.0f);
    }

    private Function<BlockState, VoxelShape> createShapeFunction(float tallHeight, float lowHeight) {
        VoxelShape voxelShape = Block.createColumnShape((double)8.0, (double)0.0, (double)tallHeight);
        int i = 6;
        Map map = VoxelShapes.createHorizontalFacingShapeMap((VoxelShape)Block.createCuboidZShape((double)6.0, (double)0.0, (double)lowHeight, (double)0.0, (double)11.0));
        Map map2 = VoxelShapes.createHorizontalFacingShapeMap((VoxelShape)Block.createCuboidZShape((double)6.0, (double)0.0, (double)tallHeight, (double)0.0, (double)11.0));
        return this.createShapeFunction(state -> {
            VoxelShape voxelShape2 = (Boolean)state.get((Property)UP) != false ? voxelShape : VoxelShapes.empty();
            for (Map.Entry entry : WALL_SHAPE_PROPERTIES_BY_DIRECTION.entrySet()) {
                voxelShape2 = VoxelShapes.union((VoxelShape)voxelShape2, (VoxelShape)(switch (1.field_55821[((WallShape)state.get((Property)entry.getValue())).ordinal()]) {
                    default -> throw new MatchException(null, null);
                    case 1 -> VoxelShapes.empty();
                    case 2 -> (VoxelShape)map.get(entry.getKey());
                    case 3 -> (VoxelShape)map2.get(entry.getKey());
                }));
            }
            return voxelShape2;
        }, new Property[]{WATERLOGGED});
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (VoxelShape)this.outlineShapeFunction.apply(state);
    }

    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (VoxelShape)this.collisionShapeFunction.apply(state);
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    private boolean shouldConnectTo(BlockState state, boolean faceFullSquare, Direction side) {
        Block block = state.getBlock();
        boolean bl = block instanceof FenceGateBlock && FenceGateBlock.canWallConnect((BlockState)state, (Direction)side);
        return state.isIn(BlockTags.WALLS) || !WallBlock.cannotConnect((BlockState)state) && faceFullSquare || block instanceof PaneBlock || bl;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World worldView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        BlockPos blockPos2 = blockPos.north();
        BlockPos blockPos3 = blockPos.east();
        BlockPos blockPos4 = blockPos.south();
        BlockPos blockPos5 = blockPos.west();
        BlockPos blockPos6 = blockPos.up();
        BlockState blockState = worldView.getBlockState(blockPos2);
        BlockState blockState2 = worldView.getBlockState(blockPos3);
        BlockState blockState3 = worldView.getBlockState(blockPos4);
        BlockState blockState4 = worldView.getBlockState(blockPos5);
        BlockState blockState5 = worldView.getBlockState(blockPos6);
        boolean bl = this.shouldConnectTo(blockState, blockState.isSideSolidFullSquare((BlockView)worldView, blockPos2, Direction.SOUTH), Direction.SOUTH);
        boolean bl2 = this.shouldConnectTo(blockState2, blockState2.isSideSolidFullSquare((BlockView)worldView, blockPos3, Direction.WEST), Direction.WEST);
        boolean bl3 = this.shouldConnectTo(blockState3, blockState3.isSideSolidFullSquare((BlockView)worldView, blockPos4, Direction.NORTH), Direction.NORTH);
        boolean bl4 = this.shouldConnectTo(blockState4, blockState4.isSideSolidFullSquare((BlockView)worldView, blockPos5, Direction.EAST), Direction.EAST);
        BlockState blockState6 = (BlockState)this.getDefaultState().with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
        return this.getStateWith((WorldView)worldView, blockState6, blockPos6, blockState5, bl, bl2, bl3, bl4);
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            tickView.scheduleFluidTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (direction == Direction.DOWN) {
            return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
        }
        if (direction == Direction.UP) {
            return this.getStateAt(world, state, neighborPos, neighborState);
        }
        return this.getStateWithNeighbor(world, pos, state, neighborPos, neighborState, direction);
    }

    private static boolean isConnected(BlockState state, Property<WallShape> property) {
        return state.get(property) != WallShape.NONE;
    }

    private static boolean shouldUseTallShape(VoxelShape aboveShape, VoxelShape tallShape) {
        return !VoxelShapes.matchesAnywhere((VoxelShape)tallShape, (VoxelShape)aboveShape, (BooleanBiFunction)BooleanBiFunction.ONLY_FIRST);
    }

    private BlockState getStateAt(WorldView world, BlockState state, BlockPos pos, BlockState aboveState) {
        boolean bl = WallBlock.isConnected((BlockState)state, (Property)NORTH_WALL_SHAPE);
        boolean bl2 = WallBlock.isConnected((BlockState)state, (Property)EAST_WALL_SHAPE);
        boolean bl3 = WallBlock.isConnected((BlockState)state, (Property)SOUTH_WALL_SHAPE);
        boolean bl4 = WallBlock.isConnected((BlockState)state, (Property)WEST_WALL_SHAPE);
        return this.getStateWith(world, state, pos, aboveState, bl, bl2, bl3, bl4);
    }

    private BlockState getStateWithNeighbor(WorldView world, BlockPos pos, BlockState state, BlockPos neighborPos, BlockState neighborState, Direction direction) {
        Direction direction2 = direction.getOpposite();
        boolean bl = direction == Direction.NORTH ? this.shouldConnectTo(neighborState, neighborState.isSideSolidFullSquare((BlockView)world, neighborPos, direction2), direction2) : WallBlock.isConnected((BlockState)state, (Property)NORTH_WALL_SHAPE);
        boolean bl2 = direction == Direction.EAST ? this.shouldConnectTo(neighborState, neighborState.isSideSolidFullSquare((BlockView)world, neighborPos, direction2), direction2) : WallBlock.isConnected((BlockState)state, (Property)EAST_WALL_SHAPE);
        boolean bl3 = direction == Direction.SOUTH ? this.shouldConnectTo(neighborState, neighborState.isSideSolidFullSquare((BlockView)world, neighborPos, direction2), direction2) : WallBlock.isConnected((BlockState)state, (Property)SOUTH_WALL_SHAPE);
        boolean bl4 = direction == Direction.WEST ? this.shouldConnectTo(neighborState, neighborState.isSideSolidFullSquare((BlockView)world, neighborPos, direction2), direction2) : WallBlock.isConnected((BlockState)state, (Property)WEST_WALL_SHAPE);
        BlockPos blockPos = pos.up();
        BlockState blockState = world.getBlockState(blockPos);
        return this.getStateWith(world, state, blockPos, blockState, bl, bl2, bl3, bl4);
    }

    private BlockState getStateWith(WorldView world, BlockState state, BlockPos pos, BlockState aboveState, boolean north, boolean east, boolean south, boolean west) {
        VoxelShape voxelShape = aboveState.getCollisionShape((BlockView)world, pos).getFace(Direction.DOWN);
        BlockState blockState = this.getStateWith(state, north, east, south, west, voxelShape);
        return (BlockState)blockState.with((Property)UP, (Comparable)Boolean.valueOf(this.shouldHavePost(blockState, aboveState, voxelShape)));
    }

    private boolean shouldHavePost(BlockState state, BlockState aboveState, VoxelShape aboveShape) {
        boolean bl7;
        boolean bl6;
        boolean bl;
        boolean bl2 = bl = aboveState.getBlock() instanceof WallBlock && (Boolean)aboveState.get((Property)UP) != false;
        if (bl) {
            return true;
        }
        WallShape wallShape = (WallShape)state.get((Property)NORTH_WALL_SHAPE);
        WallShape wallShape2 = (WallShape)state.get((Property)SOUTH_WALL_SHAPE);
        WallShape wallShape3 = (WallShape)state.get((Property)EAST_WALL_SHAPE);
        WallShape wallShape4 = (WallShape)state.get((Property)WEST_WALL_SHAPE);
        boolean bl22 = wallShape2 == WallShape.NONE;
        boolean bl3 = wallShape4 == WallShape.NONE;
        boolean bl4 = wallShape3 == WallShape.NONE;
        boolean bl5 = wallShape == WallShape.NONE;
        boolean bl8 = bl6 = bl5 && bl22 && bl3 && bl4 || bl5 != bl22 || bl3 != bl4;
        if (bl6) {
            return true;
        }
        boolean bl9 = bl7 = wallShape == WallShape.TALL && wallShape2 == WallShape.TALL || wallShape3 == WallShape.TALL && wallShape4 == WallShape.TALL;
        if (bl7) {
            return false;
        }
        return aboveState.isIn(BlockTags.WALL_POST_OVERRIDE) || WallBlock.shouldUseTallShape((VoxelShape)aboveShape, (VoxelShape)POST_SHAPE_FOR_TALL_TEST);
    }

    private BlockState getStateWith(BlockState state, boolean north, boolean east, boolean south, boolean west, VoxelShape aboveShape) {
        return (BlockState)((BlockState)((BlockState)((BlockState)state.with((Property)NORTH_WALL_SHAPE, (Comparable)this.getWallShape(north, aboveShape, (VoxelShape)WALL_SHAPES_FOR_TALL_TEST_BY_DIRECTION.get(Direction.NORTH)))).with((Property)EAST_WALL_SHAPE, (Comparable)this.getWallShape(east, aboveShape, (VoxelShape)WALL_SHAPES_FOR_TALL_TEST_BY_DIRECTION.get(Direction.EAST)))).with((Property)SOUTH_WALL_SHAPE, (Comparable)this.getWallShape(south, aboveShape, (VoxelShape)WALL_SHAPES_FOR_TALL_TEST_BY_DIRECTION.get(Direction.SOUTH)))).with((Property)WEST_WALL_SHAPE, (Comparable)this.getWallShape(west, aboveShape, (VoxelShape)WALL_SHAPES_FOR_TALL_TEST_BY_DIRECTION.get(Direction.WEST)));
    }

    private WallShape getWallShape(boolean connected, VoxelShape aboveShape, VoxelShape tallShape) {
        if (connected) {
            if (WallBlock.shouldUseTallShape((VoxelShape)aboveShape, (VoxelShape)tallShape)) {
                return WallShape.TALL;
            }
            return WallShape.LOW;
        }
        return WallShape.NONE;
    }

    protected FluidState getFluidState(BlockState state) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    protected boolean isTransparent(BlockState state) {
        return (Boolean)state.get((Property)WATERLOGGED) == false;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{UP, NORTH_WALL_SHAPE, EAST_WALL_SHAPE, WEST_WALL_SHAPE, SOUTH_WALL_SHAPE, WATERLOGGED});
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        switch (1.field_22168[rotation.ordinal()]) {
            case 1: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with((Property)NORTH_WALL_SHAPE, (Comparable)((WallShape)state.get((Property)SOUTH_WALL_SHAPE)))).with((Property)EAST_WALL_SHAPE, (Comparable)((WallShape)state.get((Property)WEST_WALL_SHAPE)))).with((Property)SOUTH_WALL_SHAPE, (Comparable)((WallShape)state.get((Property)NORTH_WALL_SHAPE)))).with((Property)WEST_WALL_SHAPE, (Comparable)((WallShape)state.get((Property)EAST_WALL_SHAPE)));
            }
            case 2: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with((Property)NORTH_WALL_SHAPE, (Comparable)((WallShape)state.get((Property)EAST_WALL_SHAPE)))).with((Property)EAST_WALL_SHAPE, (Comparable)((WallShape)state.get((Property)SOUTH_WALL_SHAPE)))).with((Property)SOUTH_WALL_SHAPE, (Comparable)((WallShape)state.get((Property)WEST_WALL_SHAPE)))).with((Property)WEST_WALL_SHAPE, (Comparable)((WallShape)state.get((Property)NORTH_WALL_SHAPE)));
            }
            case 3: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with((Property)NORTH_WALL_SHAPE, (Comparable)((WallShape)state.get((Property)WEST_WALL_SHAPE)))).with((Property)EAST_WALL_SHAPE, (Comparable)((WallShape)state.get((Property)NORTH_WALL_SHAPE)))).with((Property)SOUTH_WALL_SHAPE, (Comparable)((WallShape)state.get((Property)EAST_WALL_SHAPE)))).with((Property)WEST_WALL_SHAPE, (Comparable)((WallShape)state.get((Property)SOUTH_WALL_SHAPE)));
            }
        }
        return state;
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        switch (1.field_22169[mirror.ordinal()]) {
            case 1: {
                return (BlockState)((BlockState)state.with((Property)NORTH_WALL_SHAPE, (Comparable)((WallShape)state.get((Property)SOUTH_WALL_SHAPE)))).with((Property)SOUTH_WALL_SHAPE, (Comparable)((WallShape)state.get((Property)NORTH_WALL_SHAPE)));
            }
            case 2: {
                return (BlockState)((BlockState)state.with((Property)EAST_WALL_SHAPE, (Comparable)((WallShape)state.get((Property)WEST_WALL_SHAPE)))).with((Property)WEST_WALL_SHAPE, (Comparable)((WallShape)state.get((Property)EAST_WALL_SHAPE)));
            }
        }
        return super.mirror(state, mirror);
    }
}

