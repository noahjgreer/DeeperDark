/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;

static class PillarBlock.1 {
    static final /* synthetic */ int[] field_11461;
    static final /* synthetic */ int[] field_11460;

    static {
        field_11460 = new int[BlockRotation.values().length];
        try {
            PillarBlock.1.field_11460[BlockRotation.COUNTERCLOCKWISE_90.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PillarBlock.1.field_11460[BlockRotation.CLOCKWISE_90.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_11461 = new int[Direction.Axis.values().length];
        try {
            PillarBlock.1.field_11461[Direction.Axis.X.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PillarBlock.1.field_11461[Direction.Axis.Z.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
