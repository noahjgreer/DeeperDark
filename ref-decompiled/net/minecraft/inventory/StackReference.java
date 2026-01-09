package net.minecraft.inventory;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public interface StackReference {
   StackReference EMPTY = new StackReference() {
      public ItemStack get() {
         return ItemStack.EMPTY;
      }

      public boolean set(ItemStack stack) {
         return false;
      }
   };

   static StackReference of(final Supplier getter, final Consumer setter) {
      return new StackReference() {
         public ItemStack get() {
            return (ItemStack)getter.get();
         }

         public boolean set(ItemStack stack) {
            setter.accept(stack);
            return true;
         }
      };
   }

   static StackReference of(final Inventory inventory, final int index, final Predicate stackFilter) {
      return new StackReference() {
         public ItemStack get() {
            return inventory.getStack(index);
         }

         public boolean set(ItemStack stack) {
            if (!stackFilter.test(stack)) {
               return false;
            } else {
               inventory.setStack(index, stack);
               return true;
            }
         }
      };
   }

   static StackReference of(Inventory inventory, int index) {
      return of(inventory, index, (stack) -> {
         return true;
      });
   }

   static StackReference of(final LivingEntity entity, final EquipmentSlot slot, final Predicate filter) {
      return new StackReference() {
         public ItemStack get() {
            return entity.getEquippedStack(slot);
         }

         public boolean set(ItemStack stack) {
            if (!filter.test(stack)) {
               return false;
            } else {
               entity.equipStack(slot, stack);
               return true;
            }
         }
      };
   }

   static StackReference of(LivingEntity entity, EquipmentSlot slot) {
      return of(entity, slot, (stack) -> {
         return true;
      });
   }

   ItemStack get();

   boolean set(ItemStack stack);
}
