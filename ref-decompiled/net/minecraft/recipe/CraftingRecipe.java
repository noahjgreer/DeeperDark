package net.minecraft.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.util.collection.DefaultedList;

public interface CraftingRecipe extends Recipe {
   default RecipeType getType() {
      return RecipeType.CRAFTING;
   }

   RecipeSerializer getSerializer();

   CraftingRecipeCategory getCategory();

   default DefaultedList getRecipeRemainders(CraftingRecipeInput input) {
      return collectRecipeRemainders(input);
   }

   static DefaultedList collectRecipeRemainders(CraftingRecipeInput input) {
      DefaultedList defaultedList = DefaultedList.ofSize(input.size(), ItemStack.EMPTY);

      for(int i = 0; i < defaultedList.size(); ++i) {
         Item item = input.getStackInSlot(i).getItem();
         defaultedList.set(i, item.getRecipeRemainder());
      }

      return defaultedList;
   }

   default RecipeBookCategory getRecipeBookCategory() {
      RecipeBookCategory var10000;
      switch (this.getCategory()) {
         case BUILDING:
            var10000 = RecipeBookCategories.CRAFTING_BUILDING_BLOCKS;
            break;
         case EQUIPMENT:
            var10000 = RecipeBookCategories.CRAFTING_EQUIPMENT;
            break;
         case REDSTONE:
            var10000 = RecipeBookCategories.CRAFTING_REDSTONE;
            break;
         case MISC:
            var10000 = RecipeBookCategories.CRAFTING_MISC;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }
}
