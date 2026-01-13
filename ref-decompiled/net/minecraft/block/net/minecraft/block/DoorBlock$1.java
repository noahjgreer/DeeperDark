/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.entity.ai.pathing.NavigationType;

static class DoorBlock.1 {
    static final /* synthetic */ int[] field_10947;

    static {
        field_10947 = new int[NavigationType.values().length];
        try {
            DoorBlock.1.field_10947[NavigationType.LAND.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            DoorBlock.1.field_10947[NavigationType.AIR.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            DoorBlock.1.field_10947[NavigationType.WATER.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
