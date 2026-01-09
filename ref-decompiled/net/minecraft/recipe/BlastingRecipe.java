package net.minecraft.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.recipe.book.RecipeBookCategory;

public class BlastingRecipe extends AbstractCookingRecipe {
   public BlastingRecipe(String string, CookingRecipeCategory cookingRecipeCategory, Ingredient ingredient, ItemStack itemStack, float f, int i) {
      super(string, cookingRecipeCategory, ingredient, itemStack, f, i);
   }

   protected Item getCookerItem() {
      return Items.BLAST_FURNACE;
   }

   public RecipeSerializer getSerializer() {
      return RecipeSerializer.BLASTING;
   }

   public RecipeType getType() {
      return RecipeType.BLASTING;
   }

   public RecipeBookCategory getRecipeBookCategory() {
      RecipeBookCategory var10000;
      switch (this.getCategory()) {
         case BLOCKS:
            var10000 = RecipeBookCategories.BLAST_FURNACE_BLOCKS;
            break;
         case FOOD:
         case MISC:
            var10000 = RecipeBookCategories.BLAST_FURNACE_MISC;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }
}
