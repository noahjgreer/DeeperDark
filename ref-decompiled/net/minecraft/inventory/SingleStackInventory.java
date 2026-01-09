package net.minecraft.inventory;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface SingleStackInventory extends Inventory {
   ItemStack getStack();

   default ItemStack decreaseStack(int count) {
      return this.getStack().split(count);
   }

   void setStack(ItemStack stack);

   default ItemStack emptyStack() {
      return this.decreaseStack(this.getMaxCountPerStack());
   }

   default int size() {
      return 1;
   }

   default boolean isEmpty() {
      return this.getStack().isEmpty();
   }

   default void clear() {
      this.emptyStack();
   }

   default ItemStack removeStack(int slot) {
      return this.removeStack(slot, this.getMaxCountPerStack());
   }

   default ItemStack getStack(int slot) {
      return slot == 0 ? this.getStack() : ItemStack.EMPTY;
   }

   default ItemStack removeStack(int slot, int amount) {
      return slot != 0 ? ItemStack.EMPTY : this.decreaseStack(amount);
   }

   default void setStack(int slot, ItemStack stack) {
      if (slot == 0) {
         this.setStack(stack);
      }

   }

   public interface SingleStackBlockEntityInventory extends SingleStackInventory {
      BlockEntity asBlockEntity();

      default boolean canPlayerUse(PlayerEntity player) {
         return Inventory.canPlayerUse(this.asBlockEntity(), player);
      }
   }
}
