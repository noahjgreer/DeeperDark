/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.enums.RailShape;

static class PoweredRailBlock.1 {
    static final /* synthetic */ int[] field_11368;

    static {
        field_11368 = new int[RailShape.values().length];
        try {
            PoweredRailBlock.1.field_11368[RailShape.NORTH_SOUTH.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PoweredRailBlock.1.field_11368[RailShape.EAST_WEST.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PoweredRailBlock.1.field_11368[RailShape.ASCENDING_EAST.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PoweredRailBlock.1.field_11368[RailShape.ASCENDING_WEST.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PoweredRailBlock.1.field_11368[RailShape.ASCENDING_NORTH.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PoweredRailBlock.1.field_11368[RailShape.ASCENDING_SOUTH.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
