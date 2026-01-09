package net.minecraft.loot.provider.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;

public record EnchantmentLevelLootNumberProvider(EnchantmentLevelBasedValue amount) implements LootNumberProvider {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(EnchantmentLevelBasedValue.CODEC.fieldOf("amount").forGetter(EnchantmentLevelLootNumberProvider::amount)).apply(instance, EnchantmentLevelLootNumberProvider::new);
   });

   public EnchantmentLevelLootNumberProvider(EnchantmentLevelBasedValue enchantmentLevelBasedValue) {
      this.amount = enchantmentLevelBasedValue;
   }

   public float nextFloat(LootContext context) {
      int i = (Integer)context.getOrThrow(LootContextParameters.ENCHANTMENT_LEVEL);
      return this.amount.getValue(i);
   }

   public LootNumberProviderType getType() {
      return LootNumberProviderTypes.ENCHANTMENT_LEVEL;
   }

   public static EnchantmentLevelLootNumberProvider create(EnchantmentLevelBasedValue amount) {
      return new EnchantmentLevelLootNumberProvider(amount);
   }

   public EnchantmentLevelBasedValue amount() {
      return this.amount;
   }
}
