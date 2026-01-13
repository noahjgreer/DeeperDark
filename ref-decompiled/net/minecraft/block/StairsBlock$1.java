/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.enums.StairShape;
import net.minecraft.util.BlockMirror;

static class StairsBlock.1 {
    static final /* synthetic */ int[] field_11581;
    static final /* synthetic */ int[] field_11580;

    static {
        field_11580 = new int[BlockMirror.values().length];
        try {
            StairsBlock.1.field_11580[BlockMirror.LEFT_RIGHT.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            StairsBlock.1.field_11580[BlockMirror.FRONT_BACK.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_11581 = new int[StairShape.values().length];
        try {
            StairsBlock.1.field_11581[StairShape.STRAIGHT.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            StairsBlock.1.field_11581[StairShape.OUTER_LEFT.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            StairsBlock.1.field_11581[StairShape.INNER_RIGHT.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            StairsBlock.1.field_11581[StairShape.INNER_LEFT.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            StairsBlock.1.field_11581[StairShape.OUTER_RIGHT.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
