/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.enums.RailShape;

static class ExperimentalMinecartShapeContext.1 {
    static final /* synthetic */ int[] field_53826;

    static {
        field_53826 = new int[RailShape.values().length];
        try {
            ExperimentalMinecartShapeContext.1.field_53826[RailShape.ASCENDING_EAST.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ExperimentalMinecartShapeContext.1.field_53826[RailShape.ASCENDING_WEST.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ExperimentalMinecartShapeContext.1.field_53826[RailShape.ASCENDING_NORTH.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ExperimentalMinecartShapeContext.1.field_53826[RailShape.ASCENDING_SOUTH.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
