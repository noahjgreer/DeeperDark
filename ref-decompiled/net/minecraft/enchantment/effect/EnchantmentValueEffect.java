package net.minecraft.enchantment.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.enchantment.effect.value.AddEnchantmentEffect;
import net.minecraft.enchantment.effect.value.MultiplyEnchantmentEffect;
import net.minecraft.enchantment.effect.value.RemoveBinomialEnchantmentEffect;
import net.minecraft.enchantment.effect.value.SetEnchantmentEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.random.Random;

public interface EnchantmentValueEffect {
   Codec CODEC = Registries.ENCHANTMENT_VALUE_EFFECT_TYPE.getCodec().dispatch(EnchantmentValueEffect::getCodec, Function.identity());

   static MapCodec registerAndGetDefault(Registry registry) {
      Registry.register(registry, (String)"add", AddEnchantmentEffect.CODEC);
      Registry.register(registry, (String)"all_of", AllOfEnchantmentEffects.ValueEffects.CODEC);
      Registry.register(registry, (String)"multiply", MultiplyEnchantmentEffect.CODEC);
      Registry.register(registry, (String)"remove_binomial", RemoveBinomialEnchantmentEffect.CODEC);
      return (MapCodec)Registry.register(registry, (String)"set", SetEnchantmentEffect.CODEC);
   }

   float apply(int level, Random random, float inputValue);

   MapCodec getCodec();
}
