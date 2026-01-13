/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.AbstractRedstoneGateBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.HorizontalFacingBlock
 *  net.minecraft.block.RedstoneWireBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.SideShapeType
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.RedstoneView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.block.OrientationHelper
 *  net.minecraft.world.block.WireOrientation
 *  net.minecraft.world.tick.TickPriority
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SideShapeType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.RedstoneView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.OrientationHelper;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.tick.TickPriority;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public abstract class AbstractRedstoneGateBlock
extends HorizontalFacingBlock {
    public static final BooleanProperty POWERED = Properties.POWERED;
    private static final VoxelShape SHAPE = Block.createColumnShape((double)16.0, (double)0.0, (double)2.0);

    protected AbstractRedstoneGateBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    protected abstract MapCodec<? extends AbstractRedstoneGateBlock> getCodec();

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        return this.canPlaceAbove(world, blockPos, world.getBlockState(blockPos));
    }

    protected boolean canPlaceAbove(WorldView world, BlockPos pos, BlockState state) {
        return state.isSideSolid((BlockView)world, pos, Direction.UP, SideShapeType.RIGID);
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (this.isLocked((WorldView)world, pos, state)) {
            return;
        }
        boolean bl = (Boolean)state.get((Property)POWERED);
        boolean bl2 = this.hasPower((World)world, pos, state);
        if (bl && !bl2) {
            world.setBlockState(pos, (BlockState)state.with((Property)POWERED, (Comparable)Boolean.valueOf(false)), 2);
        } else if (!bl) {
            world.setBlockState(pos, (BlockState)state.with((Property)POWERED, (Comparable)Boolean.valueOf(true)), 2);
            if (!bl2) {
                world.scheduleBlockTick(pos, (Block)this, this.getUpdateDelayInternal(state), TickPriority.VERY_HIGH);
            }
        }
    }

    protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.getWeakRedstonePower(world, pos, direction);
    }

    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (!((Boolean)state.get((Property)POWERED)).booleanValue()) {
            return 0;
        }
        if (state.get((Property)FACING) == direction) {
            return this.getOutputLevel(world, pos, state);
        }
        return 0;
    }

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (state.canPlaceAt((WorldView)world, pos)) {
            this.updatePowered(world, pos, state);
            return;
        }
        BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
        AbstractRedstoneGateBlock.dropStacks((BlockState)state, (WorldAccess)world, (BlockPos)pos, (BlockEntity)blockEntity);
        world.removeBlock(pos, false);
        for (Direction direction : Direction.values()) {
            world.updateNeighbors(pos.offset(direction), (Block)this);
        }
    }

    protected void updatePowered(World world, BlockPos pos, BlockState state) {
        boolean bl2;
        if (this.isLocked((WorldView)world, pos, state)) {
            return;
        }
        boolean bl = (Boolean)state.get((Property)POWERED);
        if (bl != (bl2 = this.hasPower(world, pos, state)) && !world.getBlockTickScheduler().isTicking(pos, (Object)this)) {
            TickPriority tickPriority = TickPriority.HIGH;
            if (this.isTargetNotAligned((BlockView)world, pos, state)) {
                tickPriority = TickPriority.EXTREMELY_HIGH;
            } else if (bl) {
                tickPriority = TickPriority.VERY_HIGH;
            }
            world.scheduleBlockTick(pos, (Block)this, this.getUpdateDelayInternal(state), tickPriority);
        }
    }

    public boolean isLocked(WorldView world, BlockPos pos, BlockState state) {
        return false;
    }

    protected boolean hasPower(World world, BlockPos pos, BlockState state) {
        return this.getPower(world, pos, state) > 0;
    }

    protected int getPower(World world, BlockPos pos, BlockState state) {
        Direction direction = (Direction)state.get((Property)FACING);
        BlockPos blockPos = pos.offset(direction);
        int i = world.getEmittedRedstonePower(blockPos, direction);
        if (i >= 15) {
            return i;
        }
        BlockState blockState = world.getBlockState(blockPos);
        return Math.max(i, blockState.isOf(Blocks.REDSTONE_WIRE) ? (Integer)blockState.get((Property)RedstoneWireBlock.POWER) : 0);
    }

    protected int getMaxInputLevelSides(RedstoneView world, BlockPos pos, BlockState state) {
        Direction direction = (Direction)state.get((Property)FACING);
        Direction direction2 = direction.rotateYClockwise();
        Direction direction3 = direction.rotateYCounterclockwise();
        boolean bl = this.getSideInputFromGatesOnly();
        return Math.max(world.getEmittedRedstonePower(pos.offset(direction2), direction2, bl), world.getEmittedRedstonePower(pos.offset(direction3), direction3, bl));
    }

    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with((Property)FACING, (Comparable)ctx.getHorizontalPlayerFacing().getOpposite());
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (this.hasPower(world, pos, state)) {
            world.scheduleBlockTick(pos, (Block)this, 1);
        }
    }

    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        this.updateTarget(world, pos, state);
    }

    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        if (!moved) {
            this.updateTarget((World)world, pos, state);
        }
    }

    protected void updateTarget(World world, BlockPos pos, BlockState state) {
        Direction direction = (Direction)state.get((Property)FACING);
        BlockPos blockPos = pos.offset(direction.getOpposite());
        WireOrientation wireOrientation = OrientationHelper.getEmissionOrientation((World)world, (Direction)direction.getOpposite(), (Direction)Direction.UP);
        world.updateNeighbor(blockPos, (Block)this, wireOrientation);
        world.updateNeighborsExcept(blockPos, (Block)this, direction, wireOrientation);
    }

    protected boolean getSideInputFromGatesOnly() {
        return false;
    }

    protected int getOutputLevel(BlockView world, BlockPos pos, BlockState state) {
        return 15;
    }

    public static boolean isRedstoneGate(BlockState state) {
        return state.getBlock() instanceof AbstractRedstoneGateBlock;
    }

    public boolean isTargetNotAligned(BlockView world, BlockPos pos, BlockState state) {
        Direction direction = ((Direction)state.get((Property)FACING)).getOpposite();
        BlockState blockState = world.getBlockState(pos.offset(direction));
        return AbstractRedstoneGateBlock.isRedstoneGate((BlockState)blockState) && blockState.get((Property)FACING) != direction;
    }

    protected abstract int getUpdateDelayInternal(BlockState var1);
}

