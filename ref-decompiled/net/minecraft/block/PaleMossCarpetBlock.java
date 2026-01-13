/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.Block$SetBlockStateFlag
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Fertilizable
 *  net.minecraft.block.MultifaceBlock
 *  net.minecraft.block.PaleMossCarpetBlock
 *  net.minecraft.block.PaleMossCarpetBlock$1
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.enums.WallShape
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
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
 *  net.minecraft.util.math.Direction$Type
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.MultifaceBlock;
import net.minecraft.block.PaleMossCarpetBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.WallShape;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
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
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class PaleMossCarpetBlock
extends Block
implements Fertilizable {
    public static final MapCodec<PaleMossCarpetBlock> CODEC = PaleMossCarpetBlock.createCodec(PaleMossCarpetBlock::new);
    public static final BooleanProperty BOTTOM = Properties.BOTTOM;
    public static final EnumProperty<WallShape> NORTH = Properties.NORTH_WALL_SHAPE;
    public static final EnumProperty<WallShape> EAST = Properties.EAST_WALL_SHAPE;
    public static final EnumProperty<WallShape> SOUTH = Properties.SOUTH_WALL_SHAPE;
    public static final EnumProperty<WallShape> WEST = Properties.WEST_WALL_SHAPE;
    public static final Map<Direction, EnumProperty<WallShape>> WALL_SHAPE_PROPERTIES_BY_DIRECTION = ImmutableMap.copyOf((Map)Maps.newEnumMap(Map.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST)));
    private final Function<BlockState, VoxelShape> shapeFunction;

    public MapCodec<PaleMossCarpetBlock> getCodec() {
        return CODEC;
    }

    public PaleMossCarpetBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)BOTTOM, (Comparable)Boolean.valueOf(true))).with((Property)NORTH, (Comparable)WallShape.NONE)).with((Property)EAST, (Comparable)WallShape.NONE)).with((Property)SOUTH, (Comparable)WallShape.NONE)).with((Property)WEST, (Comparable)WallShape.NONE));
        this.shapeFunction = this.createShapeFunction();
    }

    public Function<BlockState, VoxelShape> createShapeFunction() {
        Map map = VoxelShapes.createHorizontalFacingShapeMap((VoxelShape)Block.createCuboidZShape((double)16.0, (double)0.0, (double)10.0, (double)0.0, (double)1.0));
        Map map2 = VoxelShapes.createFacingShapeMap((VoxelShape)Block.createCuboidZShape((double)16.0, (double)0.0, (double)1.0));
        return this.createShapeFunction(state -> {
            VoxelShape voxelShape = (Boolean)state.get((Property)BOTTOM) != false ? (VoxelShape)map2.get(Direction.DOWN) : VoxelShapes.empty();
            for (Map.Entry entry : WALL_SHAPE_PROPERTIES_BY_DIRECTION.entrySet()) {
                switch (1.field_54770[((WallShape)state.get((Property)entry.getValue())).ordinal()]) {
                    case 1: {
                        break;
                    }
                    case 2: {
                        voxelShape = VoxelShapes.union((VoxelShape)voxelShape, (VoxelShape)((VoxelShape)map.get(entry.getKey())));
                        break;
                    }
                    case 3: {
                        voxelShape = VoxelShapes.union((VoxelShape)voxelShape, (VoxelShape)((VoxelShape)map2.get(entry.getKey())));
                    }
                }
            }
            return voxelShape.isEmpty() ? VoxelShapes.fullCube() : voxelShape;
        });
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (VoxelShape)this.shapeFunction.apply(state);
    }

    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (Boolean)state.get((Property)BOTTOM) != false ? (VoxelShape)this.shapeFunction.apply(this.getDefaultState()) : VoxelShapes.empty();
    }

    protected boolean isTransparent(BlockState state) {
        return true;
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos.down());
        if (((Boolean)state.get((Property)BOTTOM)).booleanValue()) {
            return !blockState.isAir();
        }
        return blockState.isOf((Block)this) && (Boolean)blockState.get((Property)BOTTOM) != false;
    }

    private static boolean hasAnyShape(BlockState state) {
        if (((Boolean)state.get((Property)BOTTOM)).booleanValue()) {
            return true;
        }
        for (EnumProperty enumProperty : WALL_SHAPE_PROPERTIES_BY_DIRECTION.values()) {
            if (state.get((Property)enumProperty) == WallShape.NONE) continue;
            return true;
        }
        return false;
    }

    private static boolean canGrowOnFace(BlockView world, BlockPos pos, Direction direction) {
        if (direction == Direction.UP) {
            return false;
        }
        return MultifaceBlock.canGrowOn((BlockView)world, (BlockPos)pos, (Direction)direction);
    }

    private static BlockState updateState(BlockState state, BlockView world, BlockPos pos, boolean bl) {
        BlockState blockState = null;
        BlockState blockState2 = null;
        bl |= ((Boolean)state.get((Property)BOTTOM)).booleanValue();
        for (Direction direction : Direction.Type.HORIZONTAL) {
            WallShape wallShape;
            EnumProperty enumProperty = PaleMossCarpetBlock.getWallShape((Direction)direction);
            WallShape wallShape2 = PaleMossCarpetBlock.canGrowOnFace((BlockView)world, (BlockPos)pos, (Direction)direction) ? (bl ? WallShape.LOW : (WallShape)state.get((Property)enumProperty)) : (wallShape = WallShape.NONE);
            if (wallShape == WallShape.LOW) {
                if (blockState == null) {
                    blockState = world.getBlockState(pos.up());
                }
                if (blockState.isOf(Blocks.PALE_MOSS_CARPET) && blockState.get((Property)enumProperty) != WallShape.NONE && !((Boolean)blockState.get((Property)BOTTOM)).booleanValue()) {
                    wallShape = WallShape.TALL;
                }
                if (!((Boolean)state.get((Property)BOTTOM)).booleanValue()) {
                    if (blockState2 == null) {
                        blockState2 = world.getBlockState(pos.down());
                    }
                    if (blockState2.isOf(Blocks.PALE_MOSS_CARPET) && blockState2.get((Property)enumProperty) == WallShape.NONE) {
                        wallShape = WallShape.NONE;
                    }
                }
            }
            state = (BlockState)state.with((Property)enumProperty, (Comparable)wallShape);
        }
        return state;
    }

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return PaleMossCarpetBlock.updateState((BlockState)this.getDefaultState(), (BlockView)ctx.getWorld(), (BlockPos)ctx.getBlockPos(), (boolean)true);
    }

    public static void placeAt(WorldAccess world, BlockPos pos, Random random, @Block.SetBlockStateFlag int flags) {
        BlockState blockState = Blocks.PALE_MOSS_CARPET.getDefaultState();
        BlockState blockState2 = PaleMossCarpetBlock.updateState((BlockState)blockState, (BlockView)world, (BlockPos)pos, (boolean)true);
        world.setBlockState(pos, blockState2, flags);
        BlockState blockState3 = PaleMossCarpetBlock.createUpperState((BlockView)world, (BlockPos)pos, () -> ((Random)random).nextBoolean());
        if (!blockState3.isAir()) {
            world.setBlockState(pos.up(), blockState3, flags);
            BlockState blockState4 = PaleMossCarpetBlock.updateState((BlockState)blockState2, (BlockView)world, (BlockPos)pos, (boolean)true);
            world.setBlockState(pos, blockState4, flags);
        }
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.isClient()) {
            return;
        }
        Random random = world.getRandom();
        BlockState blockState = PaleMossCarpetBlock.createUpperState((BlockView)world, (BlockPos)pos, () -> ((Random)random).nextBoolean());
        if (!blockState.isAir()) {
            world.setBlockState(pos.up(), blockState, 3);
        }
    }

    private static BlockState createUpperState(BlockView world, BlockPos pos, BooleanSupplier booleanSupplier) {
        BlockPos blockPos = pos.up();
        BlockState blockState = world.getBlockState(blockPos);
        boolean bl = blockState.isOf(Blocks.PALE_MOSS_CARPET);
        if (bl && ((Boolean)blockState.get((Property)BOTTOM)).booleanValue() || !bl && !blockState.isReplaceable()) {
            return Blocks.AIR.getDefaultState();
        }
        BlockState blockState2 = (BlockState)Blocks.PALE_MOSS_CARPET.getDefaultState().with((Property)BOTTOM, (Comparable)Boolean.valueOf(false));
        BlockState blockState3 = PaleMossCarpetBlock.updateState((BlockState)blockState2, (BlockView)world, (BlockPos)pos.up(), (boolean)true);
        for (Direction direction : Direction.Type.HORIZONTAL) {
            EnumProperty enumProperty = PaleMossCarpetBlock.getWallShape((Direction)direction);
            if (blockState3.get((Property)enumProperty) == WallShape.NONE || booleanSupplier.getAsBoolean()) continue;
            blockState3 = (BlockState)blockState3.with((Property)enumProperty, (Comparable)WallShape.NONE);
        }
        if (PaleMossCarpetBlock.hasAnyShape((BlockState)blockState3) && blockState3 != blockState) {
            return blockState3;
        }
        return Blocks.AIR.getDefaultState();
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        BlockState blockState = PaleMossCarpetBlock.updateState((BlockState)state, (BlockView)world, (BlockPos)pos, (boolean)false);
        if (!PaleMossCarpetBlock.hasAnyShape((BlockState)blockState)) {
            return Blocks.AIR.getDefaultState();
        }
        return blockState;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{BOTTOM, NORTH, EAST, SOUTH, WEST});
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return switch (1.field_54771[rotation.ordinal()]) {
            case 1 -> (BlockState)((BlockState)((BlockState)((BlockState)state.with((Property)NORTH, (Comparable)((WallShape)state.get((Property)SOUTH)))).with((Property)EAST, (Comparable)((WallShape)state.get((Property)WEST)))).with((Property)SOUTH, (Comparable)((WallShape)state.get((Property)NORTH)))).with((Property)WEST, (Comparable)((WallShape)state.get((Property)EAST)));
            case 2 -> (BlockState)((BlockState)((BlockState)((BlockState)state.with((Property)NORTH, (Comparable)((WallShape)state.get((Property)EAST)))).with((Property)EAST, (Comparable)((WallShape)state.get((Property)SOUTH)))).with((Property)SOUTH, (Comparable)((WallShape)state.get((Property)WEST)))).with((Property)WEST, (Comparable)((WallShape)state.get((Property)NORTH)));
            case 3 -> (BlockState)((BlockState)((BlockState)((BlockState)state.with((Property)NORTH, (Comparable)((WallShape)state.get((Property)WEST)))).with((Property)EAST, (Comparable)((WallShape)state.get((Property)NORTH)))).with((Property)SOUTH, (Comparable)((WallShape)state.get((Property)EAST)))).with((Property)WEST, (Comparable)((WallShape)state.get((Property)SOUTH)));
            default -> state;
        };
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return switch (1.field_54772[mirror.ordinal()]) {
            case 1 -> (BlockState)((BlockState)state.with((Property)NORTH, (Comparable)((WallShape)state.get((Property)SOUTH)))).with((Property)SOUTH, (Comparable)((WallShape)state.get((Property)NORTH)));
            case 2 -> (BlockState)((BlockState)state.with((Property)EAST, (Comparable)((WallShape)state.get((Property)WEST)))).with((Property)WEST, (Comparable)((WallShape)state.get((Property)EAST)));
            default -> super.mirror(state, mirror);
        };
    }

    public static @Nullable EnumProperty<WallShape> getWallShape(Direction face) {
        return (EnumProperty)WALL_SHAPE_PROPERTIES_BY_DIRECTION.get(face);
    }

    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return (Boolean)state.get((Property)BOTTOM) != false && !PaleMossCarpetBlock.createUpperState((BlockView)world, (BlockPos)pos, () -> true).isAir();
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        BlockState blockState = PaleMossCarpetBlock.createUpperState((BlockView)world, (BlockPos)pos, () -> true);
        if (!blockState.isAir()) {
            world.setBlockState(pos.up(), blockState, 3);
        }
    }
}

