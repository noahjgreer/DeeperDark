package net.minecraft.client.recipebook;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipePropertySet;
import net.minecraft.recipe.display.CuttingRecipeDisplay;
import net.minecraft.registry.RegistryKey;

@Environment(EnvType.CLIENT)
public class ClientRecipeManager implements RecipeManager {
   private final Map propertySets;
   private final CuttingRecipeDisplay.Grouping recipes;

   public ClientRecipeManager(Map propertySets, CuttingRecipeDisplay.Grouping recipes) {
      this.propertySets = propertySets;
      this.recipes = recipes;
   }

   public RecipePropertySet getPropertySet(RegistryKey key) {
      return (RecipePropertySet)this.propertySets.getOrDefault(key, RecipePropertySet.EMPTY);
   }

   public CuttingRecipeDisplay.Grouping getStonecutterRecipes() {
      return this.recipes;
   }
}
