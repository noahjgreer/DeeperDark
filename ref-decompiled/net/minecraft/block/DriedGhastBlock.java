/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.DriedGhastBlock
 *  net.minecraft.block.HorizontalFacingBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.Waterloggable
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.SpawnReason
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.entity.passive.HappyGhastEntity
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.event.GameEvent$Emitter
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

public class DriedGhastBlock
extends HorizontalFacingBlock
implements Waterloggable {
    public static final MapCodec<DriedGhastBlock> CODEC = DriedGhastBlock.createCodec(DriedGhastBlock::new);
    public static final int MAX_HYDRATION = 3;
    public static final IntProperty HYDRATION = Properties.HYDRATION;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final int HYDRATION_TICK_TIME = 5000;
    private static final VoxelShape SHAPE = Block.createColumnShape((double)10.0, (double)10.0, (double)0.0, (double)10.0);

    public MapCodec<DriedGhastBlock> getCodec() {
        return CODEC;
    }

    public DriedGhastBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)FACING, (Comparable)Direction.NORTH)).with((Property)HYDRATION, (Comparable)Integer.valueOf(0))).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, HYDRATION, WATERLOGGED});
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            tickView.scheduleFluidTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    public int getHydration(BlockState state) {
        return (Integer)state.get((Property)HYDRATION);
    }

    private boolean isFullyHydrated(BlockState state) {
        return this.getHydration(state) == 3;
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            this.tickHydration(state, world, pos, random);
            return;
        }
        int i = this.getHydration(state);
        if (i > 0) {
            world.setBlockState(pos, (BlockState)state.with((Property)HYDRATION, (Comparable)Integer.valueOf(i - 1)), 2);
            world.emitGameEvent((RegistryEntry)GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of((BlockState)state));
        }
    }

    private void tickHydration(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!this.isFullyHydrated(state)) {
            world.playSound(null, pos, SoundEvents.BLOCK_DRIED_GHAST_TRANSITION, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.setBlockState(pos, (BlockState)state.with((Property)HYDRATION, (Comparable)Integer.valueOf(this.getHydration(state) + 1)), 2);
            world.emitGameEvent((RegistryEntry)GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of((BlockState)state));
        } else {
            this.spawnGhastling(world, pos, state);
        }
    }

    private void spawnGhastling(ServerWorld world, BlockPos pos, BlockState state) {
        world.removeBlock(pos, false);
        HappyGhastEntity happyGhastEntity = (HappyGhastEntity)EntityType.HAPPY_GHAST.create((World)world, SpawnReason.BREEDING);
        if (happyGhastEntity != null) {
            Vec3d vec3d = pos.toBottomCenterPos();
            happyGhastEntity.setBaby(true);
            float f = Direction.getHorizontalDegreesOrThrow((Direction)((Direction)state.get((Property)FACING)));
            happyGhastEntity.setHeadYaw(f);
            happyGhastEntity.refreshPositionAndAngles(vec3d.getX(), vec3d.getY(), vec3d.getZ(), f, 0.0f);
            world.spawnEntity((Entity)happyGhastEntity);
            world.playSoundFromEntity(null, (Entity)happyGhastEntity, SoundEvents.ENTITY_GHASTLING_SPAWN, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        double d = (double)pos.getX() + 0.5;
        double e = (double)pos.getY() + 0.5;
        double f = (double)pos.getZ() + 0.5;
        if (!((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            if (random.nextInt(40) == 0 && world.getBlockState(pos.down()).isIn(BlockTags.TRIGGERS_AMBIENT_DRIED_GHAST_BLOCK_SOUNDS)) {
                world.playSoundClient(d, e, f, SoundEvents.BLOCK_DRIED_GHAST_AMBIENT, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
            }
            if (random.nextInt(6) == 0) {
                world.addParticleClient((ParticleEffect)ParticleTypes.WHITE_SMOKE, d, e, f, 0.0, 0.02, 0.0);
            }
        } else {
            if (random.nextInt(40) == 0) {
                world.playSoundClient(d, e, f, SoundEvents.BLOCK_DRIED_GHAST_AMBIENT_WATER, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
            }
            if (random.nextInt(6) == 0) {
                world.addParticleClient((ParticleEffect)ParticleTypes.HAPPY_VILLAGER, d + (double)((random.nextFloat() * 2.0f - 1.0f) / 3.0f), e + 0.4, f + (double)((random.nextFloat() * 2.0f - 1.0f) / 3.0f), 0.0, (double)random.nextFloat(), 0.0);
            }
        }
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if ((((Boolean)state.get((Property)WATERLOGGED)).booleanValue() || (Integer)state.get((Property)HYDRATION) > 0) && !world.getBlockTickScheduler().isQueued(pos, (Object)this)) {
            world.scheduleBlockTick(pos, (Block)this, 5000);
        }
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean bl = fluidState.getFluid() == Fluids.WATER;
        return (BlockState)((BlockState)super.getPlacementState(ctx).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(bl))).with((Property)FACING, (Comparable)ctx.getHorizontalPlayerFacing().getOpposite());
    }

    protected FluidState getFluidState(BlockState state) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (((Boolean)state.get((Property)Properties.WATERLOGGED)).booleanValue() || fluidState.getFluid() != Fluids.WATER) {
            return false;
        }
        if (!world.isClient()) {
            world.setBlockState(pos, (BlockState)state.with((Property)Properties.WATERLOGGED, (Comparable)Boolean.valueOf(true)), 3);
            world.scheduleFluidTick(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate((WorldView)world));
            world.playSound(null, pos, SoundEvents.BLOCK_DRIED_GHAST_PLACE_IN_WATER, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        return true;
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        world.playSound(null, pos, (Boolean)state.get((Property)WATERLOGGED) != false ? SoundEvents.BLOCK_DRIED_GHAST_PLACE_IN_WATER : SoundEvents.BLOCK_DRIED_GHAST_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    public boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
}

