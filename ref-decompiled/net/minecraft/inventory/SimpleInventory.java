package net.minecraft.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

public class SimpleInventory implements Inventory, RecipeInputProvider {
   private final int size;
   public final DefaultedList heldStacks;
   @Nullable
   private List listeners;

   public SimpleInventory(int size) {
      this.size = size;
      this.heldStacks = DefaultedList.ofSize(size, ItemStack.EMPTY);
   }

   public SimpleInventory(ItemStack... items) {
      this.size = items.length;
      this.heldStacks = DefaultedList.copyOf(ItemStack.EMPTY, items);
   }

   public void addListener(InventoryChangedListener listener) {
      if (this.listeners == null) {
         this.listeners = Lists.newArrayList();
      }

      this.listeners.add(listener);
   }

   public void removeListener(InventoryChangedListener listener) {
      if (this.listeners != null) {
         this.listeners.remove(listener);
      }

   }

   public ItemStack getStack(int slot) {
      return slot >= 0 && slot < this.heldStacks.size() ? (ItemStack)this.heldStacks.get(slot) : ItemStack.EMPTY;
   }

   public List clearToList() {
      List list = (List)this.heldStacks.stream().filter((stack) -> {
         return !stack.isEmpty();
      }).collect(Collectors.toList());
      this.clear();
      return list;
   }

   public ItemStack removeStack(int slot, int amount) {
      ItemStack itemStack = Inventories.splitStack(this.heldStacks, slot, amount);
      if (!itemStack.isEmpty()) {
         this.markDirty();
      }

      return itemStack;
   }

   public ItemStack removeItem(Item item, int count) {
      ItemStack itemStack = new ItemStack(item, 0);

      for(int i = this.size - 1; i >= 0; --i) {
         ItemStack itemStack2 = this.getStack(i);
         if (itemStack2.getItem().equals(item)) {
            int j = count - itemStack.getCount();
            ItemStack itemStack3 = itemStack2.split(j);
            itemStack.increment(itemStack3.getCount());
            if (itemStack.getCount() == count) {
               break;
            }
         }
      }

      if (!itemStack.isEmpty()) {
         this.markDirty();
      }

      return itemStack;
   }

   public ItemStack addStack(ItemStack stack) {
      if (stack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         ItemStack itemStack = stack.copy();
         this.addToExistingSlot(itemStack);
         if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
         } else {
            this.addToNewSlot(itemStack);
            return itemStack.isEmpty() ? ItemStack.EMPTY : itemStack;
         }
      }
   }

   public boolean canInsert(ItemStack stack) {
      boolean bl = false;
      java.util.Iterator var3 = this.heldStacks.iterator();

      while(var3.hasNext()) {
         ItemStack itemStack = (ItemStack)var3.next();
         if (itemStack.isEmpty() || ItemStack.areItemsAndComponentsEqual(itemStack, stack) && itemStack.getCount() < itemStack.getMaxCount()) {
            bl = true;
            break;
         }
      }

      return bl;
   }

   public ItemStack removeStack(int slot) {
      ItemStack itemStack = (ItemStack)this.heldStacks.get(slot);
      if (itemStack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         this.heldStacks.set(slot, ItemStack.EMPTY);
         return itemStack;
      }
   }

   public void setStack(int slot, ItemStack stack) {
      this.heldStacks.set(slot, stack);
      stack.capCount(this.getMaxCount(stack));
      this.markDirty();
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      java.util.Iterator var1 = this.heldStacks.iterator();

      ItemStack itemStack;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         itemStack = (ItemStack)var1.next();
      } while(itemStack.isEmpty());

      return false;
   }

   public void markDirty() {
      if (this.listeners != null) {
         java.util.Iterator var1 = this.listeners.iterator();

         while(var1.hasNext()) {
            InventoryChangedListener inventoryChangedListener = (InventoryChangedListener)var1.next();
            inventoryChangedListener.onInventoryChanged(this);
         }
      }

   }

   public boolean canPlayerUse(PlayerEntity player) {
      return true;
   }

   public void clear() {
      this.heldStacks.clear();
      this.markDirty();
   }

   public void provideRecipeInputs(RecipeFinder finder) {
      java.util.Iterator var2 = this.heldStacks.iterator();

      while(var2.hasNext()) {
         ItemStack itemStack = (ItemStack)var2.next();
         finder.addInput(itemStack);
      }

   }

   public String toString() {
      return ((List)this.heldStacks.stream().filter((stack) -> {
         return !stack.isEmpty();
      }).collect(Collectors.toList())).toString();
   }

   private void addToNewSlot(ItemStack stack) {
      for(int i = 0; i < this.size; ++i) {
         ItemStack itemStack = this.getStack(i);
         if (itemStack.isEmpty()) {
            this.setStack(i, stack.copyAndEmpty());
            return;
         }
      }

   }

   private void addToExistingSlot(ItemStack stack) {
      for(int i = 0; i < this.size; ++i) {
         ItemStack itemStack = this.getStack(i);
         if (ItemStack.areItemsAndComponentsEqual(itemStack, stack)) {
            this.transfer(stack, itemStack);
            if (stack.isEmpty()) {
               return;
            }
         }
      }

   }

   private void transfer(ItemStack source, ItemStack target) {
      int i = this.getMaxCount(target);
      int j = Math.min(source.getCount(), i - target.getCount());
      if (j > 0) {
         target.increment(j);
         source.decrement(j);
         this.markDirty();
      }

   }

   public void readDataList(ReadView.TypedListReadView list) {
      this.clear();
      java.util.Iterator var2 = list.iterator();

      while(var2.hasNext()) {
         ItemStack itemStack = (ItemStack)var2.next();
         this.addStack(itemStack);
      }

   }

   public void toDataList(WriteView.ListAppender list) {
      for(int i = 0; i < this.size(); ++i) {
         ItemStack itemStack = this.getStack(i);
         if (!itemStack.isEmpty()) {
            list.add(itemStack);
         }
      }

   }

   public DefaultedList getHeldStacks() {
      return this.heldStacks;
   }
}
