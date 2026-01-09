package net.minecraft.loot.condition;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LootConditionTypes {
   public static final LootConditionType INVERTED;
   public static final LootConditionType ANY_OF;
   public static final LootConditionType ALL_OF;
   public static final LootConditionType RANDOM_CHANCE;
   public static final LootConditionType RANDOM_CHANCE_WITH_ENCHANTED_BONUS;
   public static final LootConditionType ENTITY_PROPERTIES;
   public static final LootConditionType KILLED_BY_PLAYER;
   public static final LootConditionType ENTITY_SCORES;
   public static final LootConditionType BLOCK_STATE_PROPERTY;
   public static final LootConditionType MATCH_TOOL;
   public static final LootConditionType TABLE_BONUS;
   public static final LootConditionType SURVIVES_EXPLOSION;
   public static final LootConditionType DAMAGE_SOURCE_PROPERTIES;
   public static final LootConditionType LOCATION_CHECK;
   public static final LootConditionType WEATHER_CHECK;
   public static final LootConditionType REFERENCE;
   public static final LootConditionType TIME_CHECK;
   public static final LootConditionType VALUE_CHECK;
   public static final LootConditionType ENCHANTMENT_ACTIVE_CHECK;

   private static LootConditionType register(String id, MapCodec codec) {
      return (LootConditionType)Registry.register(Registries.LOOT_CONDITION_TYPE, (Identifier)Identifier.ofVanilla(id), new LootConditionType(codec));
   }

   static {
      INVERTED = register("inverted", InvertedLootCondition.CODEC);
      ANY_OF = register("any_of", AnyOfLootCondition.CODEC);
      ALL_OF = register("all_of", AllOfLootCondition.CODEC);
      RANDOM_CHANCE = register("random_chance", RandomChanceLootCondition.CODEC);
      RANDOM_CHANCE_WITH_ENCHANTED_BONUS = register("random_chance_with_enchanted_bonus", RandomChanceWithEnchantedBonusLootCondition.CODEC);
      ENTITY_PROPERTIES = register("entity_properties", EntityPropertiesLootCondition.CODEC);
      KILLED_BY_PLAYER = register("killed_by_player", KilledByPlayerLootCondition.CODEC);
      ENTITY_SCORES = register("entity_scores", EntityScoresLootCondition.CODEC);
      BLOCK_STATE_PROPERTY = register("block_state_property", BlockStatePropertyLootCondition.CODEC);
      MATCH_TOOL = register("match_tool", MatchToolLootCondition.CODEC);
      TABLE_BONUS = register("table_bonus", TableBonusLootCondition.CODEC);
      SURVIVES_EXPLOSION = register("survives_explosion", SurvivesExplosionLootCondition.CODEC);
      DAMAGE_SOURCE_PROPERTIES = register("damage_source_properties", DamageSourcePropertiesLootCondition.CODEC);
      LOCATION_CHECK = register("location_check", LocationCheckLootCondition.CODEC);
      WEATHER_CHECK = register("weather_check", WeatherCheckLootCondition.CODEC);
      REFERENCE = register("reference", ReferenceLootCondition.CODEC);
      TIME_CHECK = register("time_check", TimeCheckLootCondition.CODEC);
      VALUE_CHECK = register("value_check", ValueCheckLootCondition.CODEC);
      ENCHANTMENT_ACTIVE_CHECK = register("enchantment_active_check", EnchantmentActiveCheckLootCondition.CODEC);
   }
}
