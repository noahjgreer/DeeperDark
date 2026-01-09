package net.minecraft.loot.entry;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import java.util.List;

public class SequenceEntry extends CombinedEntry {
   public static final MapCodec CODEC = createCodec(SequenceEntry::new);

   SequenceEntry(List list, List list2) {
      super(list, list2);
   }

   public LootPoolEntryType getType() {
      return LootPoolEntryTypes.SEQUENCE;
   }

   protected EntryCombiner combine(List terms) {
      EntryCombiner var10000;
      switch (terms.size()) {
         case 0:
            var10000 = ALWAYS_TRUE;
            break;
         case 1:
            var10000 = (EntryCombiner)terms.get(0);
            break;
         case 2:
            var10000 = ((EntryCombiner)terms.get(0)).and((EntryCombiner)terms.get(1));
            break;
         default:
            var10000 = (context, lootChoiceExpander) -> {
               Iterator var3 = terms.iterator();

               EntryCombiner entryCombiner;
               do {
                  if (!var3.hasNext()) {
                     return true;
                  }

                  entryCombiner = (EntryCombiner)var3.next();
               } while(entryCombiner.expand(context, lootChoiceExpander));

               return false;
            };
      }

      return var10000;
   }

   public static Builder create(LootPoolEntry.Builder... entries) {
      return new Builder(entries);
   }

   public static class Builder extends LootPoolEntry.Builder {
      private final ImmutableList.Builder entries = ImmutableList.builder();

      public Builder(LootPoolEntry.Builder... entries) {
         LootPoolEntry.Builder[] var2 = entries;
         int var3 = entries.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            LootPoolEntry.Builder builder = var2[var4];
            this.entries.add(builder.build());
         }

      }

      protected Builder getThisBuilder() {
         return this;
      }

      public Builder sequenceEntry(LootPoolEntry.Builder entry) {
         this.entries.add(entry.build());
         return this;
      }

      public LootPoolEntry build() {
         return new SequenceEntry(this.entries.build(), this.getConditions());
      }

      // $FF: synthetic method
      protected LootPoolEntry.Builder getThisBuilder() {
         return this.getThisBuilder();
      }
   }
}
