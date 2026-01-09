package net.minecraft.loot.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;

public record RandomChanceWithEnchantedBonusLootCondition(float unenchantedChance, EnchantmentLevelBasedValue enchantedChance, RegistryEntry enchantment) implements LootCondition {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codec.floatRange(0.0F, 1.0F).fieldOf("unenchanted_chance").forGetter(RandomChanceWithEnchantedBonusLootCondition::unenchantedChance), EnchantmentLevelBasedValue.CODEC.fieldOf("enchanted_chance").forGetter(RandomChanceWithEnchantedBonusLootCondition::enchantedChance), Enchantment.ENTRY_CODEC.fieldOf("enchantment").forGetter(RandomChanceWithEnchantedBonusLootCondition::enchantment)).apply(instance, RandomChanceWithEnchantedBonusLootCondition::new);
   });

   public RandomChanceWithEnchantedBonusLootCondition(float f, EnchantmentLevelBasedValue enchantmentLevelBasedValue, RegistryEntry registryEntry) {
      this.unenchantedChance = f;
      this.enchantedChance = enchantmentLevelBasedValue;
      this.enchantment = registryEntry;
   }

   public LootConditionType getType() {
      return LootConditionTypes.RANDOM_CHANCE_WITH_ENCHANTED_BONUS;
   }

   public Set getAllowedParameters() {
      return Set.of(LootContextParameters.ATTACKING_ENTITY);
   }

   public boolean test(LootContext lootContext) {
      Entity entity = (Entity)lootContext.get(LootContextParameters.ATTACKING_ENTITY);
      int var10000;
      if (entity instanceof LivingEntity livingEntity) {
         var10000 = EnchantmentHelper.getEquipmentLevel(this.enchantment, livingEntity);
      } else {
         var10000 = 0;
      }

      int i = var10000;
      float f = i > 0 ? this.enchantedChance.getValue(i) : this.unenchantedChance;
      return lootContext.getRandom().nextFloat() < f;
   }

   public static LootCondition.Builder builder(RegistryWrapper.WrapperLookup registries, float base, float perLevelAboveFirst) {
      RegistryWrapper.Impl impl = registries.getOrThrow(RegistryKeys.ENCHANTMENT);
      return () -> {
         return new RandomChanceWithEnchantedBonusLootCondition(base, new EnchantmentLevelBasedValue.Linear(base + perLevelAboveFirst, perLevelAboveFirst), impl.getOrThrow(Enchantments.LOOTING));
      };
   }

   public float unenchantedChance() {
      return this.unenchantedChance;
   }

   public EnchantmentLevelBasedValue enchantedChance() {
      return this.enchantedChance;
   }

   public RegistryEntry enchantment() {
      return this.enchantment;
   }

   // $FF: synthetic method
   public boolean test(final Object context) {
      return this.test((LootContext)context);
   }
}
