package net.minecraft.inventory;

import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;

public class CraftingInventory implements RecipeInputInventory {
   private final DefaultedList stacks;
   private final int width;
   private final int height;
   private final ScreenHandler handler;

   public CraftingInventory(ScreenHandler handler, int width, int height) {
      this(handler, width, height, DefaultedList.ofSize(width * height, ItemStack.EMPTY));
   }

   private CraftingInventory(ScreenHandler handler, int width, int height, DefaultedList stacks) {
      this.stacks = stacks;
      this.handler = handler;
      this.width = width;
      this.height = height;
   }

   public int size() {
      return this.stacks.size();
   }

   public boolean isEmpty() {
      java.util.Iterator var1 = this.stacks.iterator();

      ItemStack itemStack;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         itemStack = (ItemStack)var1.next();
      } while(itemStack.isEmpty());

      return false;
   }

   public ItemStack getStack(int slot) {
      return slot >= this.size() ? ItemStack.EMPTY : (ItemStack)this.stacks.get(slot);
   }

   public ItemStack removeStack(int slot) {
      return Inventories.removeStack(this.stacks, slot);
   }

   public ItemStack removeStack(int slot, int amount) {
      ItemStack itemStack = Inventories.splitStack(this.stacks, slot, amount);
      if (!itemStack.isEmpty()) {
         this.handler.onContentChanged(this);
      }

      return itemStack;
   }

   public void setStack(int slot, ItemStack stack) {
      this.stacks.set(slot, stack);
      this.handler.onContentChanged(this);
   }

   public void markDirty() {
   }

   public boolean canPlayerUse(PlayerEntity player) {
      return true;
   }

   public void clear() {
      this.stacks.clear();
   }

   public int getHeight() {
      return this.height;
   }

   public int getWidth() {
      return this.width;
   }

   public List getHeldStacks() {
      return List.copyOf(this.stacks);
   }

   public void provideRecipeInputs(RecipeFinder finder) {
      java.util.Iterator var2 = this.stacks.iterator();

      while(var2.hasNext()) {
         ItemStack itemStack = (ItemStack)var2.next();
         finder.addInputIfUsable(itemStack);
      }

   }
}
