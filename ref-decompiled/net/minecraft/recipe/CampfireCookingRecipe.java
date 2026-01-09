package net.minecraft.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.recipe.book.RecipeBookCategory;

public class CampfireCookingRecipe extends AbstractCookingRecipe {
   public CampfireCookingRecipe(String string, CookingRecipeCategory cookingRecipeCategory, Ingredient ingredient, ItemStack itemStack, float f, int i) {
      super(string, cookingRecipeCategory, ingredient, itemStack, f, i);
   }

   protected Item getCookerItem() {
      return Items.CAMPFIRE;
   }

   public RecipeSerializer getSerializer() {
      return RecipeSerializer.CAMPFIRE_COOKING;
   }

   public RecipeType getType() {
      return RecipeType.CAMPFIRE_COOKING;
   }

   public RecipeBookCategory getRecipeBookCategory() {
      return RecipeBookCategories.CAMPFIRE;
   }
}
