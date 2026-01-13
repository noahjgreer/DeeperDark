/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public static class StructureTemplate.StructureEntityInfo {
    public final Vec3d pos;
    public final BlockPos blockPos;
    public final NbtCompound nbt;

    public StructureTemplate.StructureEntityInfo(Vec3d pos, BlockPos blockPos, NbtCompound nbt) {
        this.pos = pos;
        this.blockPos = blockPos;
        this.nbt = nbt;
    }
}
