package net.minecraft.predicate.entity;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.SlotRange;
import net.minecraft.inventory.SlotRanges;
import net.minecraft.inventory.StackReference;
import net.minecraft.predicate.item.ItemPredicate;

public record SlotsPredicate(Map slots) {
   public static final Codec CODEC;

   public SlotsPredicate(Map map) {
      this.slots = map;
   }

   public boolean matches(Entity entity) {
      Iterator var2 = this.slots.entrySet().iterator();

      Map.Entry entry;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         entry = (Map.Entry)var2.next();
      } while(matches(entity, (ItemPredicate)entry.getValue(), ((SlotRange)entry.getKey()).getSlotIds()));

      return false;
   }

   private static boolean matches(Entity entity, ItemPredicate itemPredicate, IntList slotIds) {
      for(int i = 0; i < slotIds.size(); ++i) {
         int j = slotIds.getInt(i);
         StackReference stackReference = entity.getStackReference(j);
         if (itemPredicate.test(stackReference.get())) {
            return true;
         }
      }

      return false;
   }

   public Map slots() {
      return this.slots;
   }

   static {
      CODEC = Codec.unboundedMap(SlotRanges.CODEC, ItemPredicate.CODEC).xmap(SlotsPredicate::new, SlotsPredicate::slots);
   }
}
