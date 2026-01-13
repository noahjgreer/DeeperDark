/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.SingleStackRecipe;

@FunctionalInterface
public static interface SingleStackRecipe.RecipeFactory<T extends SingleStackRecipe> {
    public T create(String var1, Ingredient var2, ItemStack var3);
}
