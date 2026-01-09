package net.minecraft.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.recipe.book.RecipeBookCategory;

public class SmeltingRecipe extends AbstractCookingRecipe {
   public SmeltingRecipe(String string, CookingRecipeCategory cookingRecipeCategory, Ingredient ingredient, ItemStack itemStack, float f, int i) {
      super(string, cookingRecipeCategory, ingredient, itemStack, f, i);
   }

   protected Item getCookerItem() {
      return Items.FURNACE;
   }

   public RecipeSerializer getSerializer() {
      return RecipeSerializer.SMELTING;
   }

   public RecipeType getType() {
      return RecipeType.SMELTING;
   }

   public RecipeBookCategory getRecipeBookCategory() {
      RecipeBookCategory var10000;
      switch (this.getCategory()) {
         case BLOCKS:
            var10000 = RecipeBookCategories.FURNACE_BLOCKS;
            break;
         case FOOD:
            var10000 = RecipeBookCategories.FURNACE_FOOD;
            break;
         case MISC:
            var10000 = RecipeBookCategories.FURNACE_MISC;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }
}
