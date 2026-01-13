/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;

static final class PointedDripstoneBlock.DrippingFluid
extends Record {
    final BlockPos pos;
    final Fluid fluid;
    final BlockState sourceState;

    PointedDripstoneBlock.DrippingFluid(BlockPos pos, Fluid fluid, BlockState sourceState) {
        this.pos = pos;
        this.fluid = fluid;
        this.sourceState = sourceState;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{PointedDripstoneBlock.DrippingFluid.class, "pos;fluid;sourceState", "pos", "fluid", "sourceState"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PointedDripstoneBlock.DrippingFluid.class, "pos;fluid;sourceState", "pos", "fluid", "sourceState"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PointedDripstoneBlock.DrippingFluid.class, "pos;fluid;sourceState", "pos", "fluid", "sourceState"}, this, object);
    }

    public BlockPos pos() {
        return this.pos;
    }

    public Fluid fluid() {
        return this.fluid;
    }

    public BlockState sourceState() {
        return this.sourceState;
    }
}
