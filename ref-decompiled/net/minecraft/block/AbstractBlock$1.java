/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.entity.ai.pathing.NavigationType;

static class AbstractBlock.1 {
    static final /* synthetic */ int[] field_10659;

    static {
        field_10659 = new int[NavigationType.values().length];
        try {
            AbstractBlock.1.field_10659[NavigationType.LAND.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AbstractBlock.1.field_10659[NavigationType.WATER.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AbstractBlock.1.field_10659[NavigationType.AIR.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
