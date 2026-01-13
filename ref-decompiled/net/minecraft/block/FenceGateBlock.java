/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.FenceGateBlock
 *  net.minecraft.block.FenceGateBlock$1
 *  net.minecraft.block.HorizontalFacingBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.WoodType
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.Util
 *  net.minecraft.util.function.BooleanBiFunction
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Axis
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.block.WireOrientation
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.event.GameEvent$Emitter
 *  net.minecraft.world.explosion.Explosion
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WoodType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Util;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class FenceGateBlock
extends HorizontalFacingBlock {
    public static final MapCodec<FenceGateBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)WoodType.CODEC.fieldOf("wood_type").forGetter(block -> block.type), (App)FenceGateBlock.createSettingsCodec()).apply((Applicative)instance, FenceGateBlock::new));
    public static final BooleanProperty OPEN = Properties.OPEN;
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty IN_WALL = Properties.IN_WALL;
    private static final Map<Direction.Axis, VoxelShape> REGULAR_OUTLINE_SHAPES = VoxelShapes.createHorizontalAxisShapeMap((VoxelShape)Block.createCuboidShape((double)16.0, (double)16.0, (double)4.0));
    private static final Map<Direction.Axis, VoxelShape> IN_WALL_OUTLINE_SHAPES = Maps.newEnumMap((Map)Util.transformMapValues((Map)REGULAR_OUTLINE_SHAPES, shape -> VoxelShapes.combineAndSimplify((VoxelShape)shape, (VoxelShape)Block.createColumnShape((double)16.0, (double)13.0, (double)16.0), (BooleanBiFunction)BooleanBiFunction.ONLY_FIRST)));
    private static final Map<Direction.Axis, VoxelShape> CLOSED_COLLISION_SHAPES = VoxelShapes.createHorizontalAxisShapeMap((VoxelShape)Block.createColumnShape((double)16.0, (double)4.0, (double)0.0, (double)24.0));
    private static final Map<Direction.Axis, VoxelShape> CLOSED_SIDES_SHAPES = VoxelShapes.createHorizontalAxisShapeMap((VoxelShape)Block.createColumnShape((double)16.0, (double)4.0, (double)5.0, (double)24.0));
    private static final Map<Direction.Axis, VoxelShape> REGULAR_CULLING_SHAPES = VoxelShapes.createHorizontalAxisShapeMap((VoxelShape)VoxelShapes.union((VoxelShape)Block.createCuboidShape((double)0.0, (double)5.0, (double)7.0, (double)2.0, (double)16.0, (double)9.0), (VoxelShape)Block.createCuboidShape((double)14.0, (double)5.0, (double)7.0, (double)16.0, (double)16.0, (double)9.0)));
    private static final Map<Direction.Axis, VoxelShape> IN_WALL_CULLING_SHAPES = Maps.newEnumMap((Map)Util.transformMapValues((Map)REGULAR_CULLING_SHAPES, shape -> shape.offset(0.0, -0.1875, 0.0).simplify()));
    private final WoodType type;

    public MapCodec<FenceGateBlock> getCodec() {
        return CODEC;
    }

    public FenceGateBlock(WoodType type, AbstractBlock.Settings settings) {
        super(settings.sounds(type.soundType()));
        this.type = type;
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)OPEN, (Comparable)Boolean.valueOf(false))).with((Property)POWERED, (Comparable)Boolean.valueOf(false))).with((Property)IN_WALL, (Comparable)Boolean.valueOf(false)));
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction.Axis axis = ((Direction)state.get((Property)FACING)).getAxis();
        return (VoxelShape)((Boolean)state.get((Property)IN_WALL) != false ? IN_WALL_OUTLINE_SHAPES : REGULAR_OUTLINE_SHAPES).get(axis);
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        Direction.Axis axis = direction.getAxis();
        if (((Direction)state.get((Property)FACING)).rotateYClockwise().getAxis() == axis) {
            boolean bl = this.isWall(neighborState) || this.isWall(world.getBlockState(pos.offset(direction.getOpposite())));
            return (BlockState)state.with((Property)IN_WALL, (Comparable)Boolean.valueOf(bl));
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
        Direction.Axis axis = ((Direction)state.get((Property)FACING)).getAxis();
        return (Boolean)state.get((Property)OPEN) != false ? VoxelShapes.empty() : (VoxelShape)CLOSED_SIDES_SHAPES.get(axis);
    }

    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction.Axis axis = ((Direction)state.get((Property)FACING)).getAxis();
        return (Boolean)state.get((Property)OPEN) != false ? VoxelShapes.empty() : (VoxelShape)CLOSED_COLLISION_SHAPES.get(axis);
    }

    protected VoxelShape getCullingShape(BlockState state) {
        Direction.Axis axis = ((Direction)state.get((Property)FACING)).getAxis();
        return (VoxelShape)((Boolean)state.get((Property)IN_WALL) != false ? IN_WALL_CULLING_SHAPES : REGULAR_CULLING_SHAPES).get(axis);
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        switch (1.field_11029[type.ordinal()]) {
            case 1: {
                return (Boolean)state.get((Property)OPEN);
            }
            case 2: {
                return false;
            }
            case 3: {
                return (Boolean)state.get((Property)OPEN);
            }
        }
        return false;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        boolean bl = world.isReceivingRedstonePower(blockPos);
        Direction direction = ctx.getHorizontalPlayerFacing();
        Direction.Axis axis = direction.getAxis();
        boolean bl2 = axis == Direction.Axis.Z && (this.isWall(world.getBlockState(blockPos.west())) || this.isWall(world.getBlockState(blockPos.east()))) || axis == Direction.Axis.X && (this.isWall(world.getBlockState(blockPos.north())) || this.isWall(world.getBlockState(blockPos.south())));
        return (BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with((Property)FACING, (Comparable)direction)).with((Property)OPEN, (Comparable)Boolean.valueOf(bl))).with((Property)POWERED, (Comparable)Boolean.valueOf(bl))).with((Property)IN_WALL, (Comparable)Boolean.valueOf(bl2));
    }

    private boolean isWall(BlockState state) {
        return state.isIn(BlockTags.WALLS);
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (((Boolean)state.get((Property)OPEN)).booleanValue()) {
            state = (BlockState)state.with((Property)OPEN, (Comparable)Boolean.valueOf(false));
            world.setBlockState(pos, state, 10);
        } else {
            Direction direction = player.getHorizontalFacing();
            if (state.get((Property)FACING) == direction.getOpposite()) {
                state = (BlockState)state.with((Property)FACING, (Comparable)direction);
            }
            state = (BlockState)state.with((Property)OPEN, (Comparable)Boolean.valueOf(true));
            world.setBlockState(pos, state, 10);
        }
        boolean bl = (Boolean)state.get((Property)OPEN);
        world.playSound((Entity)player, pos, bl ? this.type.fenceGateOpen() : this.type.fenceGateClose(), SoundCategory.BLOCKS, 1.0f, world.getRandom().nextFloat() * 0.1f + 0.9f);
        world.emitGameEvent((Entity)player, (RegistryEntry)(bl ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE), pos);
        return ActionResult.SUCCESS;
    }

    protected void onExploded(BlockState state, ServerWorld world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
        if (explosion.canTriggerBlocks() && !((Boolean)state.get((Property)POWERED)).booleanValue()) {
            boolean bl = (Boolean)state.get((Property)OPEN);
            world.setBlockState(pos, (BlockState)state.with((Property)OPEN, (Comparable)Boolean.valueOf(!bl)));
            world.playSound(null, pos, bl ? this.type.fenceGateClose() : this.type.fenceGateOpen(), SoundCategory.BLOCKS, 1.0f, world.getRandom().nextFloat() * 0.1f + 0.9f);
            world.emitGameEvent((RegistryEntry)(bl ? GameEvent.BLOCK_CLOSE : GameEvent.BLOCK_OPEN), pos, GameEvent.Emitter.of((BlockState)state));
        }
        super.onExploded(state, world, pos, explosion, stackMerger);
    }

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (world.isClient()) {
            return;
        }
        boolean bl = world.isReceivingRedstonePower(pos);
        if ((Boolean)state.get((Property)POWERED) != bl) {
            world.setBlockState(pos, (BlockState)((BlockState)state.with((Property)POWERED, (Comparable)Boolean.valueOf(bl))).with((Property)OPEN, (Comparable)Boolean.valueOf(bl)), 2);
            if ((Boolean)state.get((Property)OPEN) != bl) {
                world.playSound(null, pos, bl ? this.type.fenceGateOpen() : this.type.fenceGateClose(), SoundCategory.BLOCKS, 1.0f, world.getRandom().nextFloat() * 0.1f + 0.9f);
                world.emitGameEvent(null, (RegistryEntry)(bl ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE), pos);
            }
        }
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, OPEN, POWERED, IN_WALL});
    }

    public static boolean canWallConnect(BlockState state, Direction side) {
        return ((Direction)state.get((Property)FACING)).getAxis() == side.rotateYClockwise().getAxis();
    }
}

