package net.minecraft.loot.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.OminousBottleAmplifierComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.util.math.MathHelper;

public class SetOminousBottleAmplifierLootFunction extends ConditionalLootFunction {
   static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return addConditionsField(instance).and(LootNumberProviderTypes.CODEC.fieldOf("amplifier").forGetter((lootFunction) -> {
         return lootFunction.amplifier;
      })).apply(instance, SetOminousBottleAmplifierLootFunction::new);
   });
   private final LootNumberProvider amplifier;

   private SetOminousBottleAmplifierLootFunction(List conditions, LootNumberProvider amplifier) {
      super(conditions);
      this.amplifier = amplifier;
   }

   public Set getAllowedParameters() {
      return this.amplifier.getAllowedParameters();
   }

   public LootFunctionType getType() {
      return LootFunctionTypes.SET_OMINOUS_BOTTLE_AMPLIFIER;
   }

   public ItemStack process(ItemStack stack, LootContext context) {
      int i = MathHelper.clamp(this.amplifier.nextInt(context), 0, 4);
      stack.set(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER, new OminousBottleAmplifierComponent(i));
      return stack;
   }

   public LootNumberProvider getAmplifier() {
      return this.amplifier;
   }

   public static ConditionalLootFunction.Builder builder(LootNumberProvider amplifier) {
      return builder((conditions) -> {
         return new SetOminousBottleAmplifierLootFunction(conditions, amplifier);
      });
   }
}
