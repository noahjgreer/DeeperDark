/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import net.minecraft.nbt.NbtShort;

static class NbtShort.Cache {
    private static final int MAX = 1024;
    private static final int MIN = -128;
    static final NbtShort[] VALUES = new NbtShort[1153];

    private NbtShort.Cache() {
    }

    static {
        for (int i = 0; i < VALUES.length; ++i) {
            NbtShort.Cache.VALUES[i] = new NbtShort((short)(-128 + i));
        }
    }
}
