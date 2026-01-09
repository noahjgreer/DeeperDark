package net.minecraft.recipe;

import net.minecraft.recipe.display.CuttingRecipeDisplay;
import net.minecraft.registry.RegistryKey;

public interface RecipeManager {
   RecipePropertySet getPropertySet(RegistryKey key);

   CuttingRecipeDisplay.Grouping getStonecutterRecipes();
}
