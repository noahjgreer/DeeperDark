/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.BarrierBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockRenderType
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Waterloggable
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

public class BarrierBlock
extends Block
implements Waterloggable {
    public static final MapCodec<BarrierBlock> CODEC = BarrierBlock.createCodec(BarrierBlock::new);
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public MapCodec<BarrierBlock> getCodec() {
        return CODEC;
    }

    public BarrierBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)this.getDefaultState().with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    protected boolean isTransparent(BlockState state) {
        return state.getFluidState().isEmpty();
    }

    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    protected float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0f;
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

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{WATERLOGGED});
    }

    public ItemStack tryDrainFluid(@Nullable LivingEntity drainer, WorldAccess world, BlockPos pos, BlockState state) {
        PlayerEntity playerEntity;
        if (!(drainer instanceof PlayerEntity) || !(playerEntity = (PlayerEntity)drainer).isCreative()) {
            return ItemStack.EMPTY;
        }
        return super.tryDrainFluid(drainer, world, pos, state);
    }

    public boolean canFillWithFluid(@Nullable LivingEntity filler, BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        PlayerEntity playerEntity;
        if (!(filler instanceof PlayerEntity) || !(playerEntity = (PlayerEntity)filler).isCreative()) {
            return false;
        }
        return super.canFillWithFluid(filler, world, pos, state, fluid);
    }
}

