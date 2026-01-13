/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.nbt.scanner;

import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.scanner.NbtCollector;
import org.jspecify.annotations.Nullable;

static class NbtCollector.RootNode
implements NbtCollector.Node {
    private @Nullable NbtElement value;

    NbtCollector.RootNode() {
    }

    @Override
    public void append(NbtElement value) {
        this.value = value;
    }

    @Override
    public @Nullable NbtElement getValue() {
        return this.value;
    }
}
