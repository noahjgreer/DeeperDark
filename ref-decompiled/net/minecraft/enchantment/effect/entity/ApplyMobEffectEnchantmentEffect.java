package net.minecraft.enchantment.effect.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public record ApplyMobEffectEnchantmentEffect(RegistryEntryList toApply, EnchantmentLevelBasedValue minDuration, EnchantmentLevelBasedValue maxDuration, EnchantmentLevelBasedValue minAmplifier, EnchantmentLevelBasedValue maxAmplifier) implements EnchantmentEntityEffect {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(RegistryCodecs.entryList(RegistryKeys.STATUS_EFFECT).fieldOf("to_apply").forGetter(ApplyMobEffectEnchantmentEffect::toApply), EnchantmentLevelBasedValue.CODEC.fieldOf("min_duration").forGetter(ApplyMobEffectEnchantmentEffect::minDuration), EnchantmentLevelBasedValue.CODEC.fieldOf("max_duration").forGetter(ApplyMobEffectEnchantmentEffect::maxDuration), EnchantmentLevelBasedValue.CODEC.fieldOf("min_amplifier").forGetter(ApplyMobEffectEnchantmentEffect::minAmplifier), EnchantmentLevelBasedValue.CODEC.fieldOf("max_amplifier").forGetter(ApplyMobEffectEnchantmentEffect::maxAmplifier)).apply(instance, ApplyMobEffectEnchantmentEffect::new);
   });

   public ApplyMobEffectEnchantmentEffect(RegistryEntryList registryEntryList, EnchantmentLevelBasedValue enchantmentLevelBasedValue, EnchantmentLevelBasedValue enchantmentLevelBasedValue2, EnchantmentLevelBasedValue enchantmentLevelBasedValue3, EnchantmentLevelBasedValue enchantmentLevelBasedValue4) {
      this.toApply = registryEntryList;
      this.minDuration = enchantmentLevelBasedValue;
      this.maxDuration = enchantmentLevelBasedValue2;
      this.minAmplifier = enchantmentLevelBasedValue3;
      this.maxAmplifier = enchantmentLevelBasedValue4;
   }

   public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
      if (user instanceof LivingEntity livingEntity) {
         Random random = livingEntity.getRandom();
         Optional optional = this.toApply.getRandom(random);
         if (optional.isPresent()) {
            int i = Math.round(MathHelper.nextBetween(random, this.minDuration.getValue(level), this.maxDuration.getValue(level)) * 20.0F);
            int j = Math.max(0, Math.round(MathHelper.nextBetween(random, this.minAmplifier.getValue(level), this.maxAmplifier.getValue(level))));
            livingEntity.addStatusEffect(new StatusEffectInstance((RegistryEntry)optional.get(), i, j));
         }
      }

   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public RegistryEntryList toApply() {
      return this.toApply;
   }

   public EnchantmentLevelBasedValue minDuration() {
      return this.minDuration;
   }

   public EnchantmentLevelBasedValue maxDuration() {
      return this.maxDuration;
   }

   public EnchantmentLevelBasedValue minAmplifier() {
      return this.minAmplifier;
   }

   public EnchantmentLevelBasedValue maxAmplifier() {
      return this.maxAmplifier;
   }
}
