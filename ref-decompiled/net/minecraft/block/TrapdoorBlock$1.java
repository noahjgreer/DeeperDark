/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.entity.ai.pathing.NavigationType;

static class TrapdoorBlock.1 {
    static final /* synthetic */ int[] field_11634;

    static {
        field_11634 = new int[NavigationType.values().length];
        try {
            TrapdoorBlock.1.field_11634[NavigationType.LAND.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TrapdoorBlock.1.field_11634[NavigationType.WATER.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TrapdoorBlock.1.field_11634[NavigationType.AIR.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
