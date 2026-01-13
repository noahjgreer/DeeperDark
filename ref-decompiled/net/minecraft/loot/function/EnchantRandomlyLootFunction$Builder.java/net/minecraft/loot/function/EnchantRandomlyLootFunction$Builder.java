/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.function;

import java.util.Optional;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;

public static class EnchantRandomlyLootFunction.Builder
extends ConditionalLootFunction.Builder<EnchantRandomlyLootFunction.Builder> {
    private Optional<RegistryEntryList<Enchantment>> options = Optional.empty();
    private boolean onlyCompatible = true;

    @Override
    protected EnchantRandomlyLootFunction.Builder getThisBuilder() {
        return this;
    }

    public EnchantRandomlyLootFunction.Builder option(RegistryEntry<Enchantment> enchantment) {
        this.options = Optional.of(RegistryEntryList.of(enchantment));
        return this;
    }

    public EnchantRandomlyLootFunction.Builder options(RegistryEntryList<Enchantment> options) {
        this.options = Optional.of(options);
        return this;
    }

    public EnchantRandomlyLootFunction.Builder allowIncompatible() {
        this.onlyCompatible = false;
        return this;
    }

    @Override
    public LootFunction build() {
        return new EnchantRandomlyLootFunction(this.getConditions(), this.options, this.onlyCompatible);
    }

    @Override
    protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
        return this.getThisBuilder();
    }
}
