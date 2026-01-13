/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import net.minecraft.util.math.Direction;

static class MineshaftGenerator.1 {
    static final /* synthetic */ int[] field_14417;

    static {
        field_14417 = new int[Direction.values().length];
        try {
            MineshaftGenerator.1.field_14417[Direction.NORTH.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MineshaftGenerator.1.field_14417[Direction.SOUTH.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MineshaftGenerator.1.field_14417[Direction.WEST.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MineshaftGenerator.1.field_14417[Direction.EAST.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
