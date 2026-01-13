/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.vehicle;

import net.minecraft.block.enums.RailShape;

static class DefaultMinecartController.1 {
    static final /* synthetic */ int[] field_7682;

    static {
        field_7682 = new int[RailShape.values().length];
        try {
            DefaultMinecartController.1.field_7682[RailShape.ASCENDING_EAST.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            DefaultMinecartController.1.field_7682[RailShape.ASCENDING_WEST.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            DefaultMinecartController.1.field_7682[RailShape.ASCENDING_NORTH.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            DefaultMinecartController.1.field_7682[RailShape.ASCENDING_SOUTH.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
