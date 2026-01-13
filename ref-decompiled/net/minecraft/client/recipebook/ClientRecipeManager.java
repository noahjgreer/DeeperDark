/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.recipebook.ClientRecipeManager
 *  net.minecraft.recipe.RecipeManager
 *  net.minecraft.recipe.RecipePropertySet
 *  net.minecraft.recipe.StonecuttingRecipe
 *  net.minecraft.recipe.display.CuttingRecipeDisplay$Grouping
 *  net.minecraft.registry.RegistryKey
 */
package net.minecraft.client.recipebook;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipePropertySet;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.recipe.display.CuttingRecipeDisplay;
import net.minecraft.registry.RegistryKey;

@Environment(value=EnvType.CLIENT)
public class ClientRecipeManager
implements RecipeManager {
    private final Map<RegistryKey<RecipePropertySet>, RecipePropertySet> propertySets;
    private final CuttingRecipeDisplay.Grouping<StonecuttingRecipe> recipes;

    public ClientRecipeManager(Map<RegistryKey<RecipePropertySet>, RecipePropertySet> propertySets, CuttingRecipeDisplay.Grouping<StonecuttingRecipe> recipes) {
        this.propertySets = propertySets;
        this.recipes = recipes;
    }

    public RecipePropertySet getPropertySet(RegistryKey<RecipePropertySet> key) {
        return this.propertySets.getOrDefault(key, RecipePropertySet.EMPTY);
    }

    public CuttingRecipeDisplay.Grouping<StonecuttingRecipe> getStonecutterRecipes() {
        return this.recipes;
    }
}

