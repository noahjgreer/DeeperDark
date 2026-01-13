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
 *  net.minecraft.block.BlockSetType
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.HorizontalFacingBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.TrapdoorBlock
 *  net.minecraft.block.TrapdoorBlock$1
 *  net.minecraft.block.Waterloggable
 *  net.minecraft.block.enums.BlockHalf
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.block.WireOrientation
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.explosion.Explosion
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
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
public class TrapdoorBlock
extends HorizontalFacingBlock
implements Waterloggable {
    public static final MapCodec<TrapdoorBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)BlockSetType.CODEC.fieldOf("block_set_type").forGetter(block -> block.blockSetType), (App)TrapdoorBlock.createSettingsCodec()).apply((Applicative)instance, TrapdoorBlock::new));
    public static final BooleanProperty OPEN = Properties.OPEN;
    public static final EnumProperty<BlockHalf> HALF = Properties.BLOCK_HALF;
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private static final Map<Direction, VoxelShape> shapeByDirection = VoxelShapes.createFacingShapeMap((VoxelShape)Block.createCuboidZShape((double)16.0, (double)13.0, (double)16.0));
    private final BlockSetType blockSetType;

    public MapCodec<? extends TrapdoorBlock> getCodec() {
        return CODEC;
    }

    public TrapdoorBlock(BlockSetType type, AbstractBlock.Settings settings) {
        super(settings.sounds(type.soundType()));
        this.blockSetType = type;
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)FACING, (Comparable)Direction.NORTH)).with((Property)OPEN, (Comparable)Boolean.valueOf(false))).with((Property)HALF, (Comparable)BlockHalf.BOTTOM)).with((Property)POWERED, (Comparable)Boolean.valueOf(false))).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (VoxelShape)shapeByDirection.get((Boolean)state.get((Property)OPEN) != false ? state.get((Property)FACING) : (state.get((Property)HALF) == BlockHalf.TOP ? Direction.DOWN : Direction.UP));
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        switch (1.field_11634[type.ordinal()]) {
            case 1: {
                return (Boolean)state.get((Property)OPEN);
            }
            case 2: {
                return (Boolean)state.get((Property)WATERLOGGED);
            }
            case 3: {
                return (Boolean)state.get((Property)OPEN);
            }
        }
        return false;
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!this.blockSetType.canOpenByHand()) {
            return ActionResult.PASS;
        }
        this.flip(state, world, pos, player);
        return ActionResult.SUCCESS;
    }

    protected void onExploded(BlockState state, ServerWorld world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
        if (explosion.canTriggerBlocks() && this.blockSetType.canOpenByWindCharge() && !((Boolean)state.get((Property)POWERED)).booleanValue()) {
            this.flip(state, (World)world, pos, null);
        }
        super.onExploded(state, world, pos, explosion, stackMerger);
    }

    private void flip(BlockState state, World world, BlockPos pos, @Nullable PlayerEntity player) {
        BlockState blockState = (BlockState)state.cycle((Property)OPEN);
        world.setBlockState(pos, blockState, 2);
        if (((Boolean)blockState.get((Property)WATERLOGGED)).booleanValue()) {
            world.scheduleFluidTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickRate((WorldView)world));
        }
        this.playToggleSound(player, world, pos, ((Boolean)blockState.get((Property)OPEN)).booleanValue());
    }

    protected void playToggleSound(@Nullable PlayerEntity player, World world, BlockPos pos, boolean open) {
        world.playSound((Entity)player, pos, open ? this.blockSetType.trapdoorOpen() : this.blockSetType.trapdoorClose(), SoundCategory.BLOCKS, 1.0f, world.getRandom().nextFloat() * 0.1f + 0.9f);
        world.emitGameEvent((Entity)player, (RegistryEntry)(open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE), pos);
    }

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (world.isClient()) {
            return;
        }
        boolean bl = world.isReceivingRedstonePower(pos);
        if (bl != (Boolean)state.get((Property)POWERED)) {
            if ((Boolean)state.get((Property)OPEN) != bl) {
                state = (BlockState)state.with((Property)OPEN, (Comparable)Boolean.valueOf(bl));
                this.playToggleSound(null, world, pos, bl);
            }
            world.setBlockState(pos, (BlockState)state.with((Property)POWERED, (Comparable)Boolean.valueOf(bl)), 2);
            if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
                world.scheduleFluidTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickRate((WorldView)world));
            }
        }
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = this.getDefaultState();
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        Direction direction = ctx.getSide();
        blockState = ctx.canReplaceExisting() || !direction.getAxis().isHorizontal() ? (BlockState)((BlockState)blockState.with((Property)FACING, (Comparable)ctx.getHorizontalPlayerFacing().getOpposite())).with((Property)HALF, (Comparable)(direction == Direction.UP ? BlockHalf.BOTTOM : BlockHalf.TOP)) : (BlockState)((BlockState)blockState.with((Property)FACING, (Comparable)direction)).with((Property)HALF, (Comparable)(ctx.getHitPos().y - (double)ctx.getBlockPos().getY() > 0.5 ? BlockHalf.TOP : BlockHalf.BOTTOM));
        if (ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos())) {
            blockState = (BlockState)((BlockState)blockState.with((Property)OPEN, (Comparable)Boolean.valueOf(true))).with((Property)POWERED, (Comparable)Boolean.valueOf(true));
        }
        return (BlockState)blockState.with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, OPEN, HALF, POWERED, WATERLOGGED});
    }

    protected FluidState getFluidState(BlockState state) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            tickView.scheduleFluidTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected BlockSetType getBlockSetType() {
        return this.blockSetType;
    }
}

