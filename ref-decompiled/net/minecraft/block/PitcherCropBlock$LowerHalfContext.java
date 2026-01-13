/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

static final class PitcherCropBlock.LowerHalfContext
extends Record {
    final BlockPos pos;
    final BlockState state;

    PitcherCropBlock.LowerHalfContext(BlockPos pos, BlockState state) {
        this.pos = pos;
        this.state = state;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{PitcherCropBlock.LowerHalfContext.class, "pos;state", "pos", "state"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PitcherCropBlock.LowerHalfContext.class, "pos;state", "pos", "state"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PitcherCropBlock.LowerHalfContext.class, "pos;state", "pos", "state"}, this, object);
    }

    public BlockPos pos() {
        return this.pos;
    }

    public BlockState state() {
        return this.state;
    }
}
