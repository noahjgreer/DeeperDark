/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.ai.pathing.NavigationType;

static class SlabBlock.1 {
    static final /* synthetic */ int[] field_11504;
    static final /* synthetic */ int[] field_11503;

    static {
        field_11503 = new int[NavigationType.values().length];
        try {
            SlabBlock.1.field_11503[NavigationType.LAND.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SlabBlock.1.field_11503[NavigationType.WATER.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SlabBlock.1.field_11503[NavigationType.AIR.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_11504 = new int[SlabType.values().length];
        try {
            SlabBlock.1.field_11504[SlabType.TOP.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SlabBlock.1.field_11504[SlabType.BOTTOM.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SlabBlock.1.field_11504[SlabType.DOUBLE.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
