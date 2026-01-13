/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import net.minecraft.util.math.Direction;

static class NetherFortressGenerator.1 {
    static final /* synthetic */ int[] field_14508;

    static {
        field_14508 = new int[Direction.values().length];
        try {
            NetherFortressGenerator.1.field_14508[Direction.NORTH.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            NetherFortressGenerator.1.field_14508[Direction.SOUTH.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            NetherFortressGenerator.1.field_14508[Direction.WEST.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            NetherFortressGenerator.1.field_14508[Direction.EAST.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
