/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.LightningRodBlock
 *  net.minecraft.block.RodBlock
 *  net.minecraft.block.Waterloggable
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.particle.ParticleUtil
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Axis
 *  net.minecraft.util.math.intprovider.UniformIntProvider
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.Heightmap$Type
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.block.OrientationHelper
 *  net.minecraft.world.tick.ScheduledTickView
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RodBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ParticleUtil;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.OrientationHelper;
import net.minecraft.world.tick.ScheduledTickView;

public class LightningRodBlock
extends RodBlock
implements Waterloggable {
    public static final MapCodec<LightningRodBlock> CODEC = LightningRodBlock.createCodec(LightningRodBlock::new);
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final BooleanProperty POWERED = Properties.POWERED;
    private static final int SCHEDULED_TICK_DELAY = 8;
    public static final int MAX_REDIRECT_DISTANCE = 128;
    private static final int field_31191 = 200;

    public MapCodec<? extends LightningRodBlock> getCodec() {
        return CODEC;
    }

    public LightningRodBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)FACING, (Comparable)Direction.UP)).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false))).with((Property)POWERED, (Comparable)Boolean.valueOf(false)));
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean bl = fluidState.getFluid() == Fluids.WATER;
        return (BlockState)((BlockState)this.getDefaultState().with((Property)FACING, (Comparable)ctx.getSide())).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(bl));
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            tickView.scheduleFluidTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected FluidState getFluidState(BlockState state) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return (Boolean)state.get((Property)POWERED) != false ? 15 : 0;
    }

    protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (((Boolean)state.get((Property)POWERED)).booleanValue() && state.get((Property)FACING) == direction) {
            return 15;
        }
        return 0;
    }

    public void setPowered(BlockState state, World world, BlockPos pos) {
        world.setBlockState(pos, (BlockState)state.with((Property)POWERED, (Comparable)Boolean.valueOf(true)), 3);
        this.updateNeighbors(state, world, pos);
        world.scheduleBlockTick(pos, (Block)this, 8);
        world.syncWorldEvent(3002, pos, ((Direction)state.get((Property)FACING)).getAxis().ordinal());
    }

    private void updateNeighbors(BlockState state, World world, BlockPos pos) {
        Direction direction = ((Direction)state.get((Property)FACING)).getOpposite();
        world.updateNeighborsAlways(pos.offset(direction), (Block)this, OrientationHelper.getEmissionOrientation((World)world, (Direction)direction, null));
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.setBlockState(pos, (BlockState)state.with((Property)POWERED, (Comparable)Boolean.valueOf(false)), 3);
        this.updateNeighbors(state, (World)world, pos);
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!world.isThundering() || (long)world.random.nextInt(200) > world.getTime() % 200L || pos.getY() != world.getTopY(Heightmap.Type.WORLD_SURFACE, pos.getX(), pos.getZ()) - 1) {
            return;
        }
        ParticleUtil.spawnParticle((Direction.Axis)((Direction)state.get((Property)FACING)).getAxis(), (World)world, (BlockPos)pos, (double)0.125, (ParticleEffect)ParticleTypes.ELECTRIC_SPARK, (UniformIntProvider)UniformIntProvider.create((int)1, (int)2));
    }

    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        if (((Boolean)state.get((Property)POWERED)).booleanValue()) {
            this.updateNeighbors(state, (World)world, pos);
        }
    }

    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (state.isOf(oldState.getBlock())) {
            return;
        }
        if (((Boolean)state.get((Property)POWERED)).booleanValue() && !world.getBlockTickScheduler().isQueued(pos, (Object)this)) {
            world.scheduleBlockTick(pos, (Block)this, 8);
        }
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, POWERED, WATERLOGGED});
    }

    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }
}

