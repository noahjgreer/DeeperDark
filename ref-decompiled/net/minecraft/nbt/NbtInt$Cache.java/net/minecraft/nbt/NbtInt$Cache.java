/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import net.minecraft.nbt.NbtInt;

static class NbtInt.Cache {
    private static final int MAX = 1024;
    private static final int MIN = -128;
    static final NbtInt[] VALUES = new NbtInt[1153];

    private NbtInt.Cache() {
    }

    static {
        for (int i = 0; i < VALUES.length; ++i) {
            NbtInt.Cache.VALUES[i] = new NbtInt(-128 + i);
        }
    }
}
