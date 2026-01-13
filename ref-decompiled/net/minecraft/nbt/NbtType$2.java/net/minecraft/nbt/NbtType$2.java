/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import net.minecraft.nbt.scanner.NbtScanner;

static class NbtType.2 {
    static final /* synthetic */ int[] field_36257;

    static {
        field_36257 = new int[NbtScanner.Result.values().length];
        try {
            NbtType.2.field_36257[NbtScanner.Result.CONTINUE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            NbtType.2.field_36257[NbtScanner.Result.HALT.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            NbtType.2.field_36257[NbtScanner.Result.BREAK.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
