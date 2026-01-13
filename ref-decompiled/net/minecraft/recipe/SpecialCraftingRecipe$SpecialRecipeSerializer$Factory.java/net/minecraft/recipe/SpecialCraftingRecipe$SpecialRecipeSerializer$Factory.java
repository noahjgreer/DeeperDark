/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe;

import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;

@FunctionalInterface
public static interface SpecialCraftingRecipe.SpecialRecipeSerializer.Factory<T extends CraftingRecipe> {
    public T create(CraftingRecipeCategory var1);
}
