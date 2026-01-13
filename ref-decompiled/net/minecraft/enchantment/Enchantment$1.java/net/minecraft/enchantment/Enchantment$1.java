/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.effect.EnchantmentEffectTarget;

static class Enchantment.1 {
    static final /* synthetic */ int[] field_7133;

    static {
        field_7133 = new int[EnchantmentEffectTarget.values().length];
        try {
            Enchantment.1.field_7133[EnchantmentEffectTarget.ATTACKER.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Enchantment.1.field_7133[EnchantmentEffectTarget.DAMAGING_ENTITY.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Enchantment.1.field_7133[EnchantmentEffectTarget.VICTIM.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
