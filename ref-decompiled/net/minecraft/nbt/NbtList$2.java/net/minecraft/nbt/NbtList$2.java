/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import net.minecraft.nbt.scanner.NbtScanner;

static class NbtList.2 {
    static final /* synthetic */ int[] field_36245;
    static final /* synthetic */ int[] field_36246;

    static {
        field_36246 = new int[NbtScanner.NestedResult.values().length];
        try {
            NbtList.2.field_36246[NbtScanner.NestedResult.HALT.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            NbtList.2.field_36246[NbtScanner.NestedResult.BREAK.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            NbtList.2.field_36246[NbtScanner.NestedResult.SKIP.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_36245 = new int[NbtScanner.Result.values().length];
        try {
            NbtList.2.field_36245[NbtScanner.Result.HALT.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            NbtList.2.field_36245[NbtScanner.Result.BREAK.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
