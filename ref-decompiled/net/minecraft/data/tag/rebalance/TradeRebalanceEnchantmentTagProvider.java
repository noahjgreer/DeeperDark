package net.minecraft.data.tag.rebalance;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.tag.SimpleTagProvider;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.EnchantmentTags;

public class TradeRebalanceEnchantmentTagProvider extends SimpleTagProvider {
   public TradeRebalanceEnchantmentTagProvider(DataOutput output, CompletableFuture registriesFuture) {
      super(output, RegistryKeys.ENCHANTMENT, registriesFuture);
   }

   protected void configure(RegistryWrapper.WrapperLookup registries) {
      this.builder(EnchantmentTags.DESERT_COMMON_TRADE).add((Object[])(Enchantments.FIRE_PROTECTION, Enchantments.THORNS, Enchantments.INFINITY));
      this.builder(EnchantmentTags.JUNGLE_COMMON_TRADE).add((Object[])(Enchantments.FEATHER_FALLING, Enchantments.PROJECTILE_PROTECTION, Enchantments.POWER));
      this.builder(EnchantmentTags.PLAINS_COMMON_TRADE).add((Object[])(Enchantments.PUNCH, Enchantments.SMITE, Enchantments.BANE_OF_ARTHROPODS));
      this.builder(EnchantmentTags.SAVANNA_COMMON_TRADE).add((Object[])(Enchantments.KNOCKBACK, Enchantments.BINDING_CURSE, Enchantments.SWEEPING_EDGE));
      this.builder(EnchantmentTags.SNOW_COMMON_TRADE).add((Object[])(Enchantments.AQUA_AFFINITY, Enchantments.LOOTING, Enchantments.FROST_WALKER));
      this.builder(EnchantmentTags.SWAMP_COMMON_TRADE).add((Object[])(Enchantments.DEPTH_STRIDER, Enchantments.RESPIRATION, Enchantments.VANISHING_CURSE));
      this.builder(EnchantmentTags.TAIGA_COMMON_TRADE).add((Object[])(Enchantments.BLAST_PROTECTION, Enchantments.FIRE_ASPECT, Enchantments.FLAME));
      this.builder(EnchantmentTags.DESERT_SPECIAL_TRADE).add((Object)Enchantments.EFFICIENCY);
      this.builder(EnchantmentTags.JUNGLE_SPECIAL_TRADE).add((Object)Enchantments.UNBREAKING);
      this.builder(EnchantmentTags.PLAINS_SPECIAL_TRADE).add((Object)Enchantments.PROTECTION);
      this.builder(EnchantmentTags.SAVANNA_SPECIAL_TRADE).add((Object)Enchantments.SHARPNESS);
      this.builder(EnchantmentTags.SNOW_SPECIAL_TRADE).add((Object)Enchantments.SILK_TOUCH);
      this.builder(EnchantmentTags.SWAMP_SPECIAL_TRADE).add((Object)Enchantments.MENDING);
      this.builder(EnchantmentTags.TAIGA_SPECIAL_TRADE).add((Object)Enchantments.FORTUNE);
   }
}
