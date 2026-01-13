/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.math;

import net.minecraft.util.math.Direction;

static class DirectionTransformation.1 {
    static final /* synthetic */ int[] field_23324;

    static {
        field_23324 = new int[Direction.Axis.values().length];
        try {
            DirectionTransformation.1.field_23324[Direction.Axis.X.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            DirectionTransformation.1.field_23324[Direction.Axis.Y.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            DirectionTransformation.1.field_23324[Direction.Axis.Z.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
