/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command.argument;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

static class NbtPathArgumentType.IndexedListElementNode
implements NbtPathArgumentType.PathNode {
    private final int index;

    public NbtPathArgumentType.IndexedListElementNode(int index) {
        this.index = index;
    }

    @Override
    public void get(NbtElement current, List<NbtElement> results) {
        if (current instanceof AbstractNbtList) {
            int j;
            AbstractNbtList abstractNbtList = (AbstractNbtList)current;
            int i = abstractNbtList.size();
            int n = j = this.index < 0 ? i + this.index : this.index;
            if (0 <= j && j < i) {
                results.add(abstractNbtList.method_10534(j));
            }
        }
    }

    @Override
    public void getOrInit(NbtElement current, Supplier<NbtElement> source, List<NbtElement> results) {
        this.get(current, results);
    }

    @Override
    public NbtElement init() {
        return new NbtList();
    }

    @Override
    public int set(NbtElement current, Supplier<NbtElement> source) {
        if (current instanceof AbstractNbtList) {
            int j;
            AbstractNbtList abstractNbtList = (AbstractNbtList)current;
            int i = abstractNbtList.size();
            int n = j = this.index < 0 ? i + this.index : this.index;
            if (0 <= j && j < i) {
                NbtElement nbtElement = abstractNbtList.method_10534(j);
                NbtElement nbtElement2 = source.get();
                if (!nbtElement2.equals(nbtElement) && abstractNbtList.setElement(j, nbtElement2)) {
                    return 1;
                }
            }
        }
        return 0;
    }

    @Override
    public int clear(NbtElement current) {
        if (current instanceof AbstractNbtList) {
            int j;
            AbstractNbtList abstractNbtList = (AbstractNbtList)current;
            int i = abstractNbtList.size();
            int n = j = this.index < 0 ? i + this.index : this.index;
            if (0 <= j && j < i) {
                abstractNbtList.method_10536(j);
                return 1;
            }
        }
        return 0;
    }
}
