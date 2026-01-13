/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.fluid;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.state.StateManager;

public static class WaterFluid.Flowing
extends WaterFluid {
    @Override
    protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
        super.appendProperties(builder);
        builder.add(LEVEL);
    }

    @Override
    public int getLevel(FluidState state) {
        return state.get(LEVEL);
    }

    @Override
    public boolean isStill(FluidState state) {
        return false;
    }
}
