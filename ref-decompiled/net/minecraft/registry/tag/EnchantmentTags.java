package net.minecraft.registry.tag;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public interface EnchantmentTags {
   TagKey TOOLTIP_ORDER = of("tooltip_order");
   TagKey ARMOR_EXCLUSIVE_SET = of("exclusive_set/armor");
   TagKey BOOTS_EXCLUSIVE_SET = of("exclusive_set/boots");
   TagKey BOW_EXCLUSIVE_SET = of("exclusive_set/bow");
   TagKey CROSSBOW_EXCLUSIVE_SET = of("exclusive_set/crossbow");
   TagKey DAMAGE_EXCLUSIVE_SET = of("exclusive_set/damage");
   TagKey MINING_EXCLUSIVE_SET = of("exclusive_set/mining");
   TagKey RIPTIDE_EXCLUSIVE_SET = of("exclusive_set/riptide");
   TagKey TRADEABLE = of("tradeable");
   TagKey DOUBLE_TRADE_PRICE = of("double_trade_price");
   TagKey IN_ENCHANTING_TABLE = of("in_enchanting_table");
   TagKey ON_MOB_SPAWN_EQUIPMENT = of("on_mob_spawn_equipment");
   TagKey ON_TRADED_EQUIPMENT = of("on_traded_equipment");
   TagKey ON_RANDOM_LOOT = of("on_random_loot");
   TagKey CURSE = of("curse");
   TagKey SMELTS_LOOT = of("smelts_loot");
   TagKey PREVENTS_BEE_SPAWNS_WHEN_MINING = of("prevents_bee_spawns_when_mining");
   TagKey PREVENTS_DECORATED_POT_SHATTERING = of("prevents_decorated_pot_shattering");
   TagKey PREVENTS_ICE_MELTING = of("prevents_ice_melting");
   TagKey PREVENTS_INFESTED_SPAWNS = of("prevents_infested_spawns");
   TagKey TREASURE = of("treasure");
   TagKey NON_TREASURE = of("non_treasure");
   TagKey DESERT_COMMON_TRADE = of("trades/desert_common");
   TagKey JUNGLE_COMMON_TRADE = of("trades/jungle_common");
   TagKey PLAINS_COMMON_TRADE = of("trades/plains_common");
   TagKey SAVANNA_COMMON_TRADE = of("trades/savanna_common");
   TagKey SNOW_COMMON_TRADE = of("trades/snow_common");
   TagKey SWAMP_COMMON_TRADE = of("trades/swamp_common");
   TagKey TAIGA_COMMON_TRADE = of("trades/taiga_common");
   TagKey DESERT_SPECIAL_TRADE = of("trades/desert_special");
   TagKey JUNGLE_SPECIAL_TRADE = of("trades/jungle_special");
   TagKey PLAINS_SPECIAL_TRADE = of("trades/plains_special");
   TagKey SAVANNA_SPECIAL_TRADE = of("trades/savanna_special");
   TagKey SNOW_SPECIAL_TRADE = of("trades/snow_special");
   TagKey SWAMP_SPECIAL_TRADE = of("trades/swamp_special");
   TagKey TAIGA_SPECIAL_TRADE = of("trades/taiga_special");

   private static TagKey of(String id) {
      return TagKey.of(RegistryKeys.ENCHANTMENT, Identifier.ofVanilla(id));
   }
}
