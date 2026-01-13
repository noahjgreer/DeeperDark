/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data.recipe;

import net.minecraft.data.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.ItemConvertible;

@FunctionalInterface
static interface RecipeGenerator.BlockFamilyRecipeFactory {
    public CraftingRecipeJsonBuilder create(RecipeGenerator var1, ItemConvertible var2, ItemConvertible var3);
}
