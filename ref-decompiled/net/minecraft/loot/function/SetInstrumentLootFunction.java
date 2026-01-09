package net.minecraft.loot.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.InstrumentComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;

public class SetInstrumentLootFunction extends ConditionalLootFunction {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return addConditionsField(instance).and(TagKey.codec(RegistryKeys.INSTRUMENT).fieldOf("options").forGetter((function) -> {
         return function.options;
      })).apply(instance, SetInstrumentLootFunction::new);
   });
   private final TagKey options;

   private SetInstrumentLootFunction(List conditions, TagKey options) {
      super(conditions);
      this.options = options;
   }

   public LootFunctionType getType() {
      return LootFunctionTypes.SET_INSTRUMENT;
   }

   public ItemStack process(ItemStack stack, LootContext context) {
      Registry registry = context.getWorld().getRegistryManager().getOrThrow(RegistryKeys.INSTRUMENT);
      Optional optional = registry.getRandomEntry(this.options, context.getRandom());
      if (optional.isPresent()) {
         stack.set(DataComponentTypes.INSTRUMENT, new InstrumentComponent((RegistryEntry)optional.get()));
      }

      return stack;
   }

   public static ConditionalLootFunction.Builder builder(TagKey options) {
      return builder((conditions) -> {
         return new SetInstrumentLootFunction(conditions, options);
      });
   }
}
