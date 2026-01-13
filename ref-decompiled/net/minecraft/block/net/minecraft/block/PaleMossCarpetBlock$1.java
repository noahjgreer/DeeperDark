/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.enums.WallShape;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;

static class PaleMossCarpetBlock.1 {
    static final /* synthetic */ int[] field_54771;
    static final /* synthetic */ int[] field_54772;
    static final /* synthetic */ int[] field_54770;

    static {
        field_54770 = new int[WallShape.values().length];
        try {
            PaleMossCarpetBlock.1.field_54770[WallShape.NONE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PaleMossCarpetBlock.1.field_54770[WallShape.LOW.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PaleMossCarpetBlock.1.field_54770[WallShape.TALL.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_54772 = new int[BlockMirror.values().length];
        try {
            PaleMossCarpetBlock.1.field_54772[BlockMirror.LEFT_RIGHT.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PaleMossCarpetBlock.1.field_54772[BlockMirror.FRONT_BACK.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_54771 = new int[BlockRotation.values().length];
        try {
            PaleMossCarpetBlock.1.field_54771[BlockRotation.CLOCKWISE_180.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PaleMossCarpetBlock.1.field_54771[BlockRotation.COUNTERCLOCKWISE_90.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PaleMossCarpetBlock.1.field_54771[BlockRotation.CLOCKWISE_90.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
