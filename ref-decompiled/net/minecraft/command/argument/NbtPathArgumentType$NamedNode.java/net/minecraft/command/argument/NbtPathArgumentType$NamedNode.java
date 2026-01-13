/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command.argument;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

static class NbtPathArgumentType.NamedNode
implements NbtPathArgumentType.PathNode {
    private final String name;

    public NbtPathArgumentType.NamedNode(String name) {
        this.name = name;
    }

    @Override
    public void get(NbtElement current, List<NbtElement> results) {
        NbtElement nbtElement;
        if (current instanceof NbtCompound && (nbtElement = ((NbtCompound)current).get(this.name)) != null) {
            results.add(nbtElement);
        }
    }

    @Override
    public void getOrInit(NbtElement current, Supplier<NbtElement> source, List<NbtElement> results) {
        if (current instanceof NbtCompound) {
            NbtElement nbtElement;
            NbtCompound nbtCompound = (NbtCompound)current;
            if (nbtCompound.contains(this.name)) {
                nbtElement = nbtCompound.get(this.name);
            } else {
                nbtElement = source.get();
                nbtCompound.put(this.name, nbtElement);
            }
            results.add(nbtElement);
        }
    }

    @Override
    public NbtElement init() {
        return new NbtCompound();
    }

    @Override
    public int set(NbtElement current, Supplier<NbtElement> source) {
        if (current instanceof NbtCompound) {
            NbtElement nbtElement2;
            NbtCompound nbtCompound = (NbtCompound)current;
            NbtElement nbtElement = source.get();
            if (!nbtElement.equals(nbtElement2 = nbtCompound.put(this.name, nbtElement))) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public int clear(NbtElement current) {
        NbtCompound nbtCompound;
        if (current instanceof NbtCompound && (nbtCompound = (NbtCompound)current).contains(this.name)) {
            nbtCompound.remove(this.name);
            return 1;
        }
        return 0;
    }
}
