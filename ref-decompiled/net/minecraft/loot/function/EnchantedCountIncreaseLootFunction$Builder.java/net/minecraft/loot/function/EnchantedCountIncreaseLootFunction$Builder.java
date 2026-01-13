/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.function;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.EnchantedCountIncreaseLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.registry.entry.RegistryEntry;

public static class EnchantedCountIncreaseLootFunction.Builder
extends ConditionalLootFunction.Builder<EnchantedCountIncreaseLootFunction.Builder> {
    private final RegistryEntry<Enchantment> enchantment;
    private final LootNumberProvider count;
    private int limit = 0;

    public EnchantedCountIncreaseLootFunction.Builder(RegistryEntry<Enchantment> enchantment, LootNumberProvider count) {
        this.enchantment = enchantment;
        this.count = count;
    }

    @Override
    protected EnchantedCountIncreaseLootFunction.Builder getThisBuilder() {
        return this;
    }

    public EnchantedCountIncreaseLootFunction.Builder withLimit(int limit) {
        this.limit = limit;
        return this;
    }

    @Override
    public LootFunction build() {
        return new EnchantedCountIncreaseLootFunction(this.getConditions(), this.enchantment, this.count, this.limit);
    }

    @Override
    protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
        return this.getThisBuilder();
    }
}
