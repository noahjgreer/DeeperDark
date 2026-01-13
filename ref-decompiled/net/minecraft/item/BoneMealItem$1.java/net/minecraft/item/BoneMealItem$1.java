/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import net.minecraft.block.Fertilizable;

static class BoneMealItem.1 {
    static final /* synthetic */ int[] field_47829;

    static {
        field_47829 = new int[Fertilizable.FertilizableType.values().length];
        try {
            BoneMealItem.1.field_47829[Fertilizable.FertilizableType.NEIGHBOR_SPREADER.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BoneMealItem.1.field_47829[Fertilizable.FertilizableType.GROWER.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
