/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import net.minecraft.util.Rarity;

static class ItemStack.4 {
    static final /* synthetic */ int[] field_8021;

    static {
        field_8021 = new int[Rarity.values().length];
        try {
            ItemStack.4.field_8021[Rarity.COMMON.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ItemStack.4.field_8021[Rarity.UNCOMMON.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ItemStack.4.field_8021[Rarity.RARE.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
