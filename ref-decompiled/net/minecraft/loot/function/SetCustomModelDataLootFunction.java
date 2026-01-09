package net.minecraft.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.util.collection.ListOperation;
import net.minecraft.util.dynamic.Codecs;

public class SetCustomModelDataLootFunction extends ConditionalLootFunction {
   private static final Codec COLOR_CODEC;
   public static final MapCodec CODEC;
   private final Optional floats;
   private final Optional flags;
   private final Optional strings;
   private final Optional colors;

   public SetCustomModelDataLootFunction(List conditions, Optional floats, Optional flags, Optional strings, Optional colors) {
      super(conditions);
      this.floats = floats;
      this.flags = flags;
      this.strings = strings;
      this.colors = colors;
   }

   public Set getAllowedParameters() {
      return (Set)Stream.concat(this.floats.stream(), this.colors.stream()).flatMap((operation) -> {
         return operation.value().stream();
      }).flatMap((value) -> {
         return value.getAllowedParameters().stream();
      }).collect(Collectors.toSet());
   }

   public LootFunctionType getType() {
      return LootFunctionTypes.SET_CUSTOM_MODEL_DATA;
   }

   private static List apply(Optional values, List current) {
      return (List)values.map((operation) -> {
         return operation.apply(current);
      }).orElse(current);
   }

   private static List apply(Optional values, List current, Function operationValueToAppliedValue) {
      return (List)values.map((operation) -> {
         List list2 = operation.value().stream().map(operationValueToAppliedValue).toList();
         return operation.operation().apply(current, list2);
      }).orElse(current);
   }

   public ItemStack process(ItemStack stack, LootContext context) {
      CustomModelDataComponent customModelDataComponent = (CustomModelDataComponent)stack.getOrDefault(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelDataComponent.DEFAULT);
      stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(apply(this.floats, customModelDataComponent.floats(), (provider) -> {
         return provider.nextFloat(context);
      }), apply(this.flags, customModelDataComponent.flags()), apply(this.strings, customModelDataComponent.strings()), apply(this.colors, customModelDataComponent.colors(), (provider) -> {
         return provider.nextInt(context);
      })));
      return stack;
   }

   static {
      COLOR_CODEC = Codec.withAlternative(LootNumberProviderTypes.CODEC, Codecs.RGB, ConstantLootNumberProvider::new);
      CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return addConditionsField(instance).and(instance.group(ListOperation.Values.createCodec(LootNumberProviderTypes.CODEC, Integer.MAX_VALUE).optionalFieldOf("floats").forGetter((lootFunction) -> {
            return lootFunction.floats;
         }), ListOperation.Values.createCodec(Codec.BOOL, Integer.MAX_VALUE).optionalFieldOf("flags").forGetter((lootFunction) -> {
            return lootFunction.flags;
         }), ListOperation.Values.createCodec(Codec.STRING, Integer.MAX_VALUE).optionalFieldOf("strings").forGetter((lootFunction) -> {
            return lootFunction.strings;
         }), ListOperation.Values.createCodec(COLOR_CODEC, Integer.MAX_VALUE).optionalFieldOf("colors").forGetter((lootFunction) -> {
            return lootFunction.colors;
         }))).apply(instance, SetCustomModelDataLootFunction::new);
      });
   }
}
