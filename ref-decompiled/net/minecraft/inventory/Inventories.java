package net.minecraft.inventory;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.collection.DefaultedList;

public class Inventories {
   public static final String ITEMS_NBT_KEY = "Items";

   public static ItemStack splitStack(List stacks, int slot, int amount) {
      return slot >= 0 && slot < stacks.size() && !((ItemStack)stacks.get(slot)).isEmpty() && amount > 0 ? ((ItemStack)stacks.get(slot)).split(amount) : ItemStack.EMPTY;
   }

   public static ItemStack removeStack(List stacks, int slot) {
      return slot >= 0 && slot < stacks.size() ? (ItemStack)stacks.set(slot, ItemStack.EMPTY) : ItemStack.EMPTY;
   }

   public static void writeData(WriteView view, DefaultedList stacks) {
      writeData(view, stacks, true);
   }

   public static void writeData(WriteView view, DefaultedList stacks, boolean setIfEmpty) {
      WriteView.ListAppender listAppender = view.getListAppender("Items", StackWithSlot.CODEC);

      for(int i = 0; i < stacks.size(); ++i) {
         ItemStack itemStack = (ItemStack)stacks.get(i);
         if (!itemStack.isEmpty()) {
            listAppender.add(new StackWithSlot(i, itemStack));
         }
      }

      if (listAppender.isEmpty() && !setIfEmpty) {
         view.remove("Items");
      }

   }

   public static void readData(ReadView view, DefaultedList stacks) {
      Iterator var2 = view.getTypedListView("Items", StackWithSlot.CODEC).iterator();

      while(var2.hasNext()) {
         StackWithSlot stackWithSlot = (StackWithSlot)var2.next();
         if (stackWithSlot.isValidSlot(stacks.size())) {
            stacks.set(stackWithSlot.slot(), stackWithSlot.stack());
         }
      }

   }

   public static int remove(Inventory inventory, Predicate shouldRemove, int maxCount, boolean dryRun) {
      int i = 0;

      for(int j = 0; j < inventory.size(); ++j) {
         ItemStack itemStack = inventory.getStack(j);
         int k = remove(itemStack, shouldRemove, maxCount - i, dryRun);
         if (k > 0 && !dryRun && itemStack.isEmpty()) {
            inventory.setStack(j, ItemStack.EMPTY);
         }

         i += k;
      }

      return i;
   }

   public static int remove(ItemStack stack, Predicate shouldRemove, int maxCount, boolean dryRun) {
      if (!stack.isEmpty() && shouldRemove.test(stack)) {
         if (dryRun) {
            return stack.getCount();
         } else {
            int i = maxCount < 0 ? stack.getCount() : Math.min(maxCount, stack.getCount());
            stack.decrement(i);
            return i;
         }
      } else {
         return 0;
      }
   }
}
