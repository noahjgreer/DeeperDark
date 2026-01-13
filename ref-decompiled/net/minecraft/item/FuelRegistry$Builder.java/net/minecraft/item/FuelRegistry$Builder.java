/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2IntSortedMap
 */
package net.minecraft.item;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntSortedMap;
import net.minecraft.item.FuelRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureSet;

public static class FuelRegistry.Builder {
    private final RegistryWrapper<Item> itemLookup;
    private final FeatureSet enabledFeatures;
    private final Object2IntSortedMap<Item> fuelValues = new Object2IntLinkedOpenHashMap();

    public FuelRegistry.Builder(RegistryWrapper.WrapperLookup registries, FeatureSet enabledFeatures) {
        this.itemLookup = registries.getOrThrow(RegistryKeys.ITEM);
        this.enabledFeatures = enabledFeatures;
    }

    public FuelRegistry build() {
        return new FuelRegistry(this.fuelValues);
    }

    public FuelRegistry.Builder remove(TagKey<Item> tag) {
        this.fuelValues.keySet().removeIf(item -> item.getRegistryEntry().isIn(tag));
        return this;
    }

    public FuelRegistry.Builder add(TagKey<Item> tag2, int value) {
        this.itemLookup.getOptional(tag2).ifPresent(tag -> {
            for (RegistryEntry registryEntry : tag) {
                this.add(value, (Item)registryEntry.value());
            }
        });
        return this;
    }

    public FuelRegistry.Builder add(ItemConvertible item, int value) {
        Item item2 = item.asItem();
        this.add(value, item2);
        return this;
    }

    private void add(int value, Item item) {
        if (item.isEnabled(this.enabledFeatures)) {
            this.fuelValues.put((Object)item, value);
        }
    }
}
