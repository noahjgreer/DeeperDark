/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.HorizontalFacingBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.TripwireBlock
 *  net.minecraft.block.TripwireHookBlock
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.block.OrientationHelper
 *  net.minecraft.world.block.WireOrientation
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.Optional;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TripwireBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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
import net.minecraft.world.WorldView;
import net.minecraft.world.block.OrientationHelper;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class TripwireHookBlock
extends Block {
    public static final MapCodec<TripwireHookBlock> CODEC = TripwireHookBlock.createCodec(TripwireHookBlock::new);
    public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty ATTACHED = Properties.ATTACHED;
    protected static final int field_31268 = 1;
    protected static final int field_31269 = 42;
    private static final int SCHEDULED_TICK_DELAY = 10;
    private static final Map<Direction, VoxelShape> SHAPES_BY_DIRECTION = VoxelShapes.createHorizontalFacingShapeMap((VoxelShape)Block.createCuboidZShape((double)6.0, (double)0.0, (double)10.0, (double)10.0, (double)16.0));

    public MapCodec<TripwireHookBlock> getCodec() {
        return CODEC;
    }

    public TripwireHookBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)FACING, (Comparable)Direction.NORTH)).with((Property)POWERED, (Comparable)Boolean.valueOf(false))).with((Property)ATTACHED, (Comparable)Boolean.valueOf(false)));
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (VoxelShape)SHAPES_BY_DIRECTION.get(state.get((Property)FACING));
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction direction = (Direction)state.get((Property)FACING);
        BlockPos blockPos = pos.offset(direction.getOpposite());
        BlockState blockState = world.getBlockState(blockPos);
        return direction.getAxis().isHorizontal() && blockState.isSideSolidFullSquare((BlockView)world, blockPos, direction);
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (direction.getOpposite() == state.get((Property)FACING) && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction[] directions;
        BlockState blockState = (BlockState)((BlockState)this.getDefaultState().with((Property)POWERED, (Comparable)Boolean.valueOf(false))).with((Property)ATTACHED, (Comparable)Boolean.valueOf(false));
        World worldView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        for (Direction direction : directions = ctx.getPlacementDirections()) {
            Direction direction2;
            if (!direction.getAxis().isHorizontal() || !(blockState = (BlockState)blockState.with((Property)FACING, (Comparable)(direction2 = direction.getOpposite()))).canPlaceAt((WorldView)worldView, blockPos)) continue;
            return blockState;
        }
        return null;
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        TripwireHookBlock.update((World)world, (BlockPos)pos, (BlockState)state, (boolean)false, (boolean)false, (int)-1, null);
    }

    public static void update(World world, BlockPos pos, BlockState state, boolean bl, boolean bl2, int i, @Nullable BlockState blockState) {
        BlockPos blockPos;
        Optional optional = state.getOrEmpty((Property)FACING);
        if (!optional.isPresent()) {
            return;
        }
        Direction direction = (Direction)optional.get();
        boolean bl3 = state.getOrEmpty((Property)ATTACHED).orElse(false);
        boolean bl4 = state.getOrEmpty((Property)POWERED).orElse(false);
        Block block = state.getBlock();
        boolean bl5 = !bl;
        boolean bl6 = false;
        int j = 0;
        BlockState[] blockStates = new BlockState[42];
        for (int k = 1; k < 42; ++k) {
            blockPos = pos.offset(direction, k);
            BlockState blockState2 = world.getBlockState(blockPos);
            if (blockState2.isOf(Blocks.TRIPWIRE_HOOK)) {
                if (blockState2.get((Property)FACING) != direction.getOpposite()) break;
                j = k;
                break;
            }
            if (blockState2.isOf(Blocks.TRIPWIRE) || k == i) {
                if (k == i) {
                    blockState2 = (BlockState)MoreObjects.firstNonNull((Object)blockState, (Object)blockState2);
                }
                boolean bl7 = (Boolean)blockState2.get((Property)TripwireBlock.DISARMED) == false;
                boolean bl8 = (Boolean)blockState2.get((Property)TripwireBlock.POWERED);
                bl6 |= bl7 && bl8;
                blockStates[k] = blockState2;
                if (k != i) continue;
                world.scheduleBlockTick(pos, block, 10);
                bl5 &= bl7;
                continue;
            }
            blockStates[k] = null;
            bl5 = false;
        }
        BlockState blockState3 = (BlockState)((BlockState)block.getDefaultState().withIfExists((Property)ATTACHED, (Comparable)Boolean.valueOf(bl5))).withIfExists((Property)POWERED, (Comparable)Boolean.valueOf(bl6 &= (bl5 &= j > 1)));
        if (j > 0) {
            blockPos = pos.offset(direction, j);
            Direction direction2 = direction.getOpposite();
            world.setBlockState(blockPos, (BlockState)blockState3.with((Property)FACING, (Comparable)direction2), 3);
            TripwireHookBlock.updateNeighborsOnAxis((Block)block, (World)world, (BlockPos)blockPos, (Direction)direction2);
            TripwireHookBlock.playSound((World)world, (BlockPos)blockPos, (boolean)bl5, (boolean)bl6, (boolean)bl3, (boolean)bl4);
        }
        TripwireHookBlock.playSound((World)world, (BlockPos)pos, (boolean)bl5, (boolean)bl6, (boolean)bl3, (boolean)bl4);
        if (!bl) {
            world.setBlockState(pos, (BlockState)blockState3.with((Property)FACING, (Comparable)direction), 3);
            if (bl2) {
                TripwireHookBlock.updateNeighborsOnAxis((Block)block, (World)world, (BlockPos)pos, (Direction)direction);
            }
        }
        if (bl3 != bl5) {
            for (int l = 1; l < j; ++l) {
                BlockState blockState5;
                BlockPos blockPos2 = pos.offset(direction, l);
                BlockState blockState4 = blockStates[l];
                if (blockState4 == null || !(blockState5 = world.getBlockState(blockPos2)).isOf(Blocks.TRIPWIRE) && !blockState5.isOf(Blocks.TRIPWIRE_HOOK)) continue;
                world.setBlockState(blockPos2, (BlockState)blockState4.withIfExists((Property)ATTACHED, (Comparable)Boolean.valueOf(bl5)), 3);
            }
        }
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        TripwireHookBlock.update((World)world, (BlockPos)pos, (BlockState)state, (boolean)false, (boolean)true, (int)-1, null);
    }

    private static void playSound(World world, BlockPos pos, boolean attached, boolean on, boolean detached, boolean off) {
        if (on && !off) {
            world.playSound(null, pos, SoundEvents.BLOCK_TRIPWIRE_CLICK_ON, SoundCategory.BLOCKS, 0.4f, 0.6f);
            world.emitGameEvent(null, (RegistryEntry)GameEvent.BLOCK_ACTIVATE, pos);
        } else if (!on && off) {
            world.playSound(null, pos, SoundEvents.BLOCK_TRIPWIRE_CLICK_OFF, SoundCategory.BLOCKS, 0.4f, 0.5f);
            world.emitGameEvent(null, (RegistryEntry)GameEvent.BLOCK_DEACTIVATE, pos);
        } else if (attached && !detached) {
            world.playSound(null, pos, SoundEvents.BLOCK_TRIPWIRE_ATTACH, SoundCategory.BLOCKS, 0.4f, 0.7f);
            world.emitGameEvent(null, (RegistryEntry)GameEvent.BLOCK_ATTACH, pos);
        } else if (!attached && detached) {
            world.playSound(null, pos, SoundEvents.BLOCK_TRIPWIRE_DETACH, SoundCategory.BLOCKS, 0.4f, 1.2f / (world.random.nextFloat() * 0.2f + 0.9f));
            world.emitGameEvent(null, (RegistryEntry)GameEvent.BLOCK_DETACH, pos);
        }
    }

    private static void updateNeighborsOnAxis(Block block, World world, BlockPos pos, Direction direction) {
        Direction direction2 = direction.getOpposite();
        WireOrientation wireOrientation = OrientationHelper.getEmissionOrientation((World)world, (Direction)direction2, (Direction)Direction.UP);
        world.updateNeighborsAlways(pos, block, wireOrientation);
        world.updateNeighborsAlways(pos.offset(direction2), block, wireOrientation);
    }

    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        if (moved) {
            return;
        }
        boolean bl = (Boolean)state.get((Property)ATTACHED);
        boolean bl2 = (Boolean)state.get((Property)POWERED);
        if (bl || bl2) {
            TripwireHookBlock.update((World)world, (BlockPos)pos, (BlockState)state, (boolean)true, (boolean)false, (int)-1, null);
        }
        if (bl2) {
            TripwireHookBlock.updateNeighborsOnAxis((Block)this, (World)world, (BlockPos)pos, (Direction)((Direction)state.get((Property)FACING)));
        }
    }

    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return (Boolean)state.get((Property)POWERED) != false ? 15 : 0;
    }

    protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (!((Boolean)state.get((Property)POWERED)).booleanValue()) {
            return 0;
        }
        if (state.get((Property)FACING) == direction) {
            return 15;
        }
        return 0;
    }

    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with((Property)FACING, (Comparable)rotation.rotate((Direction)state.get((Property)FACING)));
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get((Property)FACING)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, POWERED, ATTACHED});
    }
}

