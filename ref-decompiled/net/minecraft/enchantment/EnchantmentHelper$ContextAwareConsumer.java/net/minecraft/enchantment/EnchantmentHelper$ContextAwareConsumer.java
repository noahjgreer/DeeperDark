/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.registry.entry.RegistryEntry;

@FunctionalInterface
public static interface EnchantmentHelper.ContextAwareConsumer {
    public void accept(RegistryEntry<Enchantment> var1, int var2, EnchantmentEffectContext var3);
}
