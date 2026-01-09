package net.minecraft.loot.entry;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.loot.context.LootContext;

public class EmptyEntry extends LeafEntry {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return addLeafFields(instance).apply(instance, EmptyEntry::new);
   });

   private EmptyEntry(int weight, int quality, List conditions, List functions) {
      super(weight, quality, conditions, functions);
   }

   public LootPoolEntryType getType() {
      return LootPoolEntryTypes.EMPTY;
   }

   public void generateLoot(Consumer lootConsumer, LootContext context) {
   }

   public static LeafEntry.Builder builder() {
      return builder(EmptyEntry::new);
   }
}
