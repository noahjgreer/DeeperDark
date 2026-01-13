/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.InputSlotFiller;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeFinder;

class AbstractCraftingScreenHandler.1
implements InputSlotFiller.Handler<CraftingRecipe> {
    AbstractCraftingScreenHandler.1() {
    }

    @Override
    public void populateRecipeFinder(RecipeFinder finder) {
        AbstractCraftingScreenHandler.this.populateRecipeFinder(finder);
    }

    @Override
    public void clear() {
        AbstractCraftingScreenHandler.this.craftingResultInventory.clear();
        AbstractCraftingScreenHandler.this.craftingInventory.clear();
    }

    @Override
    public boolean matches(RecipeEntry<CraftingRecipe> entry) {
        return entry.value().matches(AbstractCraftingScreenHandler.this.craftingInventory.createRecipeInput(), AbstractCraftingScreenHandler.this.getPlayer().getEntityWorld());
    }
}
