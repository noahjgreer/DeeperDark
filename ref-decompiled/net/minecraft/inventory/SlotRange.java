package net.minecraft.inventory;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.util.StringIdentifiable;

public interface SlotRange extends StringIdentifiable {
   IntList getSlotIds();

   default int getSlotCount() {
      return this.getSlotIds().size();
   }

   static SlotRange create(final String name, final IntList slotIds) {
      return new SlotRange() {
         public IntList getSlotIds() {
            return slotIds;
         }

         public String asString() {
            return name;
         }

         public String toString() {
            return name;
         }
      };
   }
}
