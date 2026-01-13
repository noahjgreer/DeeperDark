/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.math;

import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;

static class BlockPos.7 {
    static final /* synthetic */ int[] field_11006;
    static final /* synthetic */ int[] field_23955;

    static {
        field_23955 = new int[Direction.Axis.values().length];
        try {
            BlockPos.7.field_23955[Direction.Axis.X.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockPos.7.field_23955[Direction.Axis.Y.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockPos.7.field_23955[Direction.Axis.Z.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_11006 = new int[BlockRotation.values().length];
        try {
            BlockPos.7.field_11006[BlockRotation.CLOCKWISE_90.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockPos.7.field_11006[BlockRotation.CLOCKWISE_180.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockPos.7.field_11006[BlockRotation.COUNTERCLOCKWISE_90.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockPos.7.field_11006[BlockRotation.NONE.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
