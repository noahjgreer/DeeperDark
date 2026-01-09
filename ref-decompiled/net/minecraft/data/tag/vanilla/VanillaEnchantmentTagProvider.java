package net.minecraft.data.tag.vanilla;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.tag.EnchantmentTagProvider;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.EnchantmentTags;

public class VanillaEnchantmentTagProvider extends EnchantmentTagProvider {
   public VanillaEnchantmentTagProvider(DataOutput dataOutput, CompletableFuture completableFuture) {
      super(dataOutput, completableFuture);
   }

   protected void configure(RegistryWrapper.WrapperLookup registries) {
      this.createTooltipOrderTag(registries, new RegistryKey[]{Enchantments.BINDING_CURSE, Enchantments.VANISHING_CURSE, Enchantments.RIPTIDE, Enchantments.CHANNELING, Enchantments.WIND_BURST, Enchantments.FROST_WALKER, Enchantments.SHARPNESS, Enchantments.SMITE, Enchantments.BANE_OF_ARTHROPODS, Enchantments.IMPALING, Enchantments.POWER, Enchantments.DENSITY, Enchantments.BREACH, Enchantments.PIERCING, Enchantments.SWEEPING_EDGE, Enchantments.MULTISHOT, Enchantments.FIRE_ASPECT, Enchantments.FLAME, Enchantments.KNOCKBACK, Enchantments.PUNCH, Enchantments.PROTECTION, Enchantments.BLAST_PROTECTION, Enchantments.FIRE_PROTECTION, Enchantments.PROJECTILE_PROTECTION, Enchantments.FEATHER_FALLING, Enchantments.FORTUNE, Enchantments.LOOTING, Enchantments.SILK_TOUCH, Enchantments.LUCK_OF_THE_SEA, Enchantments.EFFICIENCY, Enchantments.QUICK_CHARGE, Enchantments.LURE, Enchantments.RESPIRATION, Enchantments.AQUA_AFFINITY, Enchantments.SOUL_SPEED, Enchantments.SWIFT_SNEAK, Enchantments.DEPTH_STRIDER, Enchantments.THORNS, Enchantments.LOYALTY, Enchantments.UNBREAKING, Enchantments.INFINITY, Enchantments.MENDING});
      this.builder(EnchantmentTags.ARMOR_EXCLUSIVE_SET).add((Object[])(Enchantments.PROTECTION, Enchantments.BLAST_PROTECTION, Enchantments.FIRE_PROTECTION, Enchantments.PROJECTILE_PROTECTION));
      this.builder(EnchantmentTags.BOOTS_EXCLUSIVE_SET).add((Object[])(Enchantments.FROST_WALKER, Enchantments.DEPTH_STRIDER));
      this.builder(EnchantmentTags.BOW_EXCLUSIVE_SET).add((Object[])(Enchantments.INFINITY, Enchantments.MENDING));
      this.builder(EnchantmentTags.CROSSBOW_EXCLUSIVE_SET).add((Object[])(Enchantments.MULTISHOT, Enchantments.PIERCING));
      this.builder(EnchantmentTags.DAMAGE_EXCLUSIVE_SET).add((Object[])(Enchantments.SHARPNESS, Enchantments.SMITE, Enchantments.BANE_OF_ARTHROPODS, Enchantments.IMPALING, Enchantments.DENSITY, Enchantments.BREACH));
      this.builder(EnchantmentTags.MINING_EXCLUSIVE_SET).add((Object[])(Enchantments.FORTUNE, Enchantments.SILK_TOUCH));
      this.builder(EnchantmentTags.RIPTIDE_EXCLUSIVE_SET).add((Object[])(Enchantments.LOYALTY, Enchantments.CHANNELING));
      this.builder(EnchantmentTags.TREASURE).add((Object[])(Enchantments.BINDING_CURSE, Enchantments.VANISHING_CURSE, Enchantments.SWIFT_SNEAK, Enchantments.SOUL_SPEED, Enchantments.FROST_WALKER, Enchantments.MENDING, Enchantments.WIND_BURST));
      this.builder(EnchantmentTags.NON_TREASURE).add((Object[])(Enchantments.PROTECTION, Enchantments.FIRE_PROTECTION, Enchantments.FEATHER_FALLING, Enchantments.BLAST_PROTECTION, Enchantments.PROJECTILE_PROTECTION, Enchantments.RESPIRATION, Enchantments.AQUA_AFFINITY, Enchantments.THORNS, Enchantments.DEPTH_STRIDER, Enchantments.SHARPNESS, Enchantments.SMITE, Enchantments.BANE_OF_ARTHROPODS, Enchantments.KNOCKBACK, Enchantments.FIRE_ASPECT, Enchantments.LOOTING, Enchantments.SWEEPING_EDGE, Enchantments.EFFICIENCY, Enchantments.SILK_TOUCH, Enchantments.UNBREAKING, Enchantments.FORTUNE, Enchantments.POWER, Enchantments.PUNCH, Enchantments.FLAME, Enchantments.INFINITY, Enchantments.LUCK_OF_THE_SEA, Enchantments.LURE, Enchantments.LOYALTY, Enchantments.IMPALING, Enchantments.RIPTIDE, Enchantments.CHANNELING, Enchantments.MULTISHOT, Enchantments.QUICK_CHARGE, Enchantments.PIERCING, Enchantments.DENSITY, Enchantments.BREACH));
      this.builder(EnchantmentTags.DOUBLE_TRADE_PRICE).addTag(EnchantmentTags.TREASURE);
      this.builder(EnchantmentTags.IN_ENCHANTING_TABLE).addTag(EnchantmentTags.NON_TREASURE);
      this.builder(EnchantmentTags.ON_MOB_SPAWN_EQUIPMENT).addTag(EnchantmentTags.NON_TREASURE);
      this.builder(EnchantmentTags.ON_TRADED_EQUIPMENT).addTag(EnchantmentTags.NON_TREASURE);
      this.builder(EnchantmentTags.ON_RANDOM_LOOT).addTag(EnchantmentTags.NON_TREASURE).add((Object[])(Enchantments.BINDING_CURSE, Enchantments.VANISHING_CURSE, Enchantments.FROST_WALKER, Enchantments.MENDING));
      this.builder(EnchantmentTags.TRADEABLE).addTag(EnchantmentTags.NON_TREASURE).add((Object[])(Enchantments.BINDING_CURSE, Enchantments.VANISHING_CURSE, Enchantments.FROST_WALKER, Enchantments.MENDING));
      this.builder(EnchantmentTags.CURSE).add((Object[])(Enchantments.BINDING_CURSE, Enchantments.VANISHING_CURSE));
      this.builder(EnchantmentTags.SMELTS_LOOT).add((Object)Enchantments.FIRE_ASPECT);
      this.builder(EnchantmentTags.PREVENTS_BEE_SPAWNS_WHEN_MINING).add((Object)Enchantments.SILK_TOUCH);
      this.builder(EnchantmentTags.PREVENTS_DECORATED_POT_SHATTERING).add((Object)Enchantments.SILK_TOUCH);
      this.builder(EnchantmentTags.PREVENTS_ICE_MELTING).add((Object)Enchantments.SILK_TOUCH);
      this.builder(EnchantmentTags.PREVENTS_INFESTED_SPAWNS).add((Object)Enchantments.SILK_TOUCH);
   }
}
