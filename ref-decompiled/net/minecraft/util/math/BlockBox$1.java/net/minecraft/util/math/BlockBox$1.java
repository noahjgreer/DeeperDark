/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.math;

import net.minecraft.util.math.Direction;

static class BlockBox.1 {
    static final /* synthetic */ int[] field_14382;

    static {
        field_14382 = new int[Direction.values().length];
        try {
            BlockBox.1.field_14382[Direction.SOUTH.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockBox.1.field_14382[Direction.NORTH.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockBox.1.field_14382[Direction.WEST.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockBox.1.field_14382[Direction.EAST.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
