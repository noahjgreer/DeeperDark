/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data;

import net.minecraft.nbt.NbtCompound;

@FunctionalInterface
public static interface SnbtProvider.Tweaker {
    public NbtCompound write(String var1, NbtCompound var2);
}
