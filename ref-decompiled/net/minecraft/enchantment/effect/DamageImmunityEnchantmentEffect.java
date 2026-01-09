package net.minecraft.enchantment.effect;

import com.mojang.serialization.Codec;

public record DamageImmunityEnchantmentEffect() {
   public static final DamageImmunityEnchantmentEffect INSTANCE = new DamageImmunityEnchantmentEffect();
   public static final Codec CODEC = Codec.unit(() -> {
      return INSTANCE;
   });
}
