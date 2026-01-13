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

static class NbtPathArgumentType.FilteredRootNode
implements NbtPathArgumentType.PathNode {
    private final Predicate<NbtElement> matcher;

    public NbtPathArgumentType.FilteredRootNode(NbtCompound filter) {
        this.matcher = NbtPathArgumentType.getPredicate(filter);
    }

    @Override
    public void get(NbtElement current, List<NbtElement> results) {
        if (current instanceof NbtCompound && this.matcher.test(current)) {
            results.add(current);
        }
    }

    @Override
    public void getOrInit(NbtElement current, Supplier<NbtElement> source, List<NbtElement> results) {
        this.get(current, results);
    }

    @Override
    public NbtElement init() {
        return new NbtCompound();
    }

    @Override
    public int set(NbtElement current, Supplier<NbtElement> source) {
        return 0;
    }

    @Override
    public int clear(NbtElement current) {
        return 0;
    }
}
