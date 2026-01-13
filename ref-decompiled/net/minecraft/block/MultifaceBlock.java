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
 *  net.minecraft.block.Waterloggable
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.state.StateManager
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Axis
 *  net.minecraft.util.math.Direction$Type
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
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
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class MultifaceBlock
extends Block
implements Waterloggable {
    public static final MapCodec<MultifaceBlock> CODEC = MultifaceBlock.createCodec(MultifaceBlock::new);
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private static final Map<Direction, BooleanProperty> FACING_PROPERTIES = ConnectingBlock.FACING_PROPERTIES;
    protected static final Direction[] DIRECTIONS = Direction.values();
    private final Function<BlockState, VoxelShape> shapeFunction;
    private final boolean hasAllHorizontalDirections;
    private final boolean canMirrorX;
    private final boolean canMirrorZ;

    protected MapCodec<? extends MultifaceBlock> getCodec() {
        return CODEC;
    }

    public MultifaceBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(MultifaceBlock.withAllDirections((StateManager)this.stateManager));
        this.shapeFunction = this.createShapeFunction();
        this.hasAllHorizontalDirections = Direction.Type.HORIZONTAL.stream().allMatch(arg_0 -> this.canHaveDirection(arg_0));
        this.canMirrorX = Direction.Type.HORIZONTAL.stream().filter(Direction.Axis.X).filter(arg_0 -> this.canHaveDirection(arg_0)).count() % 2L == 0L;
        this.canMirrorZ = Direction.Type.HORIZONTAL.stream().filter(Direction.Axis.Z).filter(arg_0 -> this.canHaveDirection(arg_0)).count() % 2L == 0L;
    }

    private Function<BlockState, VoxelShape> createShapeFunction() {
        Map map = VoxelShapes.createFacingShapeMap((VoxelShape)Block.createCuboidZShape((double)16.0, (double)0.0, (double)1.0));
        return this.createShapeFunction(state -> {
            VoxelShape voxelShape = VoxelShapes.empty();
            for (Direction direction : DIRECTIONS) {
                if (!MultifaceBlock.hasDirection((BlockState)state, (Direction)direction)) continue;
                voxelShape = VoxelShapes.union((VoxelShape)voxelShape, (VoxelShape)((VoxelShape)map.get(direction)));
            }
            return voxelShape.isEmpty() ? VoxelShapes.fullCube() : voxelShape;
        }, new Property[]{WATERLOGGED});
    }

    public static Set<Direction> collectDirections(BlockState state) {
        if (!(state.getBlock() instanceof MultifaceBlock)) {
            return Set.of();
        }
        EnumSet<Direction> set = EnumSet.noneOf(Direction.class);
        for (Direction direction : Direction.values()) {
            if (!MultifaceBlock.hasDirection((BlockState)state, (Direction)direction)) continue;
            set.add(direction);
        }
        return set;
    }

    public static Set<Direction> flagToDirections(byte flag) {
        EnumSet<Direction> set = EnumSet.noneOf(Direction.class);
        for (Direction direction : Direction.values()) {
            if ((flag & (byte)(1 << direction.ordinal())) <= 0) continue;
            set.add(direction);
        }
        return set;
    }

    public static byte directionsToFlag(Collection<Direction> directions) {
        byte b = 0;
        for (Direction direction : directions) {
            b = (byte)(b | 1 << direction.ordinal());
        }
        return b;
    }

    protected boolean canHaveDirection(Direction direction) {
        return true;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        for (Direction direction : DIRECTIONS) {
            if (!this.canHaveDirection(direction)) continue;
            builder.add(new Property[]{MultifaceBlock.getProperty((Direction)direction)});
        }
        builder.add(new Property[]{WATERLOGGED});
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            tickView.scheduleFluidTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (!MultifaceBlock.hasAnyDirection((BlockState)state)) {
            return Blocks.AIR.getDefaultState();
        }
        if (!MultifaceBlock.hasDirection((BlockState)state, (Direction)direction) || MultifaceBlock.canGrowOn((BlockView)world, (Direction)direction, (BlockPos)neighborPos, (BlockState)neighborState)) {
            return state;
        }
        return MultifaceBlock.disableDirection((BlockState)state, (BooleanProperty)MultifaceBlock.getProperty((Direction)direction));
    }

    protected FluidState getFluidState(BlockState state) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (VoxelShape)this.shapeFunction.apply(state);
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        boolean bl = false;
        for (Direction direction : DIRECTIONS) {
            if (!MultifaceBlock.hasDirection((BlockState)state, (Direction)direction)) continue;
            if (!MultifaceBlock.canGrowOn((BlockView)world, (BlockPos)pos, (Direction)direction)) {
                return false;
            }
            bl = true;
        }
        return bl;
    }

    protected boolean canReplace(BlockState state, ItemPlacementContext context) {
        return !context.getStack().isOf(this.asItem()) || MultifaceBlock.isNotFullBlock((BlockState)state);
    }

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        return Arrays.stream(ctx.getPlacementDirections()).map(direction -> this.withDirection(blockState, (BlockView)world, blockPos, direction)).filter(Objects::nonNull).findFirst().orElse(null);
    }

    public boolean canGrowWithDirection(BlockView world, BlockState state, BlockPos pos, Direction direction) {
        if (!this.canHaveDirection(direction) || state.isOf((Block)this) && MultifaceBlock.hasDirection((BlockState)state, (Direction)direction)) {
            return false;
        }
        BlockPos blockPos = pos.offset(direction);
        return MultifaceBlock.canGrowOn((BlockView)world, (Direction)direction, (BlockPos)blockPos, (BlockState)world.getBlockState(blockPos));
    }

    public @Nullable BlockState withDirection(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (!this.canGrowWithDirection(world, state, pos, direction)) {
            return null;
        }
        BlockState blockState = state.isOf((Block)this) ? state : (state.getFluidState().isEqualAndStill((Fluid)Fluids.WATER) ? (BlockState)this.getDefaultState().with((Property)Properties.WATERLOGGED, (Comparable)Boolean.valueOf(true)) : this.getDefaultState());
        return (BlockState)blockState.with((Property)MultifaceBlock.getProperty((Direction)direction), (Comparable)Boolean.valueOf(true));
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        if (!this.hasAllHorizontalDirections) {
            return state;
        }
        return this.mirror(state, arg_0 -> ((BlockRotation)rotation).rotate(arg_0));
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        if (mirror == BlockMirror.FRONT_BACK && !this.canMirrorX) {
            return state;
        }
        if (mirror == BlockMirror.LEFT_RIGHT && !this.canMirrorZ) {
            return state;
        }
        return this.mirror(state, arg_0 -> ((BlockMirror)mirror).apply(arg_0));
    }

    private BlockState mirror(BlockState state, Function<Direction, Direction> mirror) {
        BlockState blockState = state;
        for (Direction direction : DIRECTIONS) {
            if (!this.canHaveDirection(direction)) continue;
            blockState = (BlockState)blockState.with((Property)MultifaceBlock.getProperty((Direction)mirror.apply(direction)), (Comparable)((Boolean)state.get((Property)MultifaceBlock.getProperty((Direction)direction))));
        }
        return blockState;
    }

    public static boolean hasDirection(BlockState state, Direction direction) {
        BooleanProperty booleanProperty = MultifaceBlock.getProperty((Direction)direction);
        return (Boolean)state.get((Property)booleanProperty, (Comparable)Boolean.valueOf(false));
    }

    public static boolean canGrowOn(BlockView world, BlockPos pos, Direction direction) {
        BlockPos blockPos = pos.offset(direction);
        BlockState blockState = world.getBlockState(blockPos);
        return MultifaceBlock.canGrowOn((BlockView)world, (Direction)direction, (BlockPos)blockPos, (BlockState)blockState);
    }

    public static boolean canGrowOn(BlockView world, Direction direction, BlockPos pos, BlockState state) {
        return Block.isFaceFullSquare((VoxelShape)state.getSidesShape(world, pos), (Direction)direction.getOpposite()) || Block.isFaceFullSquare((VoxelShape)state.getCollisionShape(world, pos), (Direction)direction.getOpposite());
    }

    private static BlockState disableDirection(BlockState state, BooleanProperty direction) {
        BlockState blockState = (BlockState)state.with((Property)direction, (Comparable)Boolean.valueOf(false));
        if (MultifaceBlock.hasAnyDirection((BlockState)blockState)) {
            return blockState;
        }
        return Blocks.AIR.getDefaultState();
    }

    public static BooleanProperty getProperty(Direction direction) {
        return (BooleanProperty)FACING_PROPERTIES.get(direction);
    }

    private static BlockState withAllDirections(StateManager<Block, BlockState> stateManager) {
        BlockState blockState = (BlockState)((BlockState)stateManager.getDefaultState()).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false));
        for (BooleanProperty booleanProperty : FACING_PROPERTIES.values()) {
            blockState = (BlockState)blockState.withIfExists((Property)booleanProperty, (Comparable)Boolean.valueOf(false));
        }
        return blockState;
    }

    protected static boolean hasAnyDirection(BlockState state) {
        for (Direction direction : DIRECTIONS) {
            if (!MultifaceBlock.hasDirection((BlockState)state, (Direction)direction)) continue;
            return true;
        }
        return false;
    }

    private static boolean isNotFullBlock(BlockState state) {
        for (Direction direction : DIRECTIONS) {
            if (MultifaceBlock.hasDirection((BlockState)state, (Direction)direction)) continue;
            return true;
        }
        return false;
    }
}

