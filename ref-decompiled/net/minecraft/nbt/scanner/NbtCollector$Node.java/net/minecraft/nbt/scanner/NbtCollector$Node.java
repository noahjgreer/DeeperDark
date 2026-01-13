/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.nbt.scanner;

import net.minecraft.nbt.NbtElement;
import org.jspecify.annotations.Nullable;

static interface NbtCollector.Node {
    default public void setKey(String key) {
    }

    public void append(NbtElement var1);

    public @Nullable NbtElement getValue();
}
