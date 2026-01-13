/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.fluid;

import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.WaterFluid;

public static class WaterFluid.Still
extends WaterFluid {
    @Override
    public int getLevel(FluidState state) {
        return 8;
    }

    @Override
    public boolean isStill(FluidState state) {
        return true;
    }
}
