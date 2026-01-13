/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 */
package net.minecraft.nbt;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtLongArray;
import net.minecraft.nbt.NbtOps;

static class NbtOps.LongArrayMerger
implements NbtOps.Merger {
    private final LongArrayList list = new LongArrayList();

    public NbtOps.LongArrayMerger(long[] values) {
        this.list.addElements(0, values);
    }

    @Override
    public NbtOps.Merger merge(NbtElement nbt) {
        if (nbt instanceof NbtLong) {
            NbtLong nbtLong = (NbtLong)nbt;
            this.list.add(nbtLong.longValue());
            return this;
        }
        return new NbtOps.CompoundListMerger(this.list).merge(nbt);
    }

    @Override
    public NbtElement getResult() {
        return new NbtLongArray(this.list.toLongArray());
    }
}
