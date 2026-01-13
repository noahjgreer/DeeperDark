/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import net.minecraft.nbt.NbtByte;

static class NbtByte.Cache {
    static final NbtByte[] VALUES = new NbtByte[256];

    private NbtByte.Cache() {
    }

    static {
        for (int i = 0; i < VALUES.length; ++i) {
            NbtByte.Cache.VALUES[i] = new NbtByte((byte)(i - 128));
        }
    }
}
