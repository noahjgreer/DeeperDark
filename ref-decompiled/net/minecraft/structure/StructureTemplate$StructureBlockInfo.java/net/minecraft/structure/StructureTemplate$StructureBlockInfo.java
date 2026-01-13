/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.structure;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Locale;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import org.jspecify.annotations.Nullable;

public static final class StructureTemplate.StructureBlockInfo
extends Record {
    final BlockPos pos;
    final BlockState state;
    final @Nullable NbtCompound nbt;

    public StructureTemplate.StructureBlockInfo(BlockPos pos, BlockState state, @Nullable NbtCompound nbt) {
        this.pos = pos;
        this.state = state;
        this.nbt = nbt;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "<StructureBlockInfo | %s | %s | %s>", this.pos, this.state, this.nbt);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{StructureTemplate.StructureBlockInfo.class, "pos;state;nbt", "pos", "state", "nbt"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{StructureTemplate.StructureBlockInfo.class, "pos;state;nbt", "pos", "state", "nbt"}, this, object);
    }

    public BlockPos pos() {
        return this.pos;
    }

    public BlockState state() {
        return this.state;
    }

    public @Nullable NbtCompound nbt() {
        return this.nbt;
    }
}
