/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 */
package net.minecraft.nbt;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtOps;

static class NbtOps.IntArrayMerger
implements NbtOps.Merger {
    private final IntArrayList list = new IntArrayList();

    public NbtOps.IntArrayMerger(int[] values) {
        this.list.addElements(0, values);
    }

    @Override
    public NbtOps.Merger merge(NbtElement nbt) {
        if (nbt instanceof NbtInt) {
            NbtInt nbtInt = (NbtInt)nbt;
            this.list.add(nbtInt.intValue());
            return this;
        }
        return new NbtOps.CompoundListMerger(this.list).merge(nbt);
    }

    @Override
    public NbtElement getResult() {
        return new NbtIntArray(this.list.toIntArray());
    }
}
