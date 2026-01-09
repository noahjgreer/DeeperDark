package net.minecraft.loot.entry;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.util.ErrorReporter;

public class AlternativeEntry extends CombinedEntry {
   public static final MapCodec CODEC = createCodec(AlternativeEntry::new);
   public static final ErrorReporter.Error UNREACHABLE_ENTRY_ERROR = new ErrorReporter.Error() {
      public String getMessage() {
         return "Unreachable entry!";
      }
   };

   AlternativeEntry(List list, List list2) {
      super(list, list2);
   }

   public LootPoolEntryType getType() {
      return LootPoolEntryTypes.ALTERNATIVES;
   }

   protected EntryCombiner combine(List terms) {
      EntryCombiner var10000;
      switch (terms.size()) {
         case 0:
            var10000 = ALWAYS_FALSE;
            break;
         case 1:
            var10000 = (EntryCombiner)terms.get(0);
            break;
         case 2:
            var10000 = ((EntryCombiner)terms.get(0)).or((EntryCombiner)terms.get(1));
            break;
         default:
            var10000 = (context, lootChoiceExpander) -> {
               Iterator var3 = terms.iterator();

               EntryCombiner entryCombiner;
               do {
                  if (!var3.hasNext()) {
                     return false;
                  }

                  entryCombiner = (EntryCombiner)var3.next();
               } while(!entryCombiner.expand(context, lootChoiceExpander));

               return true;
            };
      }

      return var10000;
   }

   public void validate(LootTableReporter reporter) {
      super.validate(reporter);

      for(int i = 0; i < this.children.size() - 1; ++i) {
         if (((LootPoolEntry)this.children.get(i)).conditions.isEmpty()) {
            reporter.report(UNREACHABLE_ENTRY_ERROR);
         }
      }

   }

   public static Builder builder(LootPoolEntry.Builder... children) {
      return new Builder(children);
   }

   public static Builder builder(Collection children, Function toBuilderFunction) {
      Stream var10002 = children.stream();
      Objects.requireNonNull(toBuilderFunction);
      return new Builder((LootPoolEntry.Builder[])var10002.map(toBuilderFunction::apply).toArray((i) -> {
         return new LootPoolEntry.Builder[i];
      }));
   }

   public static class Builder extends LootPoolEntry.Builder {
      private final ImmutableList.Builder children = ImmutableList.builder();

      public Builder(LootPoolEntry.Builder... children) {
         LootPoolEntry.Builder[] var2 = children;
         int var3 = children.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            LootPoolEntry.Builder builder = var2[var4];
            this.children.add(builder.build());
         }

      }

      protected Builder getThisBuilder() {
         return this;
      }

      public Builder alternatively(LootPoolEntry.Builder builder) {
         this.children.add(builder.build());
         return this;
      }

      public LootPoolEntry build() {
         return new AlternativeEntry(this.children.build(), this.getConditions());
      }

      // $FF: synthetic method
      protected LootPoolEntry.Builder getThisBuilder() {
         return this.getThisBuilder();
      }
   }
}
