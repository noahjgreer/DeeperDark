package net.minecraft.registry;

import net.minecraft.util.Identifier;

public class RegistryKeys {
   public static final Identifier ROOT = Identifier.ofVanilla("root");
   public static final RegistryKey ACTIVITY = of("activity");
   public static final RegistryKey ATTRIBUTE = of("attribute");
   public static final RegistryKey BIOME_SOURCE = of("worldgen/biome_source");
   public static final RegistryKey BLOCK_ENTITY_TYPE = of("block_entity_type");
   public static final RegistryKey BLOCK_PREDICATE_TYPE = of("block_predicate_type");
   public static final RegistryKey BLOCK_STATE_PROVIDER_TYPE = of("worldgen/block_state_provider_type");
   public static final RegistryKey BLOCK_TYPE = of("block_type");
   public static final RegistryKey BLOCK = of("block");
   public static final RegistryKey CARVER = of("worldgen/carver");
   public static final RegistryKey CHUNK_GENERATOR = of("worldgen/chunk_generator");
   public static final RegistryKey CHUNK_STATUS = of("chunk_status");
   public static final RegistryKey COMMAND_ARGUMENT_TYPE = of("command_argument_type");
   public static final RegistryKey CONSUME_EFFECT_TYPE = of("consume_effect_type");
   public static final RegistryKey ITEM_GROUP = of("creative_mode_tab");
   public static final RegistryKey CUSTOM_STAT = of("custom_stat");
   public static final RegistryKey DATA_COMPONENT_PREDICATE_TYPE = of("data_component_predicate_type");
   public static final RegistryKey DATA_COMPONENT_TYPE = of("data_component_type");
   public static final RegistryKey DECORATED_POT_PATTERN = of("decorated_pot_pattern");
   public static final RegistryKey DENSITY_FUNCTION_TYPE = of("worldgen/density_function_type");
   public static final RegistryKey DIALOG_BODY_TYPE = of("dialog_body_type");
   public static final RegistryKey DIALOG_TYPE = of("dialog_type");
   public static final RegistryKey ENCHANTMENT_EFFECT_COMPONENT_TYPE = of("enchantment_effect_component_type");
   public static final RegistryKey ENCHANTMENT_ENTITY_EFFECT_TYPE = of("enchantment_entity_effect_type");
   public static final RegistryKey ENCHANTMENT_LEVEL_BASED_VALUE_TYPE = of("enchantment_level_based_value_type");
   public static final RegistryKey ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE = of("enchantment_location_based_effect_type");
   public static final RegistryKey ENCHANTMENT_PROVIDER_TYPE = of("enchantment_provider_type");
   public static final RegistryKey ENCHANTMENT_VALUE_EFFECT_TYPE = of("enchantment_value_effect_type");
   public static final RegistryKey ENTITY_SUB_PREDICATE_TYPE = of("entity_sub_predicate_type");
   public static final RegistryKey ENTITY_TYPE = of("entity_type");
   public static final RegistryKey FEATURE_SIZE_TYPE = of("worldgen/feature_size_type");
   public static final RegistryKey FEATURE = of("worldgen/feature");
   public static final RegistryKey FLOAT_PROVIDER_TYPE = of("float_provider_type");
   public static final RegistryKey FLUID = of("fluid");
   public static final RegistryKey FOLIAGE_PLACER_TYPE = of("worldgen/foliage_placer_type");
   public static final RegistryKey GAME_EVENT = of("game_event");
   public static final RegistryKey HEIGHT_PROVIDER_TYPE = of("height_provider_type");
   public static final RegistryKey INPUT_CONTROL_TYPE = of("input_control_type");
   public static final RegistryKey INT_PROVIDER_TYPE = of("int_provider_type");
   public static final RegistryKey ITEM = of("item");
   public static final RegistryKey LOOT_CONDITION_TYPE = of("loot_condition_type");
   public static final RegistryKey LOOT_FUNCTION_TYPE = of("loot_function_type");
   public static final RegistryKey LOOT_NBT_PROVIDER_TYPE = of("loot_nbt_provider_type");
   public static final RegistryKey LOOT_NUMBER_PROVIDER_TYPE = of("loot_number_provider_type");
   public static final RegistryKey LOOT_POOL_ENTRY_TYPE = of("loot_pool_entry_type");
   public static final RegistryKey LOOT_SCORE_PROVIDER_TYPE = of("loot_score_provider_type");
   public static final RegistryKey MAP_DECORATION_TYPE = of("map_decoration_type");
   public static final RegistryKey MATERIAL_CONDITION = of("worldgen/material_condition");
   public static final RegistryKey MATERIAL_RULE = of("worldgen/material_rule");
   public static final RegistryKey MEMORY_MODULE_TYPE = of("memory_module_type");
   public static final RegistryKey SCREEN_HANDLER = of("menu");
   public static final RegistryKey STATUS_EFFECT = of("mob_effect");
   public static final RegistryKey NUMBER_FORMAT_TYPE = of("number_format_type");
   public static final RegistryKey PARTICLE_TYPE = of("particle_type");
   public static final RegistryKey PLACEMENT_MODIFIER_TYPE = of("worldgen/placement_modifier_type");
   public static final RegistryKey POINT_OF_INTEREST_TYPE = of("point_of_interest_type");
   public static final RegistryKey POOL_ALIAS_BINDING = of("worldgen/pool_alias_binding");
   public static final RegistryKey POSITION_SOURCE_TYPE = of("position_source_type");
   public static final RegistryKey POS_RULE_TEST = of("pos_rule_test");
   public static final RegistryKey POTION = of("potion");
   public static final RegistryKey RECIPE_BOOK_CATEGORY = of("recipe_book_category");
   public static final RegistryKey RECIPE_DISPLAY = of("recipe_display");
   public static final RegistryKey RECIPE_SERIALIZER = of("recipe_serializer");
   public static final RegistryKey RECIPE_TYPE = of("recipe_type");
   public static final RegistryKey ROOT_PLACER_TYPE = of("worldgen/root_placer_type");
   public static final RegistryKey RULE_BLOCK_ENTITY_MODIFIER = of("rule_block_entity_modifier");
   public static final RegistryKey RULE_TEST = of("rule_test");
   public static final RegistryKey SCHEDULE = of("schedule");
   public static final RegistryKey SENSOR_TYPE = of("sensor_type");
   public static final RegistryKey SLOT_DISPLAY = of("slot_display");
   public static final RegistryKey SOUND_EVENT = of("sound_event");
   public static final RegistryKey SPAWN_CONDITION_TYPE = of("spawn_condition_type");
   public static final RegistryKey STAT_TYPE = of("stat_type");
   public static final RegistryKey STRUCTURE_PIECE = of("worldgen/structure_piece");
   public static final RegistryKey STRUCTURE_PLACEMENT = of("worldgen/structure_placement");
   public static final RegistryKey STRUCTURE_POOL_ELEMENT = of("worldgen/structure_pool_element");
   public static final RegistryKey STRUCTURE_PROCESSOR = of("worldgen/structure_processor");
   public static final RegistryKey STRUCTURE_TYPE = of("worldgen/structure_type");
   public static final RegistryKey DIALOG_ACTION_TYPE = of("dialog_action_type");
   public static final RegistryKey TEST_ENVIRONMENT_DEFINITION_TYPE = of("test_environment_definition_type");
   public static final RegistryKey TEST_FUNCTION = of("test_function");
   public static final RegistryKey TEST_INSTANCE_TYPE = of("test_instance_type");
   public static final RegistryKey TICKET_TYPE = of("ticket_type");
   public static final RegistryKey TREE_DECORATOR_TYPE = of("worldgen/tree_decorator_type");
   public static final RegistryKey TRUNK_PLACER_TYPE = of("worldgen/trunk_placer_type");
   public static final RegistryKey VILLAGER_PROFESSION = of("villager_profession");
   public static final RegistryKey VILLAGER_TYPE = of("villager_type");
   public static final RegistryKey BANNER_PATTERN = of("banner_pattern");
   public static final RegistryKey BIOME = of("worldgen/biome");
   public static final RegistryKey CAT_VARIANT = of("cat_variant");
   public static final RegistryKey MESSAGE_TYPE = of("chat_type");
   public static final RegistryKey CHICKEN_VARIANT = of("chicken_variant");
   public static final RegistryKey CONFIGURED_CARVER = of("worldgen/configured_carver");
   public static final RegistryKey CONFIGURED_FEATURE = of("worldgen/configured_feature");
   public static final RegistryKey COW_VARIANT = of("cow_variant");
   public static final RegistryKey DAMAGE_TYPE = of("damage_type");
   public static final RegistryKey DENSITY_FUNCTION = of("worldgen/density_function");
   public static final RegistryKey DIALOG = of("dialog");
   public static final RegistryKey DIMENSION_TYPE = of("dimension_type");
   public static final RegistryKey ENCHANTMENT_PROVIDER = of("enchantment_provider");
   public static final RegistryKey ENCHANTMENT = of("enchantment");
   public static final RegistryKey FLAT_LEVEL_GENERATOR_PRESET = of("worldgen/flat_level_generator_preset");
   public static final RegistryKey FROG_VARIANT = of("frog_variant");
   public static final RegistryKey INSTRUMENT = of("instrument");
   public static final RegistryKey JUKEBOX_SONG = of("jukebox_song");
   public static final RegistryKey MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST = of("worldgen/multi_noise_biome_source_parameter_list");
   public static final RegistryKey CHUNK_GENERATOR_SETTINGS = of("worldgen/noise_settings");
   public static final RegistryKey NOISE_PARAMETERS = of("worldgen/noise");
   public static final RegistryKey PAINTING_VARIANT = of("painting_variant");
   public static final RegistryKey PIG_VARIANT = of("pig_variant");
   public static final RegistryKey PLACED_FEATURE = of("worldgen/placed_feature");
   public static final RegistryKey PROCESSOR_LIST = of("worldgen/processor_list");
   public static final RegistryKey STRUCTURE_SET = of("worldgen/structure_set");
   public static final RegistryKey STRUCTURE = of("worldgen/structure");
   public static final RegistryKey TEMPLATE_POOL = of("worldgen/template_pool");
   public static final RegistryKey TEST_ENVIRONMENT = of("test_environment");
   public static final RegistryKey TEST_INSTANCE = of("test_instance");
   public static final RegistryKey TRIAL_SPAWNER = of("trial_spawner");
   public static final RegistryKey CRITERION = of("trigger_type");
   public static final RegistryKey TRIM_MATERIAL = of("trim_material");
   public static final RegistryKey TRIM_PATTERN = of("trim_pattern");
   public static final RegistryKey WOLF_VARIANT = of("wolf_variant");
   public static final RegistryKey WOLF_SOUND_VARIANT = of("wolf_sound_variant");
   public static final RegistryKey WORLD_PRESET = of("worldgen/world_preset");
   public static final RegistryKey WORLD = of("dimension");
   public static final RegistryKey DIMENSION = of("dimension");
   public static final RegistryKey LOOT_TABLE = of("loot_table");
   public static final RegistryKey ITEM_MODIFIER = of("item_modifier");
   public static final RegistryKey PREDICATE = of("predicate");
   public static final RegistryKey ADVANCEMENT = of("advancement");
   public static final RegistryKey RECIPE = of("recipe");

   public static RegistryKey toWorldKey(RegistryKey key) {
      return RegistryKey.of(WORLD, key.getValue());
   }

   public static RegistryKey toDimensionKey(RegistryKey key) {
      return RegistryKey.of(DIMENSION, key.getValue());
   }

   private static RegistryKey of(String id) {
      return RegistryKey.ofRegistry(Identifier.ofVanilla(id));
   }

   public static String getPath(RegistryKey registryRef) {
      return registryRef.getValue().getPath();
   }

   public static String getTagPath(RegistryKey registryRef) {
      return "tags/" + registryRef.getValue().getPath();
   }
}
