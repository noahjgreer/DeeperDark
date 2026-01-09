package net.minecraft.recipe;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.slot.Slot;

public class InputSlotFiller {
   private static final int field_51523 = -1;
   private final PlayerInventory inventory;
   private final Handler handler;
   private final boolean craftAll;
   private final int width;
   private final int height;
   private final List inputSlots;
   private final List slotsToReturn;

   public static AbstractRecipeScreenHandler.PostFillAction fill(Handler handler, int width, int height, List inputSlots, List slotsToReturn, PlayerInventory inventory, RecipeEntry recipe, boolean craftAll, boolean creative) {
      InputSlotFiller inputSlotFiller = new InputSlotFiller(handler, inventory, craftAll, width, height, inputSlots, slotsToReturn);
      if (!creative && !inputSlotFiller.canReturnInputs()) {
         return AbstractRecipeScreenHandler.PostFillAction.NOTHING;
      } else {
         RecipeFinder recipeFinder = new RecipeFinder();
         inventory.populateRecipeFinder(recipeFinder);
         handler.populateRecipeFinder(recipeFinder);
         return inputSlotFiller.tryFill(recipe, recipeFinder);
      }
   }

   private InputSlotFiller(Handler handler, PlayerInventory inventory, boolean craftAll, int width, int height, List inputSlots, List slotsToReturn) {
      this.handler = handler;
      this.inventory = inventory;
      this.craftAll = craftAll;
      this.width = width;
      this.height = height;
      this.inputSlots = inputSlots;
      this.slotsToReturn = slotsToReturn;
   }

   private AbstractRecipeScreenHandler.PostFillAction tryFill(RecipeEntry recipe, RecipeFinder finder) {
      if (finder.isCraftable((Recipe)recipe.value(), (RecipeMatcher.ItemCallback)null)) {
         this.fill(recipe, finder);
         this.inventory.markDirty();
         return AbstractRecipeScreenHandler.PostFillAction.NOTHING;
      } else {
         this.returnInputs();
         this.inventory.markDirty();
         return AbstractRecipeScreenHandler.PostFillAction.PLACE_GHOST_RECIPE;
      }
   }

   private void returnInputs() {
      Iterator var1 = this.slotsToReturn.iterator();

      while(var1.hasNext()) {
         Slot slot = (Slot)var1.next();
         ItemStack itemStack = slot.getStack().copy();
         this.inventory.offer(itemStack, false);
         slot.setStackNoCallbacks(itemStack);
      }

      this.handler.clear();
   }

   private void fill(RecipeEntry recipe, RecipeFinder finder) {
      boolean bl = this.handler.matches(recipe);
      int i = finder.countCrafts(recipe.value(), (RecipeMatcher.ItemCallback)null);
      if (bl) {
         Iterator var5 = this.inputSlots.iterator();

         while(var5.hasNext()) {
            Slot slot = (Slot)var5.next();
            ItemStack itemStack = slot.getStack();
            if (!itemStack.isEmpty() && Math.min(i, itemStack.getMaxCount()) < itemStack.getCount() + 1) {
               return;
            }
         }
      }

      int j = this.calculateCraftAmount(i, bl);
      List list = new ArrayList();
      Recipe var10001 = recipe.value();
      Objects.requireNonNull(list);
      if (finder.isCraftable(var10001, j, list::add)) {
         int k = clampToMaxCount(j, list);
         if (k != j) {
            list.clear();
            var10001 = recipe.value();
            Objects.requireNonNull(list);
            if (!finder.isCraftable(var10001, k, list::add)) {
               return;
            }
         }

         this.returnInputs();
         RecipeGridAligner.alignRecipeToGrid(this.width, this.height, recipe.value(), recipe.value().getIngredientPlacement().getPlacementSlots(), (slotx, index, x, y) -> {
            if (slotx != -1) {
               Slot slot2 = (Slot)this.inputSlots.get(index);
               RegistryEntry registryEntry = (RegistryEntry)list.get(slotx);
               int j = k;

               do {
                  if (j <= 0) {
                     return;
                  }

                  j = this.fillInputSlot(slot2, registryEntry, j);
               } while(j != -1);

            }
         });
      }
   }

   private static int clampToMaxCount(int count, List entries) {
      RegistryEntry registryEntry;
      for(Iterator var2 = entries.iterator(); var2.hasNext(); count = Math.min(count, ((Item)registryEntry.value()).getMaxCount())) {
         registryEntry = (RegistryEntry)var2.next();
      }

      return count;
   }

   private int calculateCraftAmount(int forCraftAll, boolean match) {
      if (this.craftAll) {
         return forCraftAll;
      } else if (match) {
         int i = Integer.MAX_VALUE;
         Iterator var4 = this.inputSlots.iterator();

         while(var4.hasNext()) {
            Slot slot = (Slot)var4.next();
            ItemStack itemStack = slot.getStack();
            if (!itemStack.isEmpty() && i > itemStack.getCount()) {
               i = itemStack.getCount();
            }
         }

         if (i != Integer.MAX_VALUE) {
            ++i;
         }

         return i;
      } else {
         return 1;
      }
   }

   private int fillInputSlot(Slot slot, RegistryEntry item, int count) {
      ItemStack itemStack = slot.getStack();
      int i = this.inventory.getMatchingSlot(item, itemStack);
      if (i == -1) {
         return -1;
      } else {
         ItemStack itemStack2 = this.inventory.getStack(i);
         ItemStack itemStack3;
         if (count < itemStack2.getCount()) {
            itemStack3 = this.inventory.removeStack(i, count);
         } else {
            itemStack3 = this.inventory.removeStack(i);
         }

         int j = itemStack3.getCount();
         if (itemStack.isEmpty()) {
            slot.setStackNoCallbacks(itemStack3);
         } else {
            itemStack.increment(j);
         }

         return count - j;
      }
   }

   private boolean canReturnInputs() {
      List list = Lists.newArrayList();
      int i = this.getFreeInventorySlots();
      Iterator var3 = this.inputSlots.iterator();

      while(true) {
         while(true) {
            ItemStack itemStack;
            do {
               if (!var3.hasNext()) {
                  return true;
               }

               Slot slot = (Slot)var3.next();
               itemStack = slot.getStack().copy();
            } while(itemStack.isEmpty());

            int j = this.inventory.getOccupiedSlotWithRoomForStack(itemStack);
            if (j == -1 && list.size() <= i) {
               Iterator var7 = list.iterator();

               while(var7.hasNext()) {
                  ItemStack itemStack2 = (ItemStack)var7.next();
                  if (ItemStack.areItemsEqual(itemStack2, itemStack) && itemStack2.getCount() != itemStack2.getMaxCount() && itemStack2.getCount() + itemStack.getCount() <= itemStack2.getMaxCount()) {
                     itemStack2.increment(itemStack.getCount());
                     itemStack.setCount(0);
                     break;
                  }
               }

               if (!itemStack.isEmpty()) {
                  if (list.size() >= i) {
                     return false;
                  }

                  list.add(itemStack);
               }
            } else if (j == -1) {
               return false;
            }
         }
      }
   }

   private int getFreeInventorySlots() {
      int i = 0;
      Iterator var2 = this.inventory.getMainStacks().iterator();

      while(var2.hasNext()) {
         ItemStack itemStack = (ItemStack)var2.next();
         if (itemStack.isEmpty()) {
            ++i;
         }
      }

      return i;
   }

   public interface Handler {
      void populateRecipeFinder(RecipeFinder finder);

      void clear();

      boolean matches(RecipeEntry entry);
   }
}
