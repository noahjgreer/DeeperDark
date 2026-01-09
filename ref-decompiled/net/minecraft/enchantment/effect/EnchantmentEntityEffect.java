package net.minecraft.enchantment.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.effect.entity.ApplyMobEffectEnchantmentEffect;
import net.minecraft.enchantment.effect.entity.ChangeItemDamageEnchantmentEffect;
import net.minecraft.enchantment.effect.entity.DamageEntityEnchantmentEffect;
import net.minecraft.enchantment.effect.entity.ExplodeEnchantmentEffect;
import net.minecraft.enchantment.effect.entity.IgniteEnchantmentEffect;
import net.minecraft.enchantment.effect.entity.PlaySoundEnchantmentEffect;
import net.minecraft.enchantment.effect.entity.ReplaceBlockEnchantmentEffect;
import net.minecraft.enchantment.effect.entity.ReplaceDiskEnchantmentEffect;
import net.minecraft.enchantment.effect.entity.RunFunctionEnchantmentEffect;
import net.minecraft.enchantment.effect.entity.SetBlockPropertiesEnchantmentEffect;
import net.minecraft.enchantment.effect.entity.SpawnParticlesEnchantmentEffect;
import net.minecraft.enchantment.effect.entity.SummonEntityEnchantmentEffect;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public interface EnchantmentEntityEffect extends EnchantmentLocationBasedEffect {
   Codec CODEC = Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE.getCodec().dispatch(EnchantmentEntityEffect::getCodec, Function.identity());

   static MapCodec registerAndGetDefault(Registry registry) {
      Registry.register(registry, (String)"all_of", AllOfEnchantmentEffects.EntityEffects.CODEC);
      Registry.register(registry, (String)"apply_mob_effect", ApplyMobEffectEnchantmentEffect.CODEC);
      Registry.register(registry, (String)"change_item_damage", ChangeItemDamageEnchantmentEffect.CODEC);
      Registry.register(registry, (String)"damage_entity", DamageEntityEnchantmentEffect.CODEC);
      Registry.register(registry, (String)"explode", ExplodeEnchantmentEffect.CODEC);
      Registry.register(registry, (String)"ignite", IgniteEnchantmentEffect.CODEC);
      Registry.register(registry, (String)"play_sound", PlaySoundEnchantmentEffect.CODEC);
      Registry.register(registry, (String)"replace_block", ReplaceBlockEnchantmentEffect.CODEC);
      Registry.register(registry, (String)"replace_disk", ReplaceDiskEnchantmentEffect.CODEC);
      Registry.register(registry, (String)"run_function", RunFunctionEnchantmentEffect.CODEC);
      Registry.register(registry, (String)"set_block_properties", SetBlockPropertiesEnchantmentEffect.CODEC);
      Registry.register(registry, (String)"spawn_particles", SpawnParticlesEnchantmentEffect.CODEC);
      return (MapCodec)Registry.register(registry, (String)"summon_entity", SummonEntityEnchantmentEffect.CODEC);
   }

   void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos);

   default void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos, boolean newlyApplied) {
      this.apply(world, level, context, user, pos);
   }

   MapCodec getCodec();
}
