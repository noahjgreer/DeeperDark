/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.enchantment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.effect.AttributeEnchantmentEffect;
import net.minecraft.enchantment.effect.EnchantmentEffectEntry;
import net.minecraft.enchantment.effect.EnchantmentEffectTarget;
import net.minecraft.enchantment.effect.TargetedEnchantmentEffect;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;

public static class Enchantment.Builder {
    private final Enchantment.Definition definition;
    private RegistryEntryList<Enchantment> exclusiveSet = RegistryEntryList.of(new RegistryEntry[0]);
    private final Map<ComponentType<?>, List<?>> effectLists = new HashMap();
    private final ComponentMap.Builder effectMap = ComponentMap.builder();

    public Enchantment.Builder(Enchantment.Definition properties) {
        this.definition = properties;
    }

    public Enchantment.Builder exclusiveSet(RegistryEntryList<Enchantment> exclusiveSet) {
        this.exclusiveSet = exclusiveSet;
        return this;
    }

    public <E> Enchantment.Builder addEffect(ComponentType<List<EnchantmentEffectEntry<E>>> effectType, E effect, LootCondition.Builder requirements) {
        this.getEffectsList(effectType).add(new EnchantmentEffectEntry<E>(effect, Optional.of(requirements.build())));
        return this;
    }

    public <E> Enchantment.Builder addEffect(ComponentType<List<EnchantmentEffectEntry<E>>> effectType, E effect) {
        this.getEffectsList(effectType).add(new EnchantmentEffectEntry<E>(effect, Optional.empty()));
        return this;
    }

    public <E> Enchantment.Builder addEffect(ComponentType<List<TargetedEnchantmentEffect<E>>> type, EnchantmentEffectTarget enchanted, EnchantmentEffectTarget affected, E effect, LootCondition.Builder requirements) {
        this.getEffectsList(type).add(new TargetedEnchantmentEffect<E>(enchanted, affected, effect, Optional.of(requirements.build())));
        return this;
    }

    public <E> Enchantment.Builder addEffect(ComponentType<List<TargetedEnchantmentEffect<E>>> type, EnchantmentEffectTarget enchanted, EnchantmentEffectTarget affected, E effect) {
        this.getEffectsList(type).add(new TargetedEnchantmentEffect<E>(enchanted, affected, effect, Optional.empty()));
        return this;
    }

    public Enchantment.Builder addEffect(ComponentType<List<AttributeEnchantmentEffect>> type, AttributeEnchantmentEffect effect) {
        this.getEffectsList(type).add(effect);
        return this;
    }

    public <E> Enchantment.Builder addNonListEffect(ComponentType<E> type, E effect) {
        this.effectMap.add(type, effect);
        return this;
    }

    public Enchantment.Builder addEffect(ComponentType<Unit> type) {
        this.effectMap.add(type, Unit.INSTANCE);
        return this;
    }

    private <E> List<E> getEffectsList(ComponentType<List<E>> type2) {
        return this.effectLists.computeIfAbsent(type2, type -> {
            ArrayList arrayList = new ArrayList();
            this.effectMap.add(type2, arrayList);
            return arrayList;
        });
    }

    public Enchantment build(Identifier id) {
        return new Enchantment(Text.translatable(Util.createTranslationKey("enchantment", id)), this.definition, this.exclusiveSet, this.effectMap.build());
    }
}
