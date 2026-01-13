/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import net.minecraft.nbt.scanner.NbtScanner;

static class NbtCompound.2 {
    static final /* synthetic */ int[] field_36243;
    static final /* synthetic */ int[] field_36244;

    static {
        field_36244 = new int[NbtScanner.Result.values().length];
        try {
            NbtCompound.2.field_36244[NbtScanner.Result.HALT.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            NbtCompound.2.field_36244[NbtScanner.Result.BREAK.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_36243 = new int[NbtScanner.NestedResult.values().length];
        try {
            NbtCompound.2.field_36243[NbtScanner.NestedResult.HALT.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            NbtCompound.2.field_36243[NbtScanner.NestedResult.BREAK.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            NbtCompound.2.field_36243[NbtScanner.NestedResult.SKIP.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
