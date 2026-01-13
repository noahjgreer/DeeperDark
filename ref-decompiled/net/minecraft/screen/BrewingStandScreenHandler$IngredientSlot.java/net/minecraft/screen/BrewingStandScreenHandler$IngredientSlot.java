/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.screen.slot.Slot;

static class BrewingStandScreenHandler.IngredientSlot
extends Slot {
    private final BrewingRecipeRegistry brewingRecipeRegistry;

    public BrewingStandScreenHandler.IngredientSlot(BrewingRecipeRegistry brewingRecipeRegistry, Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.brewingRecipeRegistry = brewingRecipeRegistry;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return this.brewingRecipeRegistry.isValidIngredient(stack);
    }
}
