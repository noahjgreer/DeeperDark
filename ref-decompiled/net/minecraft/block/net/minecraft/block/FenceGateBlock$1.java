/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.entity.ai.pathing.NavigationType;

static class FenceGateBlock.1 {
    static final /* synthetic */ int[] field_11029;

    static {
        field_11029 = new int[NavigationType.values().length];
        try {
            FenceGateBlock.1.field_11029[NavigationType.LAND.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            FenceGateBlock.1.field_11029[NavigationType.WATER.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            FenceGateBlock.1.field_11029[NavigationType.AIR.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
