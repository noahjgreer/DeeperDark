/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe.display;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.display.DisplayedItemFactory;
import net.minecraft.registry.entry.RegistryEntry;

public static interface DisplayedItemFactory.FromStack<T>
extends DisplayedItemFactory<T> {
    default public T toDisplayed(RegistryEntry<Item> item) {
        return this.toDisplayed(new ItemStack(item));
    }

    default public T toDisplayed(Item item) {
        return this.toDisplayed(new ItemStack(item));
    }

    public T toDisplayed(ItemStack var1);
}
