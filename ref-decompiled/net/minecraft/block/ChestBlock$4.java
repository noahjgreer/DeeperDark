/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.enums.ChestType;

static class ChestBlock.4 {
    static final /* synthetic */ int[] field_10775;

    static {
        field_10775 = new int[ChestType.values().length];
        try {
            ChestBlock.4.field_10775[ChestType.SINGLE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ChestBlock.4.field_10775[ChestType.LEFT.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ChestBlock.4.field_10775[ChestType.RIGHT.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
