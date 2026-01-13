/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.BlockWithEntity
 *  net.minecraft.block.NoteBlock
 *  net.minecraft.block.SculkSensorBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.Waterloggable
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityTicker
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.SculkSensorBlockEntity
 *  net.minecraft.block.enums.SculkSensorPhase
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.particle.DustColorTransitionParticleEffect
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.intprovider.ConstantIntProvider
 *  net.minecraft.util.math.intprovider.IntProvider
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.event.GameEvent$Emitter
 *  net.minecraft.world.event.Vibrations
 *  net.minecraft.world.event.Vibrations$Callback
 *  net.minecraft.world.event.Vibrations$ListenerData
 *  net.minecraft.world.event.Vibrations$Ticker
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SculkSensorBlockEntity;
import net.minecraft.block.enums.SculkSensorPhase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.Vibrations;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class SculkSensorBlock
extends BlockWithEntity
implements Waterloggable {
    public static final MapCodec<SculkSensorBlock> CODEC = SculkSensorBlock.createCodec(SculkSensorBlock::new);
    public static final int field_31239 = 30;
    public static final int field_44607 = 10;
    public static final EnumProperty<SculkSensorPhase> SCULK_SENSOR_PHASE = Properties.SCULK_SENSOR_PHASE;
    public static final IntProperty POWER = Properties.POWER;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private static final VoxelShape OUTLINE_SHAPE = Block.createColumnShape((double)16.0, (double)0.0, (double)8.0);
    private static final float[] RESONATION_NOTE_PITCHES = (float[])Util.make((Object)new float[16], frequency -> {
        int[] is = new int[]{0, 0, 2, 4, 6, 7, 9, 10, 12, 14, 15, 18, 19, 21, 22, 24};
        for (int i = 0; i < 16; ++i) {
            frequency[i] = NoteBlock.getNotePitch((int)is[i]);
        }
    });

    public MapCodec<? extends SculkSensorBlock> getCodec() {
        return CODEC;
    }

    public SculkSensorBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)SCULK_SENSOR_PHASE, (Comparable)SculkSensorPhase.INACTIVE)).with((Property)POWER, (Comparable)Integer.valueOf(0))).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
        return (BlockState)this.getDefaultState().with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
    }

    protected FluidState getFluidState(BlockState state) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (SculkSensorBlock.getPhase((BlockState)state) != SculkSensorPhase.ACTIVE) {
            if (SculkSensorBlock.getPhase((BlockState)state) == SculkSensorPhase.COOLDOWN) {
                world.setBlockState(pos, (BlockState)state.with((Property)SCULK_SENSOR_PHASE, (Comparable)SculkSensorPhase.INACTIVE), 3);
                if (!((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
                    world.playSound(null, pos, SoundEvents.BLOCK_SCULK_SENSOR_CLICKING_STOP, SoundCategory.BLOCKS, 1.0f, world.random.nextFloat() * 0.2f + 0.8f);
                }
            }
            return;
        }
        SculkSensorBlock.setCooldown((World)world, (BlockPos)pos, (BlockState)state);
    }

    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        BlockEntity blockEntity;
        if (!world.isClient() && SculkSensorBlock.isInactive((BlockState)state) && entity.getType() != EntityType.WARDEN && (blockEntity = world.getBlockEntity(pos)) instanceof SculkSensorBlockEntity) {
            SculkSensorBlockEntity sculkSensorBlockEntity = (SculkSensorBlockEntity)blockEntity;
            if (world instanceof ServerWorld) {
                ServerWorld serverWorld = (ServerWorld)world;
                if (sculkSensorBlockEntity.getVibrationCallback().accepts(serverWorld, pos, (RegistryEntry)GameEvent.STEP, GameEvent.Emitter.of((BlockState)state))) {
                    sculkSensorBlockEntity.getEventListener().forceListen(serverWorld, (RegistryEntry)GameEvent.STEP, GameEvent.Emitter.of((Entity)entity), entity.getEntityPos());
                }
            }
        }
        super.onSteppedOn(world, pos, state, entity);
    }

    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (world.isClient() || state.isOf(oldState.getBlock())) {
            return;
        }
        if ((Integer)state.get((Property)POWER) > 0 && !world.getBlockTickScheduler().isQueued(pos, (Object)this)) {
            world.setBlockState(pos, (BlockState)state.with((Property)POWER, (Comparable)Integer.valueOf(0)), 18);
        }
    }

    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        if (SculkSensorBlock.getPhase((BlockState)state) == SculkSensorPhase.ACTIVE) {
            SculkSensorBlock.updateNeighbors((World)world, (BlockPos)pos, (BlockState)state);
        }
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            tickView.scheduleFluidTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    private static void updateNeighbors(World world, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        world.updateNeighbors(pos, block);
        world.updateNeighbors(pos.down(), block);
    }

    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SculkSensorBlockEntity(pos, state);
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (!world.isClient()) {
            return SculkSensorBlock.validateTicker(type, (BlockEntityType)BlockEntityType.SCULK_SENSOR, (worldx, pos, statex, blockEntity) -> Vibrations.Ticker.tick((World)worldx, (Vibrations.ListenerData)blockEntity.getVibrationListenerData(), (Vibrations.Callback)blockEntity.getVibrationCallback()));
        }
        return null;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }

    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return (Integer)state.get((Property)POWER);
    }

    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (direction == Direction.UP) {
            return state.getWeakRedstonePower(world, pos, direction);
        }
        return 0;
    }

    public static SculkSensorPhase getPhase(BlockState state) {
        return (SculkSensorPhase)state.get((Property)SCULK_SENSOR_PHASE);
    }

    public static boolean isInactive(BlockState state) {
        return SculkSensorBlock.getPhase((BlockState)state) == SculkSensorPhase.INACTIVE;
    }

    public static void setCooldown(World world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, (BlockState)((BlockState)state.with((Property)SCULK_SENSOR_PHASE, (Comparable)SculkSensorPhase.COOLDOWN)).with((Property)POWER, (Comparable)Integer.valueOf(0)), 3);
        world.scheduleBlockTick(pos, state.getBlock(), 10);
        SculkSensorBlock.updateNeighbors((World)world, (BlockPos)pos, (BlockState)state);
    }

    @VisibleForTesting
    public int getCooldownTime() {
        return 30;
    }

    public void setActive(@Nullable Entity sourceEntity, World world, BlockPos pos, BlockState state, int power, int frequency) {
        world.setBlockState(pos, (BlockState)((BlockState)state.with((Property)SCULK_SENSOR_PHASE, (Comparable)SculkSensorPhase.ACTIVE)).with((Property)POWER, (Comparable)Integer.valueOf(power)), 3);
        world.scheduleBlockTick(pos, state.getBlock(), this.getCooldownTime());
        SculkSensorBlock.updateNeighbors((World)world, (BlockPos)pos, (BlockState)state);
        SculkSensorBlock.tryResonate((Entity)sourceEntity, (World)world, (BlockPos)pos, (int)frequency);
        world.emitGameEvent(sourceEntity, (RegistryEntry)GameEvent.SCULK_SENSOR_TENDRILS_CLICKING, pos);
        if (!((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.BLOCK_SCULK_SENSOR_CLICKING, SoundCategory.BLOCKS, 1.0f, world.random.nextFloat() * 0.2f + 0.8f);
        }
    }

    public static void tryResonate(@Nullable Entity sourceEntity, World world, BlockPos pos, int frequency) {
        for (Direction direction : Direction.values()) {
            BlockPos blockPos = pos.offset(direction);
            BlockState blockState = world.getBlockState(blockPos);
            if (!blockState.isIn(BlockTags.VIBRATION_RESONATORS)) continue;
            world.emitGameEvent(Vibrations.getResonation((int)frequency), blockPos, GameEvent.Emitter.of((Entity)sourceEntity, (BlockState)blockState));
            float f = RESONATION_NOTE_PITCHES[frequency];
            world.playSound(null, blockPos, SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.BLOCKS, 1.0f, f);
        }
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (SculkSensorBlock.getPhase((BlockState)state) != SculkSensorPhase.ACTIVE) {
            return;
        }
        Direction direction = Direction.random((Random)random);
        if (direction == Direction.UP || direction == Direction.DOWN) {
            return;
        }
        double d = (double)pos.getX() + 0.5 + (direction.getOffsetX() == 0 ? 0.5 - random.nextDouble() : (double)direction.getOffsetX() * 0.6);
        double e = (double)pos.getY() + 0.25;
        double f = (double)pos.getZ() + 0.5 + (direction.getOffsetZ() == 0 ? 0.5 - random.nextDouble() : (double)direction.getOffsetZ() * 0.6);
        double g = (double)random.nextFloat() * 0.04;
        world.addParticleClient((ParticleEffect)DustColorTransitionParticleEffect.DEFAULT, d, e, f, 0.0, g, 0.0);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{SCULK_SENSOR_PHASE, POWER, WATERLOGGED});
    }

    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    protected int getComparatorOutput(BlockState state, World world, BlockPos pos, Direction direction) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SculkSensorBlockEntity) {
            SculkSensorBlockEntity sculkSensorBlockEntity = (SculkSensorBlockEntity)blockEntity;
            return SculkSensorBlock.getPhase((BlockState)state) == SculkSensorPhase.ACTIVE ? sculkSensorBlockEntity.getLastVibrationFrequency() : 0;
        }
        return 0;
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    protected boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    protected void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience) {
        super.onStacksDropped(state, world, pos, tool, dropExperience);
        if (dropExperience) {
            this.dropExperienceWhenMined(world, pos, tool, (IntProvider)ConstantIntProvider.create((int)5));
        }
    }
}

