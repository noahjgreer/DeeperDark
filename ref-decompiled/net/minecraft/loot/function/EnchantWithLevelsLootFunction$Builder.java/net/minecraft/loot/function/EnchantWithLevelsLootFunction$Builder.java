/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.function;

import java.util.Optional;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.EnchantWithLevelsLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.registry.entry.RegistryEntryList;

public static class EnchantWithLevelsLootFunction.Builder
extends ConditionalLootFunction.Builder<EnchantWithLevelsLootFunction.Builder> {
    private final LootNumberProvider levels;
    private Optional<RegistryEntryList<Enchantment>> options = Optional.empty();

    public EnchantWithLevelsLootFunction.Builder(LootNumberProvider levels) {
        this.levels = levels;
    }

    @Override
    protected EnchantWithLevelsLootFunction.Builder getThisBuilder() {
        return this;
    }

    public EnchantWithLevelsLootFunction.Builder options(RegistryEntryList<Enchantment> options) {
        this.options = Optional.of(options);
        return this;
    }

    @Override
    public LootFunction build() {
        return new EnchantWithLevelsLootFunction(this.getConditions(), this.levels, this.options);
    }

    @Override
    protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
        return this.getThisBuilder();
    }
}
