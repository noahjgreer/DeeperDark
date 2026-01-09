package net.minecraft.item;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

public class ItemStackSet {
   private static final Hash.Strategy HASH_STRATEGY = new Hash.Strategy() {
      public int hashCode(@Nullable ItemStack itemStack) {
         return ItemStack.hashCode(itemStack);
      }

      public boolean equals(@Nullable ItemStack itemStack, @Nullable ItemStack itemStack2) {
         return itemStack == itemStack2 || itemStack != null && itemStack2 != null && itemStack.isEmpty() == itemStack2.isEmpty() && ItemStack.areItemsAndComponentsEqual(itemStack, itemStack2);
      }

      // $FF: synthetic method
      public boolean equals(@Nullable final Object first, @Nullable final Object second) {
         return this.equals((ItemStack)first, (ItemStack)second);
      }

      // $FF: synthetic method
      public int hashCode(@Nullable final Object stack) {
         return this.hashCode((ItemStack)stack);
      }
   };

   public static Set create() {
      return new ObjectLinkedOpenCustomHashSet(HASH_STRATEGY);
   }
}
