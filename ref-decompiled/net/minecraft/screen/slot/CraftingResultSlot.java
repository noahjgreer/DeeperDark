package net.minecraft.screen.slot;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class CraftingResultSlot extends Slot {
   private final RecipeInputInventory input;
   private final PlayerEntity player;
   private int amount;

   public CraftingResultSlot(PlayerEntity player, RecipeInputInventory input, Inventory inventory, int index, int x, int y) {
      super(inventory, index, x, y);
      this.player = player;
      this.input = input;
   }

   public boolean canInsert(ItemStack stack) {
      return false;
   }

   public ItemStack takeStack(int amount) {
      if (this.hasStack()) {
         this.amount += Math.min(amount, this.getStack().getCount());
      }

      return super.takeStack(amount);
   }

   protected void onCrafted(ItemStack stack, int amount) {
      this.amount += amount;
      this.onCrafted(stack);
   }

   protected void onTake(int amount) {
      this.amount += amount;
   }

   protected void onCrafted(ItemStack stack) {
      if (this.amount > 0) {
         stack.onCraftByPlayer(this.player, this.amount);
      }

      Inventory var3 = this.inventory;
      if (var3 instanceof RecipeUnlocker recipeUnlocker) {
         recipeUnlocker.unlockLastRecipe(this.player, this.input.getHeldStacks());
      }

      this.amount = 0;
   }

   private static DefaultedList copyInput(CraftingRecipeInput input) {
      DefaultedList defaultedList = DefaultedList.ofSize(input.size(), ItemStack.EMPTY);

      for(int i = 0; i < defaultedList.size(); ++i) {
         defaultedList.set(i, input.getStackInSlot(i));
      }

      return defaultedList;
   }

   private DefaultedList getRecipeRemainders(CraftingRecipeInput input, World world) {
      if (world instanceof ServerWorld serverWorld) {
         return (DefaultedList)serverWorld.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, input, serverWorld).map((recipe) -> {
            return ((CraftingRecipe)recipe.value()).getRecipeRemainders(input);
         }).orElseGet(() -> {
            return copyInput(input);
         });
      } else {
         return CraftingRecipe.collectRecipeRemainders(input);
      }
   }

   public void onTakeItem(PlayerEntity player, ItemStack stack) {
      this.onCrafted(stack);
      CraftingRecipeInput.Positioned positioned = this.input.createPositionedRecipeInput();
      CraftingRecipeInput craftingRecipeInput = positioned.input();
      int i = positioned.left();
      int j = positioned.top();
      DefaultedList defaultedList = this.getRecipeRemainders(craftingRecipeInput, player.getWorld());

      for(int k = 0; k < craftingRecipeInput.getHeight(); ++k) {
         for(int l = 0; l < craftingRecipeInput.getWidth(); ++l) {
            int m = l + i + (k + j) * this.input.getWidth();
            ItemStack itemStack = this.input.getStack(m);
            ItemStack itemStack2 = (ItemStack)defaultedList.get(l + k * craftingRecipeInput.getWidth());
            if (!itemStack.isEmpty()) {
               this.input.removeStack(m, 1);
               itemStack = this.input.getStack(m);
            }

            if (!itemStack2.isEmpty()) {
               if (itemStack.isEmpty()) {
                  this.input.setStack(m, itemStack2);
               } else if (ItemStack.areItemsAndComponentsEqual(itemStack, itemStack2)) {
                  itemStack2.increment(itemStack.getCount());
                  this.input.setStack(m, itemStack2);
               } else if (!this.player.getInventory().insertStack(itemStack2)) {
                  this.player.dropItem(itemStack2, false);
               }
            }
         }
      }

   }

   public boolean disablesDynamicDisplay() {
      return true;
   }
}
