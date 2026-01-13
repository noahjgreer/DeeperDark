/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.FacingBlock
 *  net.minecraft.block.PistonBlock
 *  net.minecraft.block.PistonBlock$1
 *  net.minecraft.block.PistonExtensionBlock
 *  net.minecraft.block.PistonHeadBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.PistonBlockEntity
 *  net.minecraft.block.enums.PistonType
 *  net.minecraft.block.piston.PistonBehavior
 *  net.minecraft.block.piston.PistonHandler
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.RedstoneView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.block.OrientationHelper
 *  net.minecraft.world.block.WireOrientation
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.event.GameEvent$Emitter
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonExtensionBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.RedstoneView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.block.OrientationHelper;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.event.GameEvent;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class PistonBlock
extends FacingBlock {
    public static final MapCodec<PistonBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.BOOL.fieldOf("sticky").forGetter(block -> block.sticky), (App)PistonBlock.createSettingsCodec()).apply((Applicative)instance, PistonBlock::new));
    public static final BooleanProperty EXTENDED = Properties.EXTENDED;
    public static final int field_31373 = 0;
    public static final int field_31374 = 1;
    public static final int field_31375 = 2;
    public static final int field_31376 = 4;
    private static final Map<Direction, VoxelShape> EXTENDED_SHAPES_BY_DIRECTION = VoxelShapes.createFacingShapeMap((VoxelShape)Block.createCuboidZShape((double)16.0, (double)4.0, (double)16.0));
    private final boolean sticky;

    public MapCodec<PistonBlock> getCodec() {
        return CODEC;
    }

    public PistonBlock(boolean sticky, AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)FACING, (Comparable)Direction.NORTH)).with((Property)EXTENDED, (Comparable)Boolean.valueOf(false)));
        this.sticky = sticky;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (((Boolean)state.get((Property)EXTENDED)).booleanValue()) {
            return (VoxelShape)EXTENDED_SHAPES_BY_DIRECTION.get(state.get((Property)FACING));
        }
        return VoxelShapes.fullCube();
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient()) {
            this.tryMove(world, pos, state);
        }
    }

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (!world.isClient()) {
            this.tryMove(world, pos, state);
        }
    }

    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (oldState.isOf(state.getBlock())) {
            return;
        }
        if (!world.isClient() && world.getBlockEntity(pos) == null) {
            this.tryMove(world, pos, state);
        }
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)((BlockState)this.getDefaultState().with((Property)FACING, (Comparable)ctx.getPlayerLookDirection().getOpposite())).with((Property)EXTENDED, (Comparable)Boolean.valueOf(false));
    }

    private void tryMove(World world, BlockPos pos, BlockState state) {
        Direction direction = (Direction)state.get((Property)FACING);
        boolean bl = this.shouldExtend((RedstoneView)world, pos, direction);
        if (bl && !((Boolean)state.get((Property)EXTENDED)).booleanValue()) {
            if (new PistonHandler(world, pos, direction, true).calculatePush()) {
                world.addSyncedBlockEvent(pos, (Block)this, 0, direction.getIndex());
            }
        } else if (!bl && ((Boolean)state.get((Property)EXTENDED)).booleanValue()) {
            PistonBlockEntity pistonBlockEntity;
            BlockEntity blockEntity;
            BlockPos blockPos = pos.offset(direction, 2);
            BlockState blockState = world.getBlockState(blockPos);
            int i = 1;
            if (blockState.isOf(Blocks.MOVING_PISTON) && blockState.get((Property)FACING) == direction && (blockEntity = world.getBlockEntity(blockPos)) instanceof PistonBlockEntity && (pistonBlockEntity = (PistonBlockEntity)blockEntity).isExtending() && (pistonBlockEntity.getProgress(0.0f) < 0.5f || world.getTime() == pistonBlockEntity.getSavedWorldTime() || ((ServerWorld)world).isInBlockTick())) {
                i = 2;
            }
            world.addSyncedBlockEvent(pos, (Block)this, i, direction.getIndex());
        }
    }

    private boolean shouldExtend(RedstoneView world, BlockPos pos, Direction pistonFace) {
        for (Direction direction : Direction.values()) {
            if (direction == pistonFace || !world.isEmittingRedstonePower(pos.offset(direction), direction)) continue;
            return true;
        }
        if (world.isEmittingRedstonePower(pos, Direction.DOWN)) {
            return true;
        }
        BlockPos blockPos = pos.up();
        for (Direction direction2 : Direction.values()) {
            if (direction2 == Direction.DOWN || !world.isEmittingRedstonePower(blockPos.offset(direction2), direction2)) continue;
            return true;
        }
        return false;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        Direction direction = (Direction)state.get((Property)FACING);
        BlockState blockState = (BlockState)state.with((Property)EXTENDED, (Comparable)Boolean.valueOf(true));
        if (!world.isClient()) {
            boolean bl = this.shouldExtend((RedstoneView)world, pos, direction);
            if (bl && (type == 1 || type == 2)) {
                world.setBlockState(pos, blockState, 2);
                return false;
            }
            if (!bl && type == 0) {
                return false;
            }
        }
        if (type == 0) {
            if (!this.move(world, pos, direction, true)) return false;
            world.setBlockState(pos, blockState, 67);
            world.playSound(null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5f, world.random.nextFloat() * 0.25f + 0.6f);
            world.emitGameEvent((RegistryEntry)GameEvent.BLOCK_ACTIVATE, pos, GameEvent.Emitter.of((BlockState)blockState));
            return true;
        } else {
            if (type != 1 && type != 2) return true;
            BlockEntity blockEntity = world.getBlockEntity(pos.offset(direction));
            if (blockEntity instanceof PistonBlockEntity) {
                ((PistonBlockEntity)blockEntity).finish();
            }
            BlockState blockState2 = (BlockState)((BlockState)Blocks.MOVING_PISTON.getDefaultState().with((Property)PistonExtensionBlock.FACING, (Comparable)direction)).with((Property)PistonExtensionBlock.TYPE, (Comparable)(this.sticky ? PistonType.STICKY : PistonType.DEFAULT));
            world.setBlockState(pos, blockState2, 276);
            world.addBlockEntity(PistonExtensionBlock.createBlockEntityPiston((BlockPos)pos, (BlockState)blockState2, (BlockState)((BlockState)this.getDefaultState().with((Property)FACING, (Comparable)Direction.byIndex((int)(data & 7)))), (Direction)direction, (boolean)false, (boolean)true));
            world.updateNeighbors(pos, blockState2.getBlock());
            blockState2.updateNeighbors((WorldAccess)world, pos, 2);
            if (this.sticky) {
                PistonBlockEntity pistonBlockEntity;
                BlockEntity blockEntity2;
                BlockPos blockPos = pos.add(direction.getOffsetX() * 2, direction.getOffsetY() * 2, direction.getOffsetZ() * 2);
                BlockState blockState3 = world.getBlockState(blockPos);
                boolean bl2 = false;
                if (blockState3.isOf(Blocks.MOVING_PISTON) && (blockEntity2 = world.getBlockEntity(blockPos)) instanceof PistonBlockEntity && (pistonBlockEntity = (PistonBlockEntity)blockEntity2).getFacing() == direction && pistonBlockEntity.isExtending()) {
                    pistonBlockEntity.finish();
                    bl2 = true;
                }
                if (!bl2) {
                    if (type == 1 && !blockState3.isAir() && PistonBlock.isMovable((BlockState)blockState3, (World)world, (BlockPos)blockPos, (Direction)direction.getOpposite(), (boolean)false, (Direction)direction) && (blockState3.getPistonBehavior() == PistonBehavior.NORMAL || blockState3.isOf(Blocks.PISTON) || blockState3.isOf(Blocks.STICKY_PISTON))) {
                        this.move(world, pos, direction, false);
                    } else {
                        world.removeBlock(pos.offset(direction), false);
                    }
                }
            } else {
                world.removeBlock(pos.offset(direction), false);
            }
            world.playSound(null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5f, world.random.nextFloat() * 0.15f + 0.6f);
            world.emitGameEvent((RegistryEntry)GameEvent.BLOCK_DEACTIVATE, pos, GameEvent.Emitter.of((BlockState)blockState2));
        }
        return true;
    }

    public static boolean isMovable(BlockState state, World world, BlockPos pos, Direction direction, boolean canBreak, Direction pistonDir) {
        if (pos.getY() < world.getBottomY() || pos.getY() > world.getTopYInclusive() || !world.getWorldBorder().contains(pos)) {
            return false;
        }
        if (state.isAir()) {
            return true;
        }
        if (state.isOf(Blocks.OBSIDIAN) || state.isOf(Blocks.CRYING_OBSIDIAN) || state.isOf(Blocks.RESPAWN_ANCHOR) || state.isOf(Blocks.REINFORCED_DEEPSLATE)) {
            return false;
        }
        if (direction == Direction.DOWN && pos.getY() == world.getBottomY()) {
            return false;
        }
        if (direction == Direction.UP && pos.getY() == world.getTopYInclusive()) {
            return false;
        }
        if (state.isOf(Blocks.PISTON) || state.isOf(Blocks.STICKY_PISTON)) {
            if (((Boolean)state.get((Property)EXTENDED)).booleanValue()) {
                return false;
            }
        } else {
            if (state.getHardness((BlockView)world, pos) == -1.0f) {
                return false;
            }
            switch (1.field_12192[state.getPistonBehavior().ordinal()]) {
                case 1: {
                    return false;
                }
                case 2: {
                    return canBreak;
                }
                case 3: {
                    return direction == pistonDir;
                }
            }
        }
        return !state.hasBlockEntity();
    }

    /*
     * WARNING - void declaration
     */
    private boolean move(World world, BlockPos pos, Direction dir, boolean extend) {
        void var16_30;
        void var16_28;
        BlockState blockState3;
        BlockPos blockPos3;
        int j;
        PistonHandler pistonHandler;
        BlockPos blockPos = pos.offset(dir);
        if (!extend && world.getBlockState(blockPos).isOf(Blocks.PISTON_HEAD)) {
            world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 276);
        }
        if (!(pistonHandler = new PistonHandler(world, pos, dir, extend)).calculatePush()) {
            return false;
        }
        HashMap map = Maps.newHashMap();
        List list = pistonHandler.getMovedBlocks();
        ArrayList list2 = Lists.newArrayList();
        for (BlockPos blockPos2 : list) {
            BlockState blockState = world.getBlockState(blockPos2);
            list2.add(blockState);
            map.put(blockPos2, blockState);
        }
        List list3 = pistonHandler.getBrokenBlocks();
        BlockState[] blockStates = new BlockState[list.size() + list3.size()];
        Direction direction = extend ? dir : dir.getOpposite();
        int i = 0;
        for (j = list3.size() - 1; j >= 0; --j) {
            blockPos3 = (BlockPos)list3.get(j);
            BlockState blockState = world.getBlockState(blockPos3);
            BlockEntity blockEntity = blockState.hasBlockEntity() ? world.getBlockEntity(blockPos3) : null;
            PistonBlock.dropStacks((BlockState)blockState, (WorldAccess)world, (BlockPos)blockPos3, (BlockEntity)blockEntity);
            if (!blockState.isIn(BlockTags.FIRE) && world.isClient()) {
                world.syncWorldEvent(2001, blockPos3, PistonBlock.getRawIdFromState((BlockState)blockState));
            }
            world.setBlockState(blockPos3, Blocks.AIR.getDefaultState(), 18);
            world.emitGameEvent((RegistryEntry)GameEvent.BLOCK_DESTROY, blockPos3, GameEvent.Emitter.of((BlockState)blockState));
            blockStates[i++] = blockState;
        }
        for (j = list.size() - 1; j >= 0; --j) {
            blockPos3 = (BlockPos)list.get(j);
            BlockState blockState = world.getBlockState(blockPos3);
            blockPos3 = blockPos3.offset(direction);
            map.remove(blockPos3);
            blockState3 = (BlockState)Blocks.MOVING_PISTON.getDefaultState().with((Property)FACING, (Comparable)dir);
            world.setBlockState(blockPos3, blockState3, 324);
            world.addBlockEntity(PistonExtensionBlock.createBlockEntityPiston((BlockPos)blockPos3, (BlockState)blockState3, (BlockState)((BlockState)list2.get(j)), (Direction)dir, (boolean)extend, (boolean)false));
            blockStates[i++] = blockState;
        }
        if (extend) {
            PistonType pistonType = this.sticky ? PistonType.STICKY : PistonType.DEFAULT;
            BlockState blockState4 = (BlockState)((BlockState)Blocks.PISTON_HEAD.getDefaultState().with((Property)PistonHeadBlock.FACING, (Comparable)dir)).with((Property)PistonHeadBlock.TYPE, (Comparable)pistonType);
            BlockState blockState = (BlockState)((BlockState)Blocks.MOVING_PISTON.getDefaultState().with((Property)PistonExtensionBlock.FACING, (Comparable)dir)).with((Property)PistonExtensionBlock.TYPE, (Comparable)(this.sticky ? PistonType.STICKY : PistonType.DEFAULT));
            map.remove(blockPos);
            world.setBlockState(blockPos, blockState, 324);
            world.addBlockEntity(PistonExtensionBlock.createBlockEntityPiston((BlockPos)blockPos, (BlockState)blockState, (BlockState)blockState4, (Direction)dir, (boolean)true, (boolean)true));
        }
        BlockState blockState5 = Blocks.AIR.getDefaultState();
        for (BlockPos blockPos2 : map.keySet()) {
            world.setBlockState(blockPos2, blockState5, 82);
        }
        for (Map.Entry entry : map.entrySet()) {
            BlockPos blockPos5 = (BlockPos)entry.getKey();
            BlockState blockState6 = (BlockState)entry.getValue();
            blockState6.prepare((WorldAccess)world, blockPos5, 2);
            blockState5.updateNeighbors((WorldAccess)world, blockPos5, 2);
            blockState5.prepare((WorldAccess)world, blockPos5, 2);
        }
        WireOrientation wireOrientation = OrientationHelper.getEmissionOrientation((World)world, (Direction)pistonHandler.getMotionDirection(), null);
        i = 0;
        int n = list3.size() - 1;
        while (var16_28 >= 0) {
            blockState3 = blockStates[i++];
            BlockPos blockPos6 = (BlockPos)list3.get((int)var16_28);
            if (world instanceof ServerWorld) {
                ServerWorld serverWorld = (ServerWorld)world;
                blockState3.onStateReplaced(serverWorld, blockPos6, false);
            }
            blockState3.prepare((WorldAccess)world, blockPos6, 2);
            world.updateNeighborsAlways(blockPos6, blockState3.getBlock(), wireOrientation);
            --var16_28;
        }
        int n2 = list.size() - 1;
        while (var16_30 >= 0) {
            world.updateNeighborsAlways((BlockPos)list.get((int)var16_30), blockStates[i++].getBlock(), wireOrientation);
            --var16_30;
        }
        if (extend) {
            world.updateNeighborsAlways(blockPos, Blocks.PISTON_HEAD, wireOrientation);
        }
        return true;
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with((Property)FACING, (Comparable)rotation.rotate((Direction)state.get((Property)FACING)));
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get((Property)FACING)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, EXTENDED});
    }

    protected boolean hasSidedTransparency(BlockState state) {
        return (Boolean)state.get((Property)EXTENDED);
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
}

