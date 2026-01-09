package net.minecraft.enchantment.effect.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public record IgniteEnchantmentEffect(EnchantmentLevelBasedValue duration) implements EnchantmentEntityEffect {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(EnchantmentLevelBasedValue.CODEC.fieldOf("duration").forGetter((igniteEnchantmentEffect) -> {
         return igniteEnchantmentEffect.duration;
      })).apply(instance, IgniteEnchantmentEffect::new);
   });

   public IgniteEnchantmentEffect(EnchantmentLevelBasedValue enchantmentLevelBasedValue) {
      this.duration = enchantmentLevelBasedValue;
   }

   public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
      user.setOnFireFor(this.duration.getValue(level));
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public EnchantmentLevelBasedValue duration() {
      return this.duration;
   }
}
