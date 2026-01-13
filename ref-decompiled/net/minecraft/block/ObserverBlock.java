/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.FacingBlock
 *  net.minecraft.block.ObserverBlock
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.block.OrientationHelper
 *  net.minecraft.world.block.WireOrientation
 *  net.minecraft.world.tick.ScheduledTickView
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.OrientationHelper;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.tick.ScheduledTickView;

public class ObserverBlock
extends FacingBlock {
    public static final MapCodec<ObserverBlock> CODEC = ObserverBlock.createCodec(ObserverBlock::new);
    public static final BooleanProperty POWERED = Properties.POWERED;

    public MapCodec<ObserverBlock> getCodec() {
        return CODEC;
    }

    public ObserverBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)FACING, (Comparable)Direction.SOUTH)).with((Property)POWERED, (Comparable)Boolean.valueOf(false)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, POWERED});
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with((Property)FACING, (Comparable)rotation.rotate((Direction)state.get((Property)FACING)));
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get((Property)FACING)));
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (((Boolean)state.get((Property)POWERED)).booleanValue()) {
            world.setBlockState(pos, (BlockState)state.with((Property)POWERED, (Comparable)Boolean.valueOf(false)), 2);
        } else {
            world.setBlockState(pos, (BlockState)state.with((Property)POWERED, (Comparable)Boolean.valueOf(true)), 2);
            world.scheduleBlockTick(pos, (Block)this, 2);
        }
        this.updateNeighbors((World)world, pos, state);
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (state.get((Property)FACING) == direction && !((Boolean)state.get((Property)POWERED)).booleanValue()) {
            this.scheduleTick(world, tickView, pos);
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    private void scheduleTick(WorldView world, ScheduledTickView tickView, BlockPos pos) {
        if (!world.isClient() && !tickView.getBlockTickScheduler().isQueued(pos, (Object)this)) {
            tickView.scheduleBlockTick(pos, (Block)this, 2);
        }
    }

    protected void updateNeighbors(World world, BlockPos pos, BlockState state) {
        Direction direction = (Direction)state.get((Property)FACING);
        BlockPos blockPos = pos.offset(direction.getOpposite());
        WireOrientation wireOrientation = OrientationHelper.getEmissionOrientation((World)world, (Direction)direction.getOpposite(), null);
        world.updateNeighbor(blockPos, (Block)this, wireOrientation);
        world.updateNeighborsExcept(blockPos, (Block)this, direction, wireOrientation);
    }

    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.getWeakRedstonePower(world, pos, direction);
    }

    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (((Boolean)state.get((Property)POWERED)).booleanValue() && state.get((Property)FACING) == direction) {
            return 15;
        }
        return 0;
    }

    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (state.isOf(oldState.getBlock())) {
            return;
        }
        if (!world.isClient() && ((Boolean)state.get((Property)POWERED)).booleanValue() && !world.getBlockTickScheduler().isQueued(pos, (Object)this)) {
            BlockState blockState = (BlockState)state.with((Property)POWERED, (Comparable)Boolean.valueOf(false));
            world.setBlockState(pos, blockState, 18);
            this.updateNeighbors(world, pos, blockState);
        }
    }

    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        if (((Boolean)state.get((Property)POWERED)).booleanValue() && world.getBlockTickScheduler().isQueued(pos, (Object)this)) {
            this.updateNeighbors((World)world, pos, (BlockState)state.with((Property)POWERED, (Comparable)Boolean.valueOf(false)));
        }
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with((Property)FACING, (Comparable)ctx.getPlayerLookDirection().getOpposite().getOpposite());
    }
}

