/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.vehicle;

import net.minecraft.block.enums.RailShape;

static class ExperimentalMinecartController.1 {
    static final /* synthetic */ int[] field_52538;

    static {
        field_52538 = new int[RailShape.values().length];
        try {
            ExperimentalMinecartController.1.field_52538[RailShape.ASCENDING_EAST.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ExperimentalMinecartController.1.field_52538[RailShape.ASCENDING_WEST.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ExperimentalMinecartController.1.field_52538[RailShape.ASCENDING_NORTH.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ExperimentalMinecartController.1.field_52538[RailShape.ASCENDING_SOUTH.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
