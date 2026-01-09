package net.minecraft.recipe.input;

import net.minecraft.item.ItemStack;

public record SmithingRecipeInput(ItemStack template, ItemStack base, ItemStack addition) implements RecipeInput {
   public SmithingRecipeInput(ItemStack itemStack, ItemStack itemStack2, ItemStack itemStack3) {
      this.template = itemStack;
      this.base = itemStack2;
      this.addition = itemStack3;
   }

   public ItemStack getStackInSlot(int slot) {
      ItemStack var10000;
      switch (slot) {
         case 0:
            var10000 = this.template;
            break;
         case 1:
            var10000 = this.base;
            break;
         case 2:
            var10000 = this.addition;
            break;
         default:
            throw new IllegalArgumentException("Recipe does not contain slot " + slot);
      }

      return var10000;
   }

   public int size() {
      return 3;
   }

   public boolean isEmpty() {
      return this.template.isEmpty() && this.base.isEmpty() && this.addition.isEmpty();
   }

   public ItemStack template() {
      return this.template;
   }

   public ItemStack base() {
      return this.base;
   }

   public ItemStack addition() {
      return this.addition;
   }
}
