package net.minecraft.screen.slot;

import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class CrafterOutputSlot extends Slot {
   public CrafterOutputSlot(Inventory inventory, int i, int j, int k) {
      super(inventory, i, j, k);
   }

   public void onQuickTransfer(ItemStack newItem, ItemStack original) {
   }

   public boolean canTakeItems(PlayerEntity playerEntity) {
      return false;
   }

   public Optional tryTakeStackRange(int min, int max, PlayerEntity player) {
      return Optional.empty();
   }

   public ItemStack takeStackRange(int min, int max, PlayerEntity player) {
      return ItemStack.EMPTY;
   }

   public ItemStack insertStack(ItemStack stack) {
      return stack;
   }

   public ItemStack insertStack(ItemStack stack, int count) {
      return this.insertStack(stack);
   }

   public boolean canTakePartial(PlayerEntity player) {
      return false;
   }

   public boolean canInsert(ItemStack stack) {
      return false;
   }

   public ItemStack takeStack(int amount) {
      return ItemStack.EMPTY;
   }

   public void onTakeItem(PlayerEntity player, ItemStack stack) {
   }

   public boolean canBeHighlighted() {
      return false;
   }

   public boolean disablesDynamicDisplay() {
      return true;
   }
}
