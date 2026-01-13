/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.predicate.item;

import java.util.Optional;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.component.ComponentsPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;

public static class ItemPredicate.Builder {
    private Optional<RegistryEntryList<Item>> item = Optional.empty();
    private NumberRange.IntRange count = NumberRange.IntRange.ANY;
    private ComponentsPredicate components = ComponentsPredicate.EMPTY;

    public static ItemPredicate.Builder create() {
        return new ItemPredicate.Builder();
    }

    public ItemPredicate.Builder items(RegistryEntryLookup<Item> itemRegistry, ItemConvertible ... items) {
        this.item = Optional.of(RegistryEntryList.of(item -> item.asItem().getRegistryEntry(), items));
        return this;
    }

    public ItemPredicate.Builder tag(RegistryEntryLookup<Item> itemRegistry, TagKey<Item> tag) {
        this.item = Optional.of(itemRegistry.getOrThrow(tag));
        return this;
    }

    public ItemPredicate.Builder count(NumberRange.IntRange count) {
        this.count = count;
        return this;
    }

    public ItemPredicate.Builder components(ComponentsPredicate components) {
        this.components = components;
        return this;
    }

    public ItemPredicate build() {
        return new ItemPredicate(this.item, this.count, this.components);
    }
}
