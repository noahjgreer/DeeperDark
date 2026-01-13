/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command.argument;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

static class NbtPathArgumentType.FilteredNamedNode
implements NbtPathArgumentType.PathNode {
    private final String name;
    private final NbtCompound filter;
    private final Predicate<NbtElement> predicate;

    public NbtPathArgumentType.FilteredNamedNode(String name, NbtCompound filter) {
        this.name = name;
        this.filter = filter;
        this.predicate = NbtPathArgumentType.getPredicate(filter);
    }

    @Override
    public void get(NbtElement current, List<NbtElement> results) {
        NbtElement nbtElement;
        if (current instanceof NbtCompound && this.predicate.test(nbtElement = ((NbtCompound)current).get(this.name))) {
            results.add(nbtElement);
        }
    }

    @Override
    public void getOrInit(NbtElement current, Supplier<NbtElement> source, List<NbtElement> results) {
        if (current instanceof NbtCompound) {
            NbtCompound nbtCompound = (NbtCompound)current;
            NbtElement nbtElement = nbtCompound.get(this.name);
            if (nbtElement == null) {
                nbtElement = this.filter.copy();
                nbtCompound.put(this.name, nbtElement);
                results.add(nbtElement);
            } else if (this.predicate.test(nbtElement)) {
                results.add(nbtElement);
            }
        }
    }

    @Override
    public NbtElement init() {
        return new NbtCompound();
    }

    @Override
    public int set(NbtElement current, Supplier<NbtElement> source) {
        NbtElement nbtElement2;
        NbtCompound nbtCompound;
        NbtElement nbtElement;
        if (current instanceof NbtCompound && this.predicate.test(nbtElement = (nbtCompound = (NbtCompound)current).get(this.name)) && !(nbtElement2 = source.get()).equals(nbtElement)) {
            nbtCompound.put(this.name, nbtElement2);
            return 1;
        }
        return 0;
    }

    @Override
    public int clear(NbtElement current) {
        NbtCompound nbtCompound;
        NbtElement nbtElement;
        if (current instanceof NbtCompound && this.predicate.test(nbtElement = (nbtCompound = (NbtCompound)current).get(this.name))) {
            nbtCompound.remove(this.name);
            return 1;
        }
        return 0;
    }
}
