/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.util.shape.VoxelShape;

record Block.VoxelShapePair(VoxelShape first, VoxelShape second) {
    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Block.VoxelShapePair)) return false;
        Block.VoxelShapePair voxelShapePair = (Block.VoxelShapePair)o;
        if (this.first != voxelShapePair.first) return false;
        if (this.second != voxelShapePair.second) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this.first) * 31 + System.identityHashCode(this.second);
    }
}
