package net.minecraft.data.recipe;

import java.util.function.Function;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ComplexRecipeJsonBuilder {
   private final Function recipeFactory;

   public ComplexRecipeJsonBuilder(Function recipeFactory) {
      this.recipeFactory = recipeFactory;
   }

   public static ComplexRecipeJsonBuilder create(Function recipeFactory) {
      return new ComplexRecipeJsonBuilder(recipeFactory);
   }

   public void offerTo(RecipeExporter exporter, String id) {
      this.offerTo(exporter, RegistryKey.of(RegistryKeys.RECIPE, Identifier.of(id)));
   }

   public void offerTo(RecipeExporter exporter, RegistryKey recipeKey) {
      exporter.accept(recipeKey, (Recipe)this.recipeFactory.apply(CraftingRecipeCategory.MISC), (AdvancementEntry)null);
   }
}
