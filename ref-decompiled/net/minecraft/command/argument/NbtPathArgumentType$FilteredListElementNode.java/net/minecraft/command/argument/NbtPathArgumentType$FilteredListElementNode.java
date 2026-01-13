/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.mutable.MutableBoolean
 */
package net.minecraft.command.argument;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import org.apache.commons.lang3.mutable.MutableBoolean;

static class NbtPathArgumentType.FilteredListElementNode
implements NbtPathArgumentType.PathNode {
    private final NbtCompound filter;
    private final Predicate<NbtElement> predicate;

    public NbtPathArgumentType.FilteredListElementNode(NbtCompound filter) {
        this.filter = filter;
        this.predicate = NbtPathArgumentType.getPredicate(filter);
    }

    @Override
    public void get(NbtElement current, List<NbtElement> results) {
        if (current instanceof NbtList) {
            NbtList nbtList = (NbtList)current;
            nbtList.stream().filter(this.predicate).forEach(results::add);
        }
    }

    @Override
    public void getOrInit(NbtElement current, Supplier<NbtElement> source, List<NbtElement> results) {
        MutableBoolean mutableBoolean = new MutableBoolean();
        if (current instanceof NbtList) {
            NbtList nbtList = (NbtList)current;
            nbtList.stream().filter(this.predicate).forEach(nbt -> {
                results.add((NbtElement)nbt);
                mutableBoolean.setTrue();
            });
            if (mutableBoolean.isFalse()) {
                NbtCompound nbtCompound = this.filter.copy();
                nbtList.add(nbtCompound);
                results.add(nbtCompound);
            }
        }
    }

    @Override
    public NbtElement init() {
        return new NbtList();
    }

    @Override
    public int set(NbtElement current, Supplier<NbtElement> source) {
        int i = 0;
        if (current instanceof NbtList) {
            NbtList nbtList = (NbtList)current;
            int j = nbtList.size();
            if (j == 0) {
                nbtList.add(source.get());
                ++i;
            } else {
                for (int k = 0; k < j; ++k) {
                    NbtElement nbtElement2;
                    NbtElement nbtElement = nbtList.method_10534(k);
                    if (!this.predicate.test(nbtElement) || (nbtElement2 = source.get()).equals(nbtElement) || !nbtList.setElement(k, nbtElement2)) continue;
                    ++i;
                }
            }
        }
        return i;
    }

    @Override
    public int clear(NbtElement current) {
        int i = 0;
        if (current instanceof NbtList) {
            NbtList nbtList = (NbtList)current;
            for (int j = nbtList.size() - 1; j >= 0; --j) {
                if (!this.predicate.test(nbtList.method_10534(j))) continue;
                nbtList.method_10536(j);
                ++i;
            }
        }
        return i;
    }
}
