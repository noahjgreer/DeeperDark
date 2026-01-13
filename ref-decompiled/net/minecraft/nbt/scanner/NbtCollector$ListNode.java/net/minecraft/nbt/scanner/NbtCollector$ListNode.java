/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt.scanner;

import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.scanner.NbtCollector;

static class NbtCollector.ListNode
implements NbtCollector.Node {
    private final NbtList value = new NbtList();

    NbtCollector.ListNode() {
    }

    @Override
    public void append(NbtElement value) {
        this.value.unwrapAndAdd(value);
    }

    @Override
    public NbtElement getValue() {
        return this.value;
    }
}
