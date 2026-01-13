/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.BlockSetType;

static class PressurePlateBlock.1 {
    static final /* synthetic */ int[] field_11360;

    static {
        field_11360 = new int[BlockSetType.ActivationRule.values().length];
        try {
            PressurePlateBlock.1.field_11360[BlockSetType.ActivationRule.EVERYTHING.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PressurePlateBlock.1.field_11360[BlockSetType.ActivationRule.MOBS.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
