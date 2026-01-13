/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;

static class VineBlock.1 {
    static final /* synthetic */ int[] field_11708;
    static final /* synthetic */ int[] field_11707;

    static {
        field_11707 = new int[BlockMirror.values().length];
        try {
            VineBlock.1.field_11707[BlockMirror.LEFT_RIGHT.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            VineBlock.1.field_11707[BlockMirror.FRONT_BACK.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_11708 = new int[BlockRotation.values().length];
        try {
            VineBlock.1.field_11708[BlockRotation.CLOCKWISE_180.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            VineBlock.1.field_11708[BlockRotation.COUNTERCLOCKWISE_90.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            VineBlock.1.field_11708[BlockRotation.CLOCKWISE_90.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
