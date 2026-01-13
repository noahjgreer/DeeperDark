/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.storage;

import com.mojang.serialization.DynamicOps;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ErrorReporter;

static class NbtWriteView.NbtListView
implements WriteView.ListView {
    private final String key;
    private final ErrorReporter reporter;
    private final DynamicOps<NbtElement> ops;
    private final NbtList list;

    NbtWriteView.NbtListView(String key, ErrorReporter reporter, DynamicOps<NbtElement> ops, NbtList list) {
        this.key = key;
        this.reporter = reporter;
        this.ops = ops;
        this.list = list;
    }

    @Override
    public WriteView add() {
        int i = this.list.size();
        NbtCompound nbtCompound = new NbtCompound();
        this.list.add(nbtCompound);
        return new NbtWriteView(this.reporter.makeChild(new ErrorReporter.NamedListElementContext(this.key, i)), this.ops, nbtCompound);
    }

    @Override
    public void removeLast() {
        this.list.removeLast();
    }

    @Override
    public boolean isEmpty() {
        return this.list.isEmpty();
    }
}
