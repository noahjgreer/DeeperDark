/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import net.minecraft.nbt.scanner.NbtScanner;

static class NbtIo.1 {
    static final /* synthetic */ int[] field_36247;

    static {
        field_36247 = new int[NbtScanner.Result.values().length];
        try {
            NbtIo.1.field_36247[NbtScanner.Result.HALT.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            NbtIo.1.field_36247[NbtScanner.Result.BREAK.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            NbtIo.1.field_36247[NbtScanner.Result.CONTINUE.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
