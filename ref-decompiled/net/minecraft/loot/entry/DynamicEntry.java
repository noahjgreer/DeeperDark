package net.minecraft.loot.entry;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Identifier;

public class DynamicEntry extends LeafEntry {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Identifier.CODEC.fieldOf("name").forGetter((entry) -> {
         return entry.name;
      })).and(addLeafFields(instance)).apply(instance, DynamicEntry::new);
   });
   private final Identifier name;

   private DynamicEntry(Identifier name, int weight, int quality, List conditions, List functions) {
      super(weight, quality, conditions, functions);
      this.name = name;
   }

   public LootPoolEntryType getType() {
      return LootPoolEntryTypes.DYNAMIC;
   }

   public void generateLoot(Consumer lootConsumer, LootContext context) {
      context.drop(this.name, lootConsumer);
   }

   public static LeafEntry.Builder builder(Identifier name) {
      return builder((weight, quality, conditions, functions) -> {
         return new DynamicEntry(name, weight, quality, conditions, functions);
      });
   }
}
