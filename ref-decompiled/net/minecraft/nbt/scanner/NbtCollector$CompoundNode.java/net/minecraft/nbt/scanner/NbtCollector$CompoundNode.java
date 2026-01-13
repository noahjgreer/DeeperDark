/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt.scanner;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.scanner.NbtCollector;

static class NbtCollector.CompoundNode
implements NbtCollector.Node {
    private final NbtCompound value = new NbtCompound();
    private String key = "";

    NbtCollector.CompoundNode() {
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public void append(NbtElement value) {
        this.value.put(this.key, value);
    }

    @Override
    public NbtElement getValue() {
        return this.value;
    }
}
