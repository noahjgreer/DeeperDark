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
 *  net.minecraft.block.ConnectingBlock
 *  net.minecraft.block.HorizontalConnectingBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.TripwireBlock
 *  net.minecraft.block.TripwireBlock$1
 *  net.minecraft.block.TripwireHookBlock
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCollisionHandler
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.Items
 *  net.minecraft.registry.Registries
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.tick.ScheduledTickView
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.block.HorizontalConnectingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TripwireBlock;
import net.minecraft.block.TripwireHookBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.ScheduledTickView;

/*
 * Exception performing whole class analysis ignored.
 */
public class TripwireBlock
extends Block {
    public static final MapCodec<TripwireBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Registries.BLOCK.getCodec().fieldOf("hook").forGetter(block -> block.hookBlock), (App)TripwireBlock.createSettingsCodec()).apply((Applicative)instance, TripwireBlock::new));
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty ATTACHED = Properties.ATTACHED;
    public static final BooleanProperty DISARMED = Properties.DISARMED;
    public static final BooleanProperty NORTH = ConnectingBlock.NORTH;
    public static final BooleanProperty EAST = ConnectingBlock.EAST;
    public static final BooleanProperty SOUTH = ConnectingBlock.SOUTH;
    public static final BooleanProperty WEST = ConnectingBlock.WEST;
    private static final Map<Direction, BooleanProperty> FACING_PROPERTIES = HorizontalConnectingBlock.FACING_PROPERTIES;
    private static final VoxelShape ATTACHED_SHAPE = Block.createColumnShape((double)16.0, (double)1.0, (double)2.5);
    private static final VoxelShape UNATTACHED_SHAPE = Block.createColumnShape((double)16.0, (double)0.0, (double)8.0);
    private static final int SCHEDULED_TICK_DELAY = 10;
    private final Block hookBlock;

    public MapCodec<TripwireBlock> getCodec() {
        return CODEC;
    }

    public TripwireBlock(Block hookBlock, AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)POWERED, (Comparable)Boolean.valueOf(false))).with((Property)ATTACHED, (Comparable)Boolean.valueOf(false))).with((Property)DISARMED, (Comparable)Boolean.valueOf(false))).with((Property)NORTH, (Comparable)Boolean.valueOf(false))).with((Property)EAST, (Comparable)Boolean.valueOf(false))).with((Property)SOUTH, (Comparable)Boolean.valueOf(false))).with((Property)WEST, (Comparable)Boolean.valueOf(false)));
        this.hookBlock = hookBlock;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (Boolean)state.get((Property)ATTACHED) != false ? ATTACHED_SHAPE : UNATTACHED_SHAPE;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World blockView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        return (BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with((Property)NORTH, (Comparable)Boolean.valueOf(this.shouldConnectTo(blockView.getBlockState(blockPos.north()), Direction.NORTH)))).with((Property)EAST, (Comparable)Boolean.valueOf(this.shouldConnectTo(blockView.getBlockState(blockPos.east()), Direction.EAST)))).with((Property)SOUTH, (Comparable)Boolean.valueOf(this.shouldConnectTo(blockView.getBlockState(blockPos.south()), Direction.SOUTH)))).with((Property)WEST, (Comparable)Boolean.valueOf(this.shouldConnectTo(blockView.getBlockState(blockPos.west()), Direction.WEST)));
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (direction.getAxis().isHorizontal()) {
            return (BlockState)state.with((Property)FACING_PROPERTIES.get(direction), (Comparable)Boolean.valueOf(this.shouldConnectTo(neighborState, direction)));
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (oldState.isOf(state.getBlock())) {
            return;
        }
        this.update(world, pos, state);
    }

    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        if (!moved) {
            this.update((World)world, pos, (BlockState)state.with((Property)POWERED, (Comparable)Boolean.valueOf(true)));
        }
    }

    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient() && !player.getMainHandStack().isEmpty() && player.getMainHandStack().isOf(Items.SHEARS)) {
            world.setBlockState(pos, (BlockState)state.with((Property)DISARMED, (Comparable)Boolean.valueOf(true)), 260);
            world.emitGameEvent((Entity)player, (RegistryEntry)GameEvent.SHEAR, pos);
        }
        return super.onBreak(world, pos, state, player);
    }

    private void update(World world, BlockPos pos, BlockState state) {
        block0: for (Direction direction : new Direction[]{Direction.SOUTH, Direction.WEST}) {
            for (int i = 1; i < 42; ++i) {
                BlockPos blockPos = pos.offset(direction, i);
                BlockState blockState = world.getBlockState(blockPos);
                if (blockState.isOf(this.hookBlock)) {
                    if (blockState.get((Property)TripwireHookBlock.FACING) != direction.getOpposite()) continue block0;
                    TripwireHookBlock.update((World)world, (BlockPos)blockPos, (BlockState)blockState, (boolean)false, (boolean)true, (int)i, (BlockState)state);
                    continue block0;
                }
                if (!blockState.isOf((Block)this)) continue block0;
            }
        }
    }

    protected VoxelShape getInsideCollisionShape(BlockState state, BlockView world, BlockPos pos, Entity entity) {
        return state.getOutlineShape(world, pos);
    }

    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl) {
        if (world.isClient()) {
            return;
        }
        if (((Boolean)state.get((Property)POWERED)).booleanValue()) {
            return;
        }
        this.updatePowered(world, pos, List.of(entity));
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!((Boolean)world.getBlockState(pos).get((Property)POWERED)).booleanValue()) {
            return;
        }
        this.updatePowered((World)world, pos);
    }

    private void updatePowered(World world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        List list = world.getOtherEntities(null, blockState.getOutlineShape((BlockView)world, pos).getBoundingBox().offset(pos));
        this.updatePowered(world, pos, list);
    }

    private void updatePowered(World world, BlockPos pos, List<? extends Entity> entities) {
        BlockState blockState = world.getBlockState(pos);
        boolean bl = (Boolean)blockState.get((Property)POWERED);
        boolean bl2 = false;
        if (!entities.isEmpty()) {
            for (Entity entity : entities) {
                if (entity.canAvoidTraps()) continue;
                bl2 = true;
                break;
            }
        }
        if (bl2 != bl) {
            blockState = (BlockState)blockState.with((Property)POWERED, (Comparable)Boolean.valueOf(bl2));
            world.setBlockState(pos, blockState, 3);
            this.update(world, pos, blockState);
        }
        if (bl2) {
            world.scheduleBlockTick(new BlockPos((Vec3i)pos), (Block)this, 10);
        }
    }

    public boolean shouldConnectTo(BlockState state, Direction facing) {
        if (state.isOf(this.hookBlock)) {
            return state.get((Property)TripwireHookBlock.FACING) == facing.getOpposite();
        }
        return state.isOf((Block)this);
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        switch (1.field_11685[rotation.ordinal()]) {
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
        switch (1.field_11684[mirror.ordinal()]) {
            case 1: {
                return (BlockState)((BlockState)state.with((Property)NORTH, (Comparable)((Boolean)state.get((Property)SOUTH)))).with((Property)SOUTH, (Comparable)((Boolean)state.get((Property)NORTH)));
            }
            case 2: {
                return (BlockState)((BlockState)state.with((Property)EAST, (Comparable)((Boolean)state.get((Property)WEST)))).with((Property)WEST, (Comparable)((Boolean)state.get((Property)EAST)));
            }
        }
        return super.mirror(state, mirror);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{POWERED, ATTACHED, DISARMED, NORTH, EAST, WEST, SOUTH});
    }
}

