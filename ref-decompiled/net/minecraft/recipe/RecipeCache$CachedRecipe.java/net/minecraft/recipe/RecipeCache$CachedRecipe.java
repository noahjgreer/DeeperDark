/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.util.collection.DefaultedList;
import org.jspecify.annotations.Nullable;

record RecipeCache.CachedRecipe(DefaultedList<ItemStack> key, int width, int height, @Nullable RecipeEntry<CraftingRecipe> value) {
    public boolean matches(CraftingRecipeInput input) {
        if (this.width != input.getWidth() || this.height != input.getHeight()) {
            return false;
        }
        for (int i = 0; i < this.key.size(); ++i) {
            if (ItemStack.areItemsAndComponentsEqual(this.key.get(i), input.getStackInSlot(i))) continue;
            return false;
        }
        return true;
    }
}
