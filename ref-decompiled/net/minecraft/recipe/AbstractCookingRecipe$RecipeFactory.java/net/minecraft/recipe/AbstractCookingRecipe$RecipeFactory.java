/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.CookingRecipeCategory;

@FunctionalInterface
public static interface AbstractCookingRecipe.RecipeFactory<T extends AbstractCookingRecipe> {
    public T create(String var1, CookingRecipeCategory var2, Ingredient var3, ItemStack var4, float var5, int var6);
}
