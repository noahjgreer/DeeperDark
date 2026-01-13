/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.enchantment;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.Optional;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.dynamic.Codecs;

public static final class Enchantment.Definition
extends Record {
    final RegistryEntryList<Item> supportedItems;
    final Optional<RegistryEntryList<Item>> primaryItems;
    private final int weight;
    private final int maxLevel;
    private final Enchantment.Cost minCost;
    private final Enchantment.Cost maxCost;
    private final int anvilCost;
    private final List<AttributeModifierSlot> slots;
    public static final MapCodec<Enchantment.Definition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)RegistryCodecs.entryList(RegistryKeys.ITEM).fieldOf("supported_items").forGetter(Enchantment.Definition::supportedItems), (App)RegistryCodecs.entryList(RegistryKeys.ITEM).optionalFieldOf("primary_items").forGetter(Enchantment.Definition::primaryItems), (App)Codecs.rangedInt(1, 1024).fieldOf("weight").forGetter(Enchantment.Definition::weight), (App)Codecs.rangedInt(1, 255).fieldOf("max_level").forGetter(Enchantment.Definition::maxLevel), (App)Enchantment.Cost.CODEC.fieldOf("min_cost").forGetter(Enchantment.Definition::minCost), (App)Enchantment.Cost.CODEC.fieldOf("max_cost").forGetter(Enchantment.Definition::maxCost), (App)Codecs.NON_NEGATIVE_INT.fieldOf("anvil_cost").forGetter(Enchantment.Definition::anvilCost), (App)AttributeModifierSlot.CODEC.listOf().fieldOf("slots").forGetter(Enchantment.Definition::slots)).apply((Applicative)instance, Enchantment.Definition::new));

    public Enchantment.Definition(RegistryEntryList<Item> supportedItems, Optional<RegistryEntryList<Item>> primaryItems, int weight, int maxLevel, Enchantment.Cost minCost, Enchantment.Cost maxCost, int anvilCost, List<AttributeModifierSlot> slots) {
        this.supportedItems = supportedItems;
        this.primaryItems = primaryItems;
        this.weight = weight;
        this.maxLevel = maxLevel;
        this.minCost = minCost;
        this.maxCost = maxCost;
        this.anvilCost = anvilCost;
        this.slots = slots;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{Enchantment.Definition.class, "supportedItems;primaryItems;weight;maxLevel;minCost;maxCost;anvilCost;slots", "supportedItems", "primaryItems", "weight", "maxLevel", "minCost", "maxCost", "anvilCost", "slots"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Enchantment.Definition.class, "supportedItems;primaryItems;weight;maxLevel;minCost;maxCost;anvilCost;slots", "supportedItems", "primaryItems", "weight", "maxLevel", "minCost", "maxCost", "anvilCost", "slots"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Enchantment.Definition.class, "supportedItems;primaryItems;weight;maxLevel;minCost;maxCost;anvilCost;slots", "supportedItems", "primaryItems", "weight", "maxLevel", "minCost", "maxCost", "anvilCost", "slots"}, this, object);
    }

    public RegistryEntryList<Item> supportedItems() {
        return this.supportedItems;
    }

    public Optional<RegistryEntryList<Item>> primaryItems() {
        return this.primaryItems;
    }

    public int weight() {
        return this.weight;
    }

    public int maxLevel() {
        return this.maxLevel;
    }

    public Enchantment.Cost minCost() {
        return this.minCost;
    }

    public Enchantment.Cost maxCost() {
        return this.maxCost;
    }

    public int anvilCost() {
        return this.anvilCost;
    }

    public List<AttributeModifierSlot> slots() {
        return this.slots;
    }
}
