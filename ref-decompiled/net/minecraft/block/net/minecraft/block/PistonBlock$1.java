/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.piston.PistonBehavior;

static class PistonBlock.1 {
    static final /* synthetic */ int[] field_12192;

    static {
        field_12192 = new int[PistonBehavior.values().length];
        try {
            PistonBlock.1.field_12192[PistonBehavior.BLOCK.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PistonBlock.1.field_12192[PistonBehavior.DESTROY.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PistonBlock.1.field_12192[PistonBehavior.PUSH_ONLY.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
