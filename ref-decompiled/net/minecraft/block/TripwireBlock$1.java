/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;

static class TripwireBlock.1 {
    static final /* synthetic */ int[] field_11685;
    static final /* synthetic */ int[] field_11684;

    static {
        field_11684 = new int[BlockMirror.values().length];
        try {
            TripwireBlock.1.field_11684[BlockMirror.LEFT_RIGHT.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TripwireBlock.1.field_11684[BlockMirror.FRONT_BACK.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_11685 = new int[BlockRotation.values().length];
        try {
            TripwireBlock.1.field_11685[BlockRotation.CLOCKWISE_180.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TripwireBlock.1.field_11685[BlockRotation.COUNTERCLOCKWISE_90.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TripwireBlock.1.field_11685[BlockRotation.CLOCKWISE_90.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
