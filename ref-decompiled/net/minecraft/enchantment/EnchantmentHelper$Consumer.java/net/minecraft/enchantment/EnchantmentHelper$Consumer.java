/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;

@FunctionalInterface
public static interface EnchantmentHelper.Consumer {
    public void accept(RegistryEntry<Enchantment> var1, int var2);
}
