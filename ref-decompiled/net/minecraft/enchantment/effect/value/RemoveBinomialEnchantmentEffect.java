package net.minecraft.enchantment.effect.value;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.EnchantmentValueEffect;
import net.minecraft.util.math.random.Random;

public record RemoveBinomialEnchantmentEffect(EnchantmentLevelBasedValue chance) implements EnchantmentValueEffect {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(EnchantmentLevelBasedValue.CODEC.fieldOf("chance").forGetter(RemoveBinomialEnchantmentEffect::chance)).apply(instance, RemoveBinomialEnchantmentEffect::new);
   });

   public RemoveBinomialEnchantmentEffect(EnchantmentLevelBasedValue enchantmentLevelBasedValue) {
      this.chance = enchantmentLevelBasedValue;
   }

   public float apply(int level, Random random, float inputValue) {
      float f = this.chance.getValue(level);
      int i = 0;

      for(int j = 0; (float)j < inputValue; ++j) {
         if (random.nextFloat() < f) {
            ++i;
         }
      }

      return inputValue - (float)i;
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public EnchantmentLevelBasedValue chance() {
      return this.chance;
   }
}
