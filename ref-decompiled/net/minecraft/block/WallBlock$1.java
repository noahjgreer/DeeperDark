/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.enums.WallShape;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;

static class WallBlock.1 {
    static final /* synthetic */ int[] field_22168;
    static final /* synthetic */ int[] field_22169;
    static final /* synthetic */ int[] field_55821;

    static {
        field_55821 = new int[WallShape.values().length];
        try {
            WallBlock.1.field_55821[WallShape.NONE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            WallBlock.1.field_55821[WallShape.LOW.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            WallBlock.1.field_55821[WallShape.TALL.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_22169 = new int[BlockMirror.values().length];
        try {
            WallBlock.1.field_22169[BlockMirror.LEFT_RIGHT.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            WallBlock.1.field_22169[BlockMirror.FRONT_BACK.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_22168 = new int[BlockRotation.values().length];
        try {
            WallBlock.1.field_22168[BlockRotation.CLOCKWISE_180.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            WallBlock.1.field_22168[BlockRotation.COUNTERCLOCKWISE_90.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            WallBlock.1.field_22168[BlockRotation.CLOCKWISE_90.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
