/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.AbstractRedstoneGateBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockEntityProvider
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.ComparatorBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.ComparatorBlockEntity
 *  net.minecraft.block.enums.ComparatorMode
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.decoration.ItemFrameEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.RedstoneView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 *  net.minecraft.world.tick.TickPriority
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ComparatorBlockEntity;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.RedstoneView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import net.minecraft.world.tick.TickPriority;
import org.jspecify.annotations.Nullable;

public class ComparatorBlock
extends AbstractRedstoneGateBlock
implements BlockEntityProvider {
    public static final MapCodec<ComparatorBlock> CODEC = ComparatorBlock.createCodec(ComparatorBlock::new);
    public static final EnumProperty<ComparatorMode> MODE = Properties.COMPARATOR_MODE;

    public MapCodec<ComparatorBlock> getCodec() {
        return CODEC;
    }

    public ComparatorBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)FACING, (Comparable)Direction.NORTH)).with((Property)POWERED, (Comparable)Boolean.valueOf(false))).with((Property)MODE, (Comparable)ComparatorMode.COMPARE));
    }

    protected int getUpdateDelayInternal(BlockState state) {
        return 2;
    }

    public BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (direction == Direction.DOWN && !this.canPlaceAbove(world, neighborPos, neighborState)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected int getOutputLevel(BlockView world, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ComparatorBlockEntity) {
            return ((ComparatorBlockEntity)blockEntity).getOutputSignal();
        }
        return 0;
    }

    private int calculateOutputSignal(World world, BlockPos pos, BlockState state) {
        int i = this.getPower(world, pos, state);
        if (i == 0) {
            return 0;
        }
        int j = this.getMaxInputLevelSides((RedstoneView)world, pos, state);
        if (j > i) {
            return 0;
        }
        if (state.get((Property)MODE) == ComparatorMode.SUBTRACT) {
            return i - j;
        }
        return i;
    }

    protected boolean hasPower(World world, BlockPos pos, BlockState state) {
        int i = this.getPower(world, pos, state);
        if (i == 0) {
            return false;
        }
        int j = this.getMaxInputLevelSides((RedstoneView)world, pos, state);
        if (i > j) {
            return true;
        }
        return i == j && state.get((Property)MODE) == ComparatorMode.COMPARE;
    }

    protected int getPower(World world, BlockPos pos, BlockState state) {
        int i = super.getPower(world, pos, state);
        Direction direction = (Direction)state.get((Property)FACING);
        BlockPos blockPos = pos.offset(direction);
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.hasComparatorOutput()) {
            i = blockState.getComparatorOutput(world, blockPos, direction.getOpposite());
        } else if (i < 15 && blockState.isSolidBlock((BlockView)world, blockPos)) {
            blockPos = blockPos.offset(direction);
            blockState = world.getBlockState(blockPos);
            ItemFrameEntity itemFrameEntity = this.getAttachedItemFrame(world, direction, blockPos);
            int j = Math.max(itemFrameEntity == null ? Integer.MIN_VALUE : itemFrameEntity.getComparatorPower(), blockState.hasComparatorOutput() ? blockState.getComparatorOutput(world, blockPos, direction.getOpposite()) : Integer.MIN_VALUE);
            if (j != Integer.MIN_VALUE) {
                i = j;
            }
        }
        return i;
    }

    private @Nullable ItemFrameEntity getAttachedItemFrame(World world, Direction facing, BlockPos pos) {
        List list = world.getEntitiesByClass(ItemFrameEntity.class, new Box((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1)), itemFrame -> itemFrame.getHorizontalFacing() == facing);
        if (list.size() == 1) {
            return (ItemFrameEntity)list.get(0);
        }
        return null;
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!player.getAbilities().allowModifyWorld) {
            return ActionResult.PASS;
        }
        float f = (state = (BlockState)state.cycle((Property)MODE)).get((Property)MODE) == ComparatorMode.SUBTRACT ? 0.55f : 0.5f;
        world.playSound((Entity)player, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3f, f);
        world.setBlockState(pos, state, 2);
        this.update(world, pos, state);
        return ActionResult.SUCCESS;
    }

    protected void updatePowered(World world, BlockPos pos, BlockState state) {
        int j;
        if (world.getBlockTickScheduler().isTicking(pos, (Object)this)) {
            return;
        }
        int i = this.calculateOutputSignal(world, pos, state);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        int n = j = blockEntity instanceof ComparatorBlockEntity ? ((ComparatorBlockEntity)blockEntity).getOutputSignal() : 0;
        if (i != j || ((Boolean)state.get((Property)POWERED)).booleanValue() != this.hasPower(world, pos, state)) {
            TickPriority tickPriority = this.isTargetNotAligned((BlockView)world, pos, state) ? TickPriority.HIGH : TickPriority.NORMAL;
            world.scheduleBlockTick(pos, (Block)this, 2, tickPriority);
        }
    }

    private void update(World world, BlockPos pos, BlockState state) {
        int i = this.calculateOutputSignal(world, pos, state);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        int j = 0;
        if (blockEntity instanceof ComparatorBlockEntity) {
            ComparatorBlockEntity comparatorBlockEntity = (ComparatorBlockEntity)blockEntity;
            j = comparatorBlockEntity.getOutputSignal();
            comparatorBlockEntity.setOutputSignal(i);
        }
        if (j != i || state.get((Property)MODE) == ComparatorMode.COMPARE) {
            boolean bl = this.hasPower(world, pos, state);
            boolean bl2 = (Boolean)state.get((Property)POWERED);
            if (bl2 && !bl) {
                world.setBlockState(pos, (BlockState)state.with((Property)POWERED, (Comparable)Boolean.valueOf(false)), 2);
            } else if (!bl2 && bl) {
                world.setBlockState(pos, (BlockState)state.with((Property)POWERED, (Comparable)Boolean.valueOf(true)), 2);
            }
            this.updateTarget(world, pos, state);
        }
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.update((World)world, pos, state);
    }

    protected boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        super.onSyncedBlockEvent(state, world, pos, type, data);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity != null && blockEntity.onSyncedBlockEvent(type, data);
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ComparatorBlockEntity(pos, state);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, MODE, POWERED});
    }
}

