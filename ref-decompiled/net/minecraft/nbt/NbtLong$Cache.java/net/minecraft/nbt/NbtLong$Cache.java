/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import net.minecraft.nbt.NbtLong;

static class NbtLong.Cache {
    private static final int MAX = 1024;
    private static final int MIN = -128;
    static final NbtLong[] VALUES = new NbtLong[1153];

    private NbtLong.Cache() {
    }

    static {
        for (int i = 0; i < VALUES.length; ++i) {
            NbtLong.Cache.VALUES[i] = new NbtLong(-128 + i);
        }
    }
}
