/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.entity;

import net.minecraft.util.math.Direction;

static class PistonBlockEntity.1 {
    static final /* synthetic */ int[] field_12210;
    static final /* synthetic */ int[] field_21467;

    static {
        field_21467 = new int[Direction.values().length];
        try {
            PistonBlockEntity.1.field_21467[Direction.EAST.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PistonBlockEntity.1.field_21467[Direction.WEST.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PistonBlockEntity.1.field_21467[Direction.UP.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PistonBlockEntity.1.field_21467[Direction.DOWN.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PistonBlockEntity.1.field_21467[Direction.SOUTH.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PistonBlockEntity.1.field_21467[Direction.NORTH.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_12210 = new int[Direction.Axis.values().length];
        try {
            PistonBlockEntity.1.field_12210[Direction.Axis.X.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PistonBlockEntity.1.field_12210[Direction.Axis.Y.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PistonBlockEntity.1.field_12210[Direction.Axis.Z.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
