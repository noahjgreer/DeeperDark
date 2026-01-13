/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.SetEnchantmentsLootFunction;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.registry.entry.RegistryEntry;

public static class SetEnchantmentsLootFunction.Builder
extends ConditionalLootFunction.Builder<SetEnchantmentsLootFunction.Builder> {
    private final ImmutableMap.Builder<RegistryEntry<Enchantment>, LootNumberProvider> enchantments = ImmutableMap.builder();
    private final boolean add;

    public SetEnchantmentsLootFunction.Builder() {
        this(false);
    }

    public SetEnchantmentsLootFunction.Builder(boolean add) {
        this.add = add;
    }

    @Override
    protected SetEnchantmentsLootFunction.Builder getThisBuilder() {
        return this;
    }

    public SetEnchantmentsLootFunction.Builder enchantment(RegistryEntry<Enchantment> enchantment, LootNumberProvider level) {
        this.enchantments.put(enchantment, (Object)level);
        return this;
    }

    @Override
    public LootFunction build() {
        return new SetEnchantmentsLootFunction(this.getConditions(), (Map<RegistryEntry<Enchantment>, LootNumberProvider>)this.enchantments.build(), this.add);
    }

    @Override
    protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
        return this.getThisBuilder();
    }
}
