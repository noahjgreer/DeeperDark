/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.FluidDrainable
 *  net.minecraft.block.FluidFillable
 *  net.minecraft.block.Waterloggable
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.WorldView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jspecify.annotations.Nullable;

public interface Waterloggable
extends FluidDrainable,
FluidFillable {
    default public boolean canFillWithFluid(@Nullable LivingEntity filler, BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        return fluid == Fluids.WATER;
    }

    default public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!((Boolean)state.get((Property)Properties.WATERLOGGED)).booleanValue() && fluidState.getFluid() == Fluids.WATER) {
            if (!world.isClient()) {
                world.setBlockState(pos, (BlockState)state.with((Property)Properties.WATERLOGGED, (Comparable)Boolean.valueOf(true)), 3);
                world.scheduleFluidTick(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate((WorldView)world));
            }
            return true;
        }
        return false;
    }

    default public ItemStack tryDrainFluid(@Nullable LivingEntity drainer, WorldAccess world, BlockPos pos, BlockState state) {
        if (((Boolean)state.get((Property)Properties.WATERLOGGED)).booleanValue()) {
            world.setBlockState(pos, (BlockState)state.with((Property)Properties.WATERLOGGED, (Comparable)Boolean.valueOf(false)), 3);
            if (!state.canPlaceAt((WorldView)world, pos)) {
                world.breakBlock(pos, true);
            }
            return new ItemStack((ItemConvertible)Items.WATER_BUCKET);
        }
        return ItemStack.EMPTY;
    }

    default public Optional<SoundEvent> getBucketFillSound() {
        return Fluids.WATER.getBucketFillSound();
    }
}

