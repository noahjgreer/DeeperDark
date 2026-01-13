/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;

static class HorizontalConnectingBlock.1 {
    static final /* synthetic */ int[] field_10909;
    static final /* synthetic */ int[] field_10908;

    static {
        field_10908 = new int[BlockMirror.values().length];
        try {
            HorizontalConnectingBlock.1.field_10908[BlockMirror.LEFT_RIGHT.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            HorizontalConnectingBlock.1.field_10908[BlockMirror.FRONT_BACK.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_10909 = new int[BlockRotation.values().length];
        try {
            HorizontalConnectingBlock.1.field_10909[BlockRotation.CLOCKWISE_180.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            HorizontalConnectingBlock.1.field_10909[BlockRotation.COUNTERCLOCKWISE_90.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            HorizontalConnectingBlock.1.field_10909[BlockRotation.CLOCKWISE_90.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
