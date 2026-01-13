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
 *  net.minecraft.block.ObserverBlock
 *  net.minecraft.block.RedstoneWireBlock
 *  net.minecraft.block.RedstoneWireBlock$1
 *  net.minecraft.block.RepeaterBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.TrapdoorBlock
 *  net.minecraft.block.enums.WireConnection
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.particle.DustParticleEffect
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.resource.featuretoggle.FeatureFlags
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.Util
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$Mutable
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Type
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.DefaultRedstoneController
 *  net.minecraft.world.ExperimentalRedstoneController
 *  net.minecraft.world.RedstoneController
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.block.OrientationHelper
 *  net.minecraft.world.block.WireOrientation
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
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
import net.minecraft.block.Blocks;
import net.minecraft.block.ObserverBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.DefaultRedstoneController;
import net.minecraft.world.ExperimentalRedstoneController;
import net.minecraft.world.RedstoneController;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.OrientationHelper;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class RedstoneWireBlock
extends Block {
    public static final MapCodec<RedstoneWireBlock> CODEC = RedstoneWireBlock.createCodec(RedstoneWireBlock::new);
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_NORTH = Properties.NORTH_WIRE_CONNECTION;
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_EAST = Properties.EAST_WIRE_CONNECTION;
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_SOUTH = Properties.SOUTH_WIRE_CONNECTION;
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_WEST = Properties.WEST_WIRE_CONNECTION;
    public static final IntProperty POWER = Properties.POWER;
    public static final Map<Direction, EnumProperty<WireConnection>> DIRECTION_TO_WIRE_CONNECTION_PROPERTY = ImmutableMap.copyOf((Map)Maps.newEnumMap(Map.of(Direction.NORTH, WIRE_CONNECTION_NORTH, Direction.EAST, WIRE_CONNECTION_EAST, Direction.SOUTH, WIRE_CONNECTION_SOUTH, Direction.WEST, WIRE_CONNECTION_WEST)));
    private static final int[] COLORS = (int[])Util.make((Object)new int[16], colors -> {
        for (int i = 0; i <= 15; ++i) {
            float f;
            float g = f * 0.6f + ((f = (float)i / 15.0f) > 0.0f ? 0.4f : 0.3f);
            float h = MathHelper.clamp((float)(f * f * 0.7f - 0.5f), (float)0.0f, (float)1.0f);
            float j = MathHelper.clamp((float)(f * f * 0.6f - 0.7f), (float)0.0f, (float)1.0f);
            colors[i] = ColorHelper.fromFloats((float)1.0f, (float)g, (float)h, (float)j);
        }
    });
    private static final float field_31221 = 0.2f;
    private final Function<BlockState, VoxelShape> shapeFunction;
    private final BlockState dotState;
    private final RedstoneController redstoneController = new DefaultRedstoneController(this);
    private boolean wiresGivePower = true;

    public MapCodec<RedstoneWireBlock> getCodec() {
        return CODEC;
    }

    public RedstoneWireBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)WIRE_CONNECTION_NORTH, (Comparable)WireConnection.NONE)).with((Property)WIRE_CONNECTION_EAST, (Comparable)WireConnection.NONE)).with((Property)WIRE_CONNECTION_SOUTH, (Comparable)WireConnection.NONE)).with((Property)WIRE_CONNECTION_WEST, (Comparable)WireConnection.NONE)).with((Property)POWER, (Comparable)Integer.valueOf(0)));
        this.shapeFunction = this.createShapeFunction();
        this.dotState = (BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with((Property)WIRE_CONNECTION_NORTH, (Comparable)WireConnection.SIDE)).with((Property)WIRE_CONNECTION_EAST, (Comparable)WireConnection.SIDE)).with((Property)WIRE_CONNECTION_SOUTH, (Comparable)WireConnection.SIDE)).with((Property)WIRE_CONNECTION_WEST, (Comparable)WireConnection.SIDE);
    }

    private Function<BlockState, VoxelShape> createShapeFunction() {
        boolean i = true;
        int j = 10;
        VoxelShape voxelShape = Block.createColumnShape((double)10.0, (double)0.0, (double)1.0);
        Map map = VoxelShapes.createHorizontalFacingShapeMap((VoxelShape)Block.createCuboidZShape((double)10.0, (double)0.0, (double)1.0, (double)0.0, (double)8.0));
        Map map2 = VoxelShapes.createHorizontalFacingShapeMap((VoxelShape)Block.createCuboidZShape((double)10.0, (double)16.0, (double)0.0, (double)1.0));
        return this.createShapeFunction(state -> {
            VoxelShape voxelShape2 = voxelShape;
            for (Map.Entry entry : DIRECTION_TO_WIRE_CONNECTION_PROPERTY.entrySet()) {
                voxelShape2 = switch (1.field_24467[((WireConnection)state.get((Property)entry.getValue())).ordinal()]) {
                    default -> throw new MatchException(null, null);
                    case 1 -> VoxelShapes.union((VoxelShape)voxelShape2, (VoxelShape[])new VoxelShape[]{(VoxelShape)map.get(entry.getKey()), (VoxelShape)map2.get(entry.getKey())});
                    case 2 -> VoxelShapes.union((VoxelShape)voxelShape2, (VoxelShape)((VoxelShape)map.get(entry.getKey())));
                    case 3 -> voxelShape2;
                };
            }
            return voxelShape2;
        }, new Property[]{POWER});
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (VoxelShape)this.shapeFunction.apply(state);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getPlacementState((BlockView)ctx.getWorld(), this.dotState, ctx.getBlockPos());
    }

    private BlockState getPlacementState(BlockView world, BlockState state, BlockPos pos) {
        boolean bl7;
        boolean bl = RedstoneWireBlock.isNotConnected((BlockState)state);
        state = this.getDefaultWireState(world, (BlockState)this.getDefaultState().with((Property)POWER, (Comparable)((Integer)state.get((Property)POWER))), pos);
        if (bl && RedstoneWireBlock.isNotConnected((BlockState)state)) {
            return state;
        }
        boolean bl2 = ((WireConnection)state.get((Property)WIRE_CONNECTION_NORTH)).isConnected();
        boolean bl3 = ((WireConnection)state.get((Property)WIRE_CONNECTION_SOUTH)).isConnected();
        boolean bl4 = ((WireConnection)state.get((Property)WIRE_CONNECTION_EAST)).isConnected();
        boolean bl5 = ((WireConnection)state.get((Property)WIRE_CONNECTION_WEST)).isConnected();
        boolean bl6 = !bl2 && !bl3;
        boolean bl8 = bl7 = !bl4 && !bl5;
        if (!bl5 && bl6) {
            state = (BlockState)state.with((Property)WIRE_CONNECTION_WEST, (Comparable)WireConnection.SIDE);
        }
        if (!bl4 && bl6) {
            state = (BlockState)state.with((Property)WIRE_CONNECTION_EAST, (Comparable)WireConnection.SIDE);
        }
        if (!bl2 && bl7) {
            state = (BlockState)state.with((Property)WIRE_CONNECTION_NORTH, (Comparable)WireConnection.SIDE);
        }
        if (!bl3 && bl7) {
            state = (BlockState)state.with((Property)WIRE_CONNECTION_SOUTH, (Comparable)WireConnection.SIDE);
        }
        return state;
    }

    private BlockState getDefaultWireState(BlockView world, BlockState state, BlockPos pos) {
        boolean bl = !world.getBlockState(pos.up()).isSolidBlock(world, pos);
        for (Direction direction : Direction.Type.HORIZONTAL) {
            if (((WireConnection)state.get((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction))).isConnected()) continue;
            WireConnection wireConnection = this.getRenderConnectionType(world, pos, direction, bl);
            state = (BlockState)state.with((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), (Comparable)wireConnection);
        }
        return state;
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (direction == Direction.DOWN) {
            if (!this.canRunOnTop((BlockView)world, neighborPos, neighborState)) {
                return Blocks.AIR.getDefaultState();
            }
            return state;
        }
        if (direction == Direction.UP) {
            return this.getPlacementState((BlockView)world, state, pos);
        }
        WireConnection wireConnection = this.getRenderConnectionType((BlockView)world, pos, direction);
        if (wireConnection.isConnected() == ((WireConnection)state.get((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction))).isConnected() && !RedstoneWireBlock.isFullyConnected((BlockState)state)) {
            return (BlockState)state.with((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), (Comparable)wireConnection);
        }
        return this.getPlacementState((BlockView)world, (BlockState)((BlockState)this.dotState.with((Property)POWER, (Comparable)((Integer)state.get((Property)POWER)))).with((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), (Comparable)wireConnection), pos);
    }

    private static boolean isFullyConnected(BlockState state) {
        return ((WireConnection)state.get((Property)WIRE_CONNECTION_NORTH)).isConnected() && ((WireConnection)state.get((Property)WIRE_CONNECTION_SOUTH)).isConnected() && ((WireConnection)state.get((Property)WIRE_CONNECTION_EAST)).isConnected() && ((WireConnection)state.get((Property)WIRE_CONNECTION_WEST)).isConnected();
    }

    private static boolean isNotConnected(BlockState state) {
        return !((WireConnection)state.get((Property)WIRE_CONNECTION_NORTH)).isConnected() && !((WireConnection)state.get((Property)WIRE_CONNECTION_SOUTH)).isConnected() && !((WireConnection)state.get((Property)WIRE_CONNECTION_EAST)).isConnected() && !((WireConnection)state.get((Property)WIRE_CONNECTION_WEST)).isConnected();
    }

    protected void prepare(BlockState state, WorldAccess world, BlockPos pos, @Block.SetBlockStateFlag int flags, int maxUpdateDepth) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (Direction direction : Direction.Type.HORIZONTAL) {
            WireConnection wireConnection = (WireConnection)state.get((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
            if (wireConnection == WireConnection.NONE || world.getBlockState((BlockPos)mutable.set((Vec3i)pos, direction)).isOf((Block)this)) continue;
            mutable.move(Direction.DOWN);
            BlockState blockState = world.getBlockState((BlockPos)mutable);
            if (blockState.isOf((Block)this)) {
                BlockPos blockPos = mutable.offset(direction.getOpposite());
                world.replaceWithStateForNeighborUpdate(direction.getOpposite(), (BlockPos)mutable, blockPos, world.getBlockState(blockPos), flags, maxUpdateDepth);
            }
            mutable.set((Vec3i)pos, direction).move(Direction.UP);
            BlockState blockState2 = world.getBlockState((BlockPos)mutable);
            if (!blockState2.isOf((Block)this)) continue;
            BlockPos blockPos2 = mutable.offset(direction.getOpposite());
            world.replaceWithStateForNeighborUpdate(direction.getOpposite(), (BlockPos)mutable, blockPos2, world.getBlockState(blockPos2), flags, maxUpdateDepth);
        }
    }

    private WireConnection getRenderConnectionType(BlockView world, BlockPos pos, Direction direction) {
        return this.getRenderConnectionType(world, pos, direction, !world.getBlockState(pos.up()).isSolidBlock(world, pos));
    }

    private WireConnection getRenderConnectionType(BlockView world, BlockPos pos, Direction direction, boolean bl) {
        BlockPos blockPos = pos.offset(direction);
        BlockState blockState = world.getBlockState(blockPos);
        if (bl) {
            boolean bl2;
            boolean bl3 = bl2 = blockState.getBlock() instanceof TrapdoorBlock || this.canRunOnTop(world, blockPos, blockState);
            if (bl2 && RedstoneWireBlock.connectsTo((BlockState)world.getBlockState(blockPos.up()))) {
                if (blockState.isSideSolidFullSquare(world, blockPos, direction.getOpposite())) {
                    return WireConnection.UP;
                }
                return WireConnection.SIDE;
            }
        }
        if (RedstoneWireBlock.connectsTo((BlockState)blockState, (Direction)direction) || !blockState.isSolidBlock(world, blockPos) && RedstoneWireBlock.connectsTo((BlockState)world.getBlockState(blockPos.down()))) {
            return WireConnection.SIDE;
        }
        return WireConnection.NONE;
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        return this.canRunOnTop((BlockView)world, blockPos, blockState);
    }

    private boolean canRunOnTop(BlockView world, BlockPos pos, BlockState floor) {
        return floor.isSideSolidFullSquare(world, pos, Direction.UP) || floor.isOf(Blocks.HOPPER);
    }

    private void update(World world, BlockPos pos, BlockState state, @Nullable WireOrientation orientation, boolean blockAdded) {
        if (RedstoneWireBlock.areRedstoneExperimentsEnabled((World)world)) {
            new ExperimentalRedstoneController(this).update(world, pos, state, orientation, blockAdded);
        } else {
            this.redstoneController.update(world, pos, state, orientation, blockAdded);
        }
    }

    public int getStrongPower(World world, BlockPos pos) {
        this.wiresGivePower = false;
        int i = world.getReceivedRedstonePower(pos);
        this.wiresGivePower = true;
        return i;
    }

    private void updateNeighbors(World world, BlockPos pos) {
        if (!world.getBlockState(pos).isOf((Block)this)) {
            return;
        }
        world.updateNeighbors(pos, (Block)this);
        for (Direction direction : Direction.values()) {
            world.updateNeighbors(pos.offset(direction), (Block)this);
        }
    }

    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (oldState.isOf(state.getBlock()) || world.isClient()) {
            return;
        }
        this.update(world, pos, state, null, true);
        for (Direction direction : Direction.Type.VERTICAL) {
            world.updateNeighbors(pos.offset(direction), (Block)this);
        }
        this.updateOffsetNeighbors(world, pos);
    }

    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        if (moved) {
            return;
        }
        for (Direction direction : Direction.values()) {
            world.updateNeighbors(pos.offset(direction), (Block)this);
        }
        this.update((World)world, pos, state, null, false);
        this.updateOffsetNeighbors((World)world, pos);
    }

    private void updateOffsetNeighbors(World world, BlockPos pos) {
        for (Direction direction : Direction.Type.HORIZONTAL) {
            this.updateNeighbors(world, pos.offset(direction));
        }
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos blockPos = pos.offset(direction);
            if (world.getBlockState(blockPos).isSolidBlock((BlockView)world, blockPos)) {
                this.updateNeighbors(world, blockPos.up());
                continue;
            }
            this.updateNeighbors(world, blockPos.down());
        }
    }

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (world.isClient()) {
            return;
        }
        if (sourceBlock == this && RedstoneWireBlock.areRedstoneExperimentsEnabled((World)world)) {
            return;
        }
        if (state.canPlaceAt((WorldView)world, pos)) {
            this.update(world, pos, state, wireOrientation, false);
        } else {
            RedstoneWireBlock.dropStacks((BlockState)state, (World)world, (BlockPos)pos);
            world.removeBlock(pos, false);
        }
    }

    private static boolean areRedstoneExperimentsEnabled(World world) {
        return world.getEnabledFeatures().contains(FeatureFlags.REDSTONE_EXPERIMENTS);
    }

    protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (!this.wiresGivePower) {
            return 0;
        }
        return state.getWeakRedstonePower(world, pos, direction);
    }

    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (!this.wiresGivePower || direction == Direction.DOWN) {
            return 0;
        }
        int i = (Integer)state.get((Property)POWER);
        if (i == 0) {
            return 0;
        }
        if (direction == Direction.UP || ((WireConnection)this.getPlacementState(world, state, pos).get((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction.getOpposite()))).isConnected()) {
            return i;
        }
        return 0;
    }

    protected static boolean connectsTo(BlockState state) {
        return RedstoneWireBlock.connectsTo((BlockState)state, null);
    }

    protected static boolean connectsTo(BlockState state, @Nullable Direction dir) {
        if (state.isOf(Blocks.REDSTONE_WIRE)) {
            return true;
        }
        if (state.isOf(Blocks.REPEATER)) {
            Direction direction = (Direction)state.get((Property)RepeaterBlock.FACING);
            return direction == dir || direction.getOpposite() == dir;
        }
        if (state.isOf(Blocks.OBSERVER)) {
            return dir == state.get((Property)ObserverBlock.FACING);
        }
        return state.emitsRedstonePower() && dir != null;
    }

    protected boolean emitsRedstonePower(BlockState state) {
        return this.wiresGivePower;
    }

    public static int getWireColor(int powerLevel) {
        return COLORS[powerLevel];
    }

    private static void addPoweredParticles(World world, Random random, BlockPos pos, int color, Direction perpendicular, Direction direction, float minOffset, float maxOffset) {
        float f = maxOffset - minOffset;
        if (random.nextFloat() >= 0.2f * f) {
            return;
        }
        float g = 0.4375f;
        float h = minOffset + f * random.nextFloat();
        double d = 0.5 + (double)(0.4375f * (float)perpendicular.getOffsetX()) + (double)(h * (float)direction.getOffsetX());
        double e = 0.5 + (double)(0.4375f * (float)perpendicular.getOffsetY()) + (double)(h * (float)direction.getOffsetY());
        double i = 0.5 + (double)(0.4375f * (float)perpendicular.getOffsetZ()) + (double)(h * (float)direction.getOffsetZ());
        world.addParticleClient((ParticleEffect)new DustParticleEffect(color, 1.0f), (double)pos.getX() + d, (double)pos.getY() + e, (double)pos.getZ() + i, 0.0, 0.0, 0.0);
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        int i = (Integer)state.get((Property)POWER);
        if (i == 0) {
            return;
        }
        block4: for (Direction direction : Direction.Type.HORIZONTAL) {
            WireConnection wireConnection = (WireConnection)state.get((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
            switch (1.field_24467[wireConnection.ordinal()]) {
                case 1: {
                    RedstoneWireBlock.addPoweredParticles((World)world, (Random)random, (BlockPos)pos, (int)COLORS[i], (Direction)direction, (Direction)Direction.UP, (float)-0.5f, (float)0.5f);
                }
                case 2: {
                    RedstoneWireBlock.addPoweredParticles((World)world, (Random)random, (BlockPos)pos, (int)COLORS[i], (Direction)Direction.DOWN, (Direction)direction, (float)0.0f, (float)0.5f);
                    continue block4;
                }
            }
            RedstoneWireBlock.addPoweredParticles((World)world, (Random)random, (BlockPos)pos, (int)COLORS[i], (Direction)Direction.DOWN, (Direction)direction, (float)0.0f, (float)0.3f);
        }
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        switch (1.field_11442[rotation.ordinal()]) {
            case 1: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with((Property)WIRE_CONNECTION_NORTH, (Comparable)((WireConnection)state.get((Property)WIRE_CONNECTION_SOUTH)))).with((Property)WIRE_CONNECTION_EAST, (Comparable)((WireConnection)state.get((Property)WIRE_CONNECTION_WEST)))).with((Property)WIRE_CONNECTION_SOUTH, (Comparable)((WireConnection)state.get((Property)WIRE_CONNECTION_NORTH)))).with((Property)WIRE_CONNECTION_WEST, (Comparable)((WireConnection)state.get((Property)WIRE_CONNECTION_EAST)));
            }
            case 2: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with((Property)WIRE_CONNECTION_NORTH, (Comparable)((WireConnection)state.get((Property)WIRE_CONNECTION_EAST)))).with((Property)WIRE_CONNECTION_EAST, (Comparable)((WireConnection)state.get((Property)WIRE_CONNECTION_SOUTH)))).with((Property)WIRE_CONNECTION_SOUTH, (Comparable)((WireConnection)state.get((Property)WIRE_CONNECTION_WEST)))).with((Property)WIRE_CONNECTION_WEST, (Comparable)((WireConnection)state.get((Property)WIRE_CONNECTION_NORTH)));
            }
            case 3: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with((Property)WIRE_CONNECTION_NORTH, (Comparable)((WireConnection)state.get((Property)WIRE_CONNECTION_WEST)))).with((Property)WIRE_CONNECTION_EAST, (Comparable)((WireConnection)state.get((Property)WIRE_CONNECTION_NORTH)))).with((Property)WIRE_CONNECTION_SOUTH, (Comparable)((WireConnection)state.get((Property)WIRE_CONNECTION_EAST)))).with((Property)WIRE_CONNECTION_WEST, (Comparable)((WireConnection)state.get((Property)WIRE_CONNECTION_SOUTH)));
            }
        }
        return state;
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        switch (1.field_11441[mirror.ordinal()]) {
            case 1: {
                return (BlockState)((BlockState)state.with((Property)WIRE_CONNECTION_NORTH, (Comparable)((WireConnection)state.get((Property)WIRE_CONNECTION_SOUTH)))).with((Property)WIRE_CONNECTION_SOUTH, (Comparable)((WireConnection)state.get((Property)WIRE_CONNECTION_NORTH)));
            }
            case 2: {
                return (BlockState)((BlockState)state.with((Property)WIRE_CONNECTION_EAST, (Comparable)((WireConnection)state.get((Property)WIRE_CONNECTION_WEST)))).with((Property)WIRE_CONNECTION_WEST, (Comparable)((WireConnection)state.get((Property)WIRE_CONNECTION_EAST)));
            }
        }
        return super.mirror(state, mirror);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{WIRE_CONNECTION_NORTH, WIRE_CONNECTION_EAST, WIRE_CONNECTION_SOUTH, WIRE_CONNECTION_WEST, POWER});
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!player.getAbilities().allowModifyWorld) {
            return ActionResult.PASS;
        }
        if (RedstoneWireBlock.isFullyConnected((BlockState)state) || RedstoneWireBlock.isNotConnected((BlockState)state)) {
            BlockState blockState = RedstoneWireBlock.isFullyConnected((BlockState)state) ? this.getDefaultState() : this.dotState;
            blockState = (BlockState)blockState.with((Property)POWER, (Comparable)((Integer)state.get((Property)POWER)));
            if ((blockState = this.getPlacementState((BlockView)world, blockState, pos)) != state) {
                world.setBlockState(pos, blockState, 3);
                this.updateForNewState(world, pos, state, blockState);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    private void updateForNewState(World world, BlockPos pos, BlockState oldState, BlockState newState) {
        WireOrientation wireOrientation = OrientationHelper.getEmissionOrientation((World)world, null, (Direction)Direction.UP);
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos blockPos = pos.offset(direction);
            if (((WireConnection)oldState.get((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction))).isConnected() == ((WireConnection)newState.get((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction))).isConnected() || !world.getBlockState(blockPos).isSolidBlock((BlockView)world, blockPos)) continue;
            world.updateNeighborsExcept(blockPos, newState.getBlock(), direction.getOpposite(), OrientationHelper.withFrontNullable((WireOrientation)wireOrientation, (Direction)direction));
        }
    }
}

