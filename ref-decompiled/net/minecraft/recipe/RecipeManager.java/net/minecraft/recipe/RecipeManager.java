/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.recipe.v1.FabricRecipeManager
 */
package net.minecraft.recipe;

import net.fabricmc.fabric.api.recipe.v1.FabricRecipeManager;
import net.minecraft.recipe.RecipePropertySet;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.recipe.display.CuttingRecipeDisplay;
import net.minecraft.registry.RegistryKey;

public interface RecipeManager
extends FabricRecipeManager {
    public RecipePropertySet getPropertySet(RegistryKey<RecipePropertySet> var1);

    public CuttingRecipeDisplay.Grouping<StonecuttingRecipe> getStonecutterRecipes();
}
