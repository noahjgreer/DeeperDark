package net.minecraft.component;

import com.mojang.serialization.Codec;
import java.util.function.UnaryOperator;
import net.minecraft.enchantment.effect.AttributeEnchantmentEffect;
import net.minecraft.enchantment.effect.DamageImmunityEnchantmentEffect;
import net.minecraft.enchantment.effect.EnchantmentEffectEntry;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.enchantment.effect.EnchantmentLocationBasedEffect;
import net.minecraft.enchantment.effect.EnchantmentValueEffect;
import net.minecraft.enchantment.effect.TargetedEnchantmentEffect;
import net.minecraft.item.CrossbowItem;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Unit;

public interface EnchantmentEffectComponentTypes {
   Codec COMPONENT_TYPE_CODEC = Codec.lazyInitialized(() -> {
      return Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE.getCodec();
   });
   Codec COMPONENT_MAP_CODEC = ComponentMap.createCodec(COMPONENT_TYPE_CODEC);
   ComponentType DAMAGE_PROTECTION = register("damage_protection", (builder) -> {
      return builder.codec(EnchantmentEffectEntry.createCodec(EnchantmentValueEffect.CODEC, LootContextTypes.ENCHANTED_DAMAGE).listOf());
   });
   ComponentType DAMAGE_IMMUNITY = register("damage_immunity", (builder) -> {
      return builder.codec(EnchantmentEffectEntry.createCodec(DamageImmunityEnchantmentEffect.CODEC, LootContextTypes.ENCHANTED_DAMAGE).listOf());
   });
   ComponentType DAMAGE = register("damage", (builder) -> {
      return builder.codec(EnchantmentEffectEntry.createCodec(EnchantmentValueEffect.CODEC, LootContextTypes.ENCHANTED_DAMAGE).listOf());
   });
   ComponentType SMASH_DAMAGE_PER_FALLEN_BLOCK = register("smash_damage_per_fallen_block", (builder) -> {
      return builder.codec(EnchantmentEffectEntry.createCodec(EnchantmentValueEffect.CODEC, LootContextTypes.ENCHANTED_DAMAGE).listOf());
   });
   ComponentType KNOCKBACK = register("knockback", (builder) -> {
      return builder.codec(EnchantmentEffectEntry.createCodec(EnchantmentValueEffect.CODEC, LootContextTypes.ENCHANTED_DAMAGE).listOf());
   });
   ComponentType ARMOR_EFFECTIVENESS = register("armor_effectiveness", (builder) -> {
      return builder.codec(EnchantmentEffectEntry.createCodec(EnchantmentValueEffect.CODEC, LootContextTypes.ENCHANTED_DAMAGE).listOf());
   });
   ComponentType POST_ATTACK = register("post_attack", (builder) -> {
      return builder.codec(TargetedEnchantmentEffect.createPostAttackCodec(EnchantmentEntityEffect.CODEC, LootContextTypes.ENCHANTED_DAMAGE).listOf());
   });
   ComponentType HIT_BLOCK = register("hit_block", (builder) -> {
      return builder.codec(EnchantmentEffectEntry.createCodec(EnchantmentEntityEffect.CODEC, LootContextTypes.HIT_BLOCK).listOf());
   });
   ComponentType ITEM_DAMAGE = register("item_damage", (builder) -> {
      return builder.codec(EnchantmentEffectEntry.createCodec(EnchantmentValueEffect.CODEC, LootContextTypes.ENCHANTED_ITEM).listOf());
   });
   ComponentType ATTRIBUTES = register("attributes", (builder) -> {
      return builder.codec(AttributeEnchantmentEffect.CODEC.codec().listOf());
   });
   ComponentType EQUIPMENT_DROPS = register("equipment_drops", (builder) -> {
      return builder.codec(TargetedEnchantmentEffect.createEquipmentDropsCodec(EnchantmentValueEffect.CODEC, LootContextTypes.ENCHANTED_DAMAGE).listOf());
   });
   ComponentType LOCATION_CHANGED = register("location_changed", (builder) -> {
      return builder.codec(EnchantmentEffectEntry.createCodec(EnchantmentLocationBasedEffect.CODEC, LootContextTypes.ENCHANTED_LOCATION).listOf());
   });
   ComponentType TICK = register("tick", (builder) -> {
      return builder.codec(EnchantmentEffectEntry.createCodec(EnchantmentEntityEffect.CODEC, LootContextTypes.ENCHANTED_ENTITY).listOf());
   });
   ComponentType AMMO_USE = register("ammo_use", (builder) -> {
      return builder.codec(EnchantmentEffectEntry.createCodec(EnchantmentValueEffect.CODEC, LootContextTypes.ENCHANTED_ITEM).listOf());
   });
   ComponentType PROJECTILE_PIERCING = register("projectile_piercing", (builder) -> {
      return builder.codec(EnchantmentEffectEntry.createCodec(EnchantmentValueEffect.CODEC, LootContextTypes.ENCHANTED_ITEM).listOf());
   });
   ComponentType PROJECTILE_SPAWNED = register("projectile_spawned", (builder) -> {
      return builder.codec(EnchantmentEffectEntry.createCodec(EnchantmentEntityEffect.CODEC, LootContextTypes.ENCHANTED_ENTITY).listOf());
   });
   ComponentType PROJECTILE_SPREAD = register("projectile_spread", (builder) -> {
      return builder.codec(EnchantmentEffectEntry.createCodec(EnchantmentValueEffect.CODEC, LootContextTypes.ENCHANTED_ENTITY).listOf());
   });
   ComponentType PROJECTILE_COUNT = register("projectile_count", (builder) -> {
      return builder.codec(EnchantmentEffectEntry.createCodec(EnchantmentValueEffect.CODEC, LootContextTypes.ENCHANTED_ENTITY).listOf());
   });
   ComponentType TRIDENT_RETURN_ACCELERATION = register("trident_return_acceleration", (builder) -> {
      return builder.codec(EnchantmentEffectEntry.createCodec(EnchantmentValueEffect.CODEC, LootContextTypes.ENCHANTED_ENTITY).listOf());
   });
   ComponentType FISHING_TIME_REDUCTION = register("fishing_time_reduction", (builder) -> {
      return builder.codec(EnchantmentEffectEntry.createCodec(EnchantmentValueEffect.CODEC, LootContextTypes.ENCHANTED_ENTITY).listOf());
   });
   ComponentType FISHING_LUCK_BONUS = register("fishing_luck_bonus", (builder) -> {
      return builder.codec(EnchantmentEffectEntry.createCodec(EnchantmentValueEffect.CODEC, LootContextTypes.ENCHANTED_ENTITY).listOf());
   });
   ComponentType BLOCK_EXPERIENCE = register("block_experience", (builder) -> {
      return builder.codec(EnchantmentEffectEntry.createCodec(EnchantmentValueEffect.CODEC, LootContextTypes.ENCHANTED_ITEM).listOf());
   });
   ComponentType MOB_EXPERIENCE = register("mob_experience", (builder) -> {
      return builder.codec(EnchantmentEffectEntry.createCodec(EnchantmentValueEffect.CODEC, LootContextTypes.ENCHANTED_ENTITY).listOf());
   });
   ComponentType REPAIR_WITH_XP = register("repair_with_xp", (builder) -> {
      return builder.codec(EnchantmentEffectEntry.createCodec(EnchantmentValueEffect.CODEC, LootContextTypes.ENCHANTED_ITEM).listOf());
   });
   ComponentType CROSSBOW_CHARGE_TIME = register("crossbow_charge_time", (builder) -> {
      return builder.codec(EnchantmentValueEffect.CODEC);
   });
   ComponentType CROSSBOW_CHARGING_SOUNDS = register("crossbow_charging_sounds", (builder) -> {
      return builder.codec(CrossbowItem.LoadingSounds.CODEC.listOf());
   });
   ComponentType TRIDENT_SOUND = register("trident_sound", (builder) -> {
      return builder.codec(SoundEvent.ENTRY_CODEC.listOf());
   });
   ComponentType PREVENT_EQUIPMENT_DROP = register("prevent_equipment_drop", (builder) -> {
      return builder.codec(Unit.CODEC);
   });
   ComponentType PREVENT_ARMOR_CHANGE = register("prevent_armor_change", (builder) -> {
      return builder.codec(Unit.CODEC);
   });
   ComponentType TRIDENT_SPIN_ATTACK_STRENGTH = register("trident_spin_attack_strength", (builder) -> {
      return builder.codec(EnchantmentValueEffect.CODEC);
   });

   static ComponentType getDefault(Registry registry) {
      return DAMAGE_PROTECTION;
   }

   private static ComponentType register(String id, UnaryOperator builderOperator) {
      return (ComponentType)Registry.register(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, (String)id, ((ComponentType.Builder)builderOperator.apply(ComponentType.builder())).build());
   }
}
