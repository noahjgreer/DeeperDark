/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.FluidFillable
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.WorldAccess
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jspecify.annotations.Nullable;

public interface FluidFillable {
    public boolean canFillWithFluid(@Nullable LivingEntity var1, BlockView var2, BlockPos var3, BlockState var4, Fluid var5);

    public boolean tryFillWithFluid(WorldAccess var1, BlockPos var2, BlockState var3, FluidState var4);
}

