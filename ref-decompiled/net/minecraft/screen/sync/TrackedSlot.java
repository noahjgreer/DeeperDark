package net.minecraft.screen.sync;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface TrackedSlot {
   TrackedSlot ALWAYS_IN_SYNC = new TrackedSlot() {
      public void setReceivedHash(ItemStackHash receivedHash) {
      }

      public void setReceivedStack(ItemStack receivedStack) {
      }

      public boolean isInSync(ItemStack actualStack) {
         return true;
      }
   };

   void setReceivedStack(ItemStack receivedStack);

   void setReceivedHash(ItemStackHash receivedHash);

   boolean isInSync(ItemStack actualStack);

   public static class Impl implements TrackedSlot {
      private final ComponentChangesHash.ComponentHasher hasher;
      @Nullable
      private ItemStack receivedStack = null;
      @Nullable
      private ItemStackHash receivedHash = null;

      public Impl(ComponentChangesHash.ComponentHasher hasher) {
         this.hasher = hasher;
      }

      public void setReceivedStack(ItemStack receivedStack) {
         this.receivedStack = receivedStack.copy();
         this.receivedHash = null;
      }

      public void setReceivedHash(ItemStackHash receivedHash) {
         this.receivedStack = null;
         this.receivedHash = receivedHash;
      }

      public boolean isInSync(ItemStack actualStack) {
         if (this.receivedStack != null) {
            return ItemStack.areEqual(this.receivedStack, actualStack);
         } else if (this.receivedHash != null && this.receivedHash.hashEquals(actualStack, this.hasher)) {
            this.receivedStack = actualStack.copy();
            return true;
         } else {
            return false;
         }
      }

      public void copyFrom(Impl slot) {
         this.receivedStack = slot.receivedStack;
         this.receivedHash = slot.receivedHash;
      }
   }
}
