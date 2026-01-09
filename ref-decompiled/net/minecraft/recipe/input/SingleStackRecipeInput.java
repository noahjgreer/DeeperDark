package net.minecraft.recipe.input;

import net.minecraft.item.ItemStack;

public record SingleStackRecipeInput(ItemStack item) implements RecipeInput {
   public SingleStackRecipeInput(ItemStack itemStack) {
      this.item = itemStack;
   }

   public ItemStack getStackInSlot(int slot) {
      if (slot != 0) {
         throw new IllegalArgumentException("No item for index " + slot);
      } else {
         return this.item;
      }
   }

   public int size() {
      return 1;
   }

   public ItemStack item() {
      return this.item;
   }
}
