/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.bytes.ByteArrayList
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 */
package net.minecraft.nbt;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtOps;

static class NbtOps.CompoundListMerger
implements NbtOps.Merger {
    private final NbtList list = new NbtList();

    NbtOps.CompoundListMerger() {
    }

    NbtOps.CompoundListMerger(NbtList nbtList) {
        this.list.addAll(nbtList);
    }

    public NbtOps.CompoundListMerger(IntArrayList list) {
        list.forEach(value -> this.list.add(NbtInt.of(value)));
    }

    public NbtOps.CompoundListMerger(ByteArrayList list) {
        list.forEach(value -> this.list.add(NbtByte.of(value)));
    }

    public NbtOps.CompoundListMerger(LongArrayList list) {
        list.forEach(value -> this.list.add(NbtLong.of(value)));
    }

    @Override
    public NbtOps.Merger merge(NbtElement nbt) {
        this.list.add(nbt);
        return this;
    }

    @Override
    public NbtElement getResult() {
        return this.list;
    }
}
