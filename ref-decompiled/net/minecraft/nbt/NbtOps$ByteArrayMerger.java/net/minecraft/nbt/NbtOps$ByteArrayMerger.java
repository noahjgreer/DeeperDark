/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.bytes.ByteArrayList
 */
package net.minecraft.nbt;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;

static class NbtOps.ByteArrayMerger
implements NbtOps.Merger {
    private final ByteArrayList list = new ByteArrayList();

    public NbtOps.ByteArrayMerger(byte[] values) {
        this.list.addElements(0, values);
    }

    @Override
    public NbtOps.Merger merge(NbtElement nbt) {
        if (nbt instanceof NbtByte) {
            NbtByte nbtByte = (NbtByte)nbt;
            this.list.add(nbtByte.byteValue());
            return this;
        }
        return new NbtOps.CompoundListMerger(this.list).merge(nbt);
    }

    @Override
    public NbtElement getResult() {
        return new NbtByteArray(this.list.toByteArray());
    }
}
