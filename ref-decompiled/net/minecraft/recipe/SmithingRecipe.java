package net.minecraft.recipe;

import java.util.Optional;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.world.World;

public interface SmithingRecipe extends Recipe {
   default RecipeType getType() {
      return RecipeType.SMITHING;
   }

   RecipeSerializer getSerializer();

   default boolean matches(SmithingRecipeInput smithingRecipeInput, World world) {
      return Ingredient.matches(this.template(), smithingRecipeInput.template()) && this.base().test(smithingRecipeInput.base()) && Ingredient.matches(this.addition(), smithingRecipeInput.addition());
   }

   Optional template();

   Ingredient base();

   Optional addition();

   default RecipeBookCategory getRecipeBookCategory() {
      return RecipeBookCategories.SMITHING;
   }
}
