package net.minecraft.loot.entry;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.ErrorReporter;

public class LootTableEntry extends LeafEntry {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codec.either(LootTable.TABLE_KEY, LootTable.CODEC).fieldOf("value").forGetter((entry) -> {
         return entry.value;
      })).and(addLeafFields(instance)).apply(instance, LootTableEntry::new);
   });
   public static final ErrorReporter.Context INLINE_CONTEXT = new ErrorReporter.Context() {
      public String getName() {
         return "->{inline}";
      }
   };
   private final Either value;

   private LootTableEntry(Either value, int weight, int quality, List conditions, List functions) {
      super(weight, quality, conditions, functions);
      this.value = value;
   }

   public LootPoolEntryType getType() {
      return LootPoolEntryTypes.LOOT_TABLE;
   }

   public void generateLoot(Consumer lootConsumer, LootContext context) {
      ((LootTable)this.value.map((key) -> {
         return (LootTable)context.getLookup().getOptionalEntry(key).map(RegistryEntry::value).orElse(LootTable.EMPTY);
      }, (table) -> {
         return table;
      })).generateUnprocessedLoot(context, lootConsumer);
   }

   public void validate(LootTableReporter reporter) {
      Optional optional = this.value.left();
      if (optional.isPresent()) {
         RegistryKey registryKey = (RegistryKey)optional.get();
         if (!reporter.canUseReferences()) {
            reporter.report(new LootTableReporter.ReferenceNotAllowedError(registryKey));
            return;
         }

         if (reporter.isInStack(registryKey)) {
            reporter.report(new LootTableReporter.RecursionError(registryKey));
            return;
         }
      }

      super.validate(reporter);
      this.value.ifLeft((key) -> {
         reporter.getDataLookup().getOptionalEntry(key).ifPresentOrElse((entry) -> {
            ((LootTable)entry.value()).validate(reporter.makeChild(new ErrorReporter.ReferenceLootTableContext(key), key));
         }, () -> {
            reporter.report(new LootTableReporter.MissingElementError(key));
         });
      }).ifRight((table) -> {
         table.validate(reporter.makeChild(INLINE_CONTEXT));
      });
   }

   public static LeafEntry.Builder builder(RegistryKey key) {
      return builder((LeafEntry.Factory)((weight, quality, conditions, functions) -> {
         return new LootTableEntry(Either.left(key), weight, quality, conditions, functions);
      }));
   }

   public static LeafEntry.Builder builder(LootTable table) {
      return builder((LeafEntry.Factory)((weight, quality, conditions, functions) -> {
         return new LootTableEntry(Either.right(table), weight, quality, conditions, functions);
      }));
   }
}
