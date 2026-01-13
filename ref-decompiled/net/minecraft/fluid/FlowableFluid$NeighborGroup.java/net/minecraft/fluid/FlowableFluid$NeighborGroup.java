/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.fluid;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

record FlowableFluid.NeighborGroup(BlockState self, BlockState other, Direction facing) {
    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FlowableFluid.NeighborGroup)) return false;
        FlowableFluid.NeighborGroup neighborGroup = (FlowableFluid.NeighborGroup)o;
        if (this.self != neighborGroup.self) return false;
        if (this.other != neighborGroup.other) return false;
        if (this.facing != neighborGroup.facing) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int i = System.identityHashCode(this.self);
        i = 31 * i + System.identityHashCode(this.other);
        i = 31 * i + this.facing.hashCode();
        return i;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{FlowableFluid.NeighborGroup.class, "first;second;direction", "self", "other", "facing"}, this);
    }
}
