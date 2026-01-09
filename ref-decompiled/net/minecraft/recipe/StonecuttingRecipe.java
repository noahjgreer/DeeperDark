package net.minecraft.recipe;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.recipe.display.StonecutterRecipeDisplay;

public class StonecuttingRecipe extends SingleStackRecipe {
   public StonecuttingRecipe(String group, Ingredient ingredient, ItemStack result) {
      super(group, ingredient, result);
   }

   public RecipeType getType() {
      return RecipeType.STONECUTTING;
   }

   public RecipeSerializer getSerializer() {
      return RecipeSerializer.STONECUTTING;
   }

   public List getDisplays() {
      return List.of(new StonecutterRecipeDisplay(this.ingredient().toDisplay(), this.createResultDisplay(), new SlotDisplay.ItemSlotDisplay(Items.STONECUTTER)));
   }

   public SlotDisplay createResultDisplay() {
      return new SlotDisplay.StackSlotDisplay(this.result());
   }

   public RecipeBookCategory getRecipeBookCategory() {
      return RecipeBookCategories.STONECUTTER;
   }
}
