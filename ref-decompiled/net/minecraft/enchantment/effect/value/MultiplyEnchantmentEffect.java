package net.minecraft.enchantment.effect.value;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.EnchantmentValueEffect;
import net.minecraft.util.math.random.Random;

public record MultiplyEnchantmentEffect(EnchantmentLevelBasedValue factor) implements EnchantmentValueEffect {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(EnchantmentLevelBasedValue.CODEC.fieldOf("factor").forGetter(MultiplyEnchantmentEffect::factor)).apply(instance, MultiplyEnchantmentEffect::new);
   });

   public MultiplyEnchantmentEffect(EnchantmentLevelBasedValue enchantmentLevelBasedValue) {
      this.factor = enchantmentLevelBasedValue;
   }

   public float apply(int level, Random random, float inputValue) {
      return inputValue * this.factor.getValue(level);
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public EnchantmentLevelBasedValue factor() {
      return this.factor;
   }
}
