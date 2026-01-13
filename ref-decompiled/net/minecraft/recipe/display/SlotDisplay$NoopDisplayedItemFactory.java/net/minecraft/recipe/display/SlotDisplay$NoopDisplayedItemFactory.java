/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe.display;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.display.DisplayedItemFactory;

public static class SlotDisplay.NoopDisplayedItemFactory
implements DisplayedItemFactory.FromStack<ItemStack> {
    public static final SlotDisplay.NoopDisplayedItemFactory INSTANCE = new SlotDisplay.NoopDisplayedItemFactory();

    @Override
    public ItemStack toDisplayed(ItemStack itemStack) {
        return itemStack;
    }

    @Override
    public /* synthetic */ Object toDisplayed(ItemStack stack) {
        return this.toDisplayed(stack);
    }
}
