package net.minecraft.datafixer;

import com.mojang.datafixers.DSL;

public class TypeReferences {
   public static final DSL.TypeReference LEVEL = create("level");
   public static final DSL.TypeReference LIGHTWEIGHT_LEVEL = create("lightweight_level");
   public static final DSL.TypeReference PLAYER = create("player");
   public static final DSL.TypeReference CHUNK = create("chunk");
   public static final DSL.TypeReference HOTBAR = create("hotbar");
   public static final DSL.TypeReference OPTIONS = create("options");
   public static final DSL.TypeReference STRUCTURE = create("structure");
   public static final DSL.TypeReference STATS = create("stats");
   public static final DSL.TypeReference SAVED_DATA_COMMAND_STORAGE = create("saved_data/command_storage");
   public static final DSL.TypeReference TICKETS_SAVED_DATA = create("saved_data/tickets");
   public static final DSL.TypeReference SAVED_DATA_MAP_DATA = create("saved_data/map_data");
   public static final DSL.TypeReference SAVED_DATA_IDCOUNTS = create("saved_data/idcounts");
   public static final DSL.TypeReference SAVED_DATA_RAIDS = create("saved_data/raids");
   public static final DSL.TypeReference SAVED_DATA_RANDOM_SEQUENCES = create("saved_data/random_sequences");
   public static final DSL.TypeReference SAVED_DATA_STRUCTURE_FEATURE_INDICES = create("saved_data/structure_feature_indices");
   public static final DSL.TypeReference SAVED_DATA_SCOREBOARD = create("saved_data/scoreboard");
   public static final DSL.TypeReference ADVANCEMENTS = create("advancements");
   public static final DSL.TypeReference POI_CHUNK = create("poi_chunk");
   public static final DSL.TypeReference ENTITY_CHUNK = create("entity_chunk");
   public static final DSL.TypeReference BLOCK_ENTITY = create("block_entity");
   public static final DSL.TypeReference ITEM_STACK = create("item_stack");
   public static final DSL.TypeReference BLOCK_STATE = create("block_state");
   public static final DSL.TypeReference FLAT_BLOCK_STATE = create("flat_block_state");
   public static final DSL.TypeReference DATA_COMPONENTS = create("data_components");
   public static final DSL.TypeReference VILLAGER_TRADE = create("villager_trade");
   public static final DSL.TypeReference PARTICLE = create("particle");
   public static final DSL.TypeReference TEXT_COMPONENT = create("text_component");
   public static final DSL.TypeReference ENTITY_EQUIPMENT = create("entity_equipment");
   public static final DSL.TypeReference ENTITY_NAME = create("entity_name");
   public static final DSL.TypeReference ENTITY_TREE = create("entity_tree");
   public static final DSL.TypeReference ENTITY = create("entity");
   public static final DSL.TypeReference BLOCK_NAME = create("block_name");
   public static final DSL.TypeReference ITEM_NAME = create("item_name");
   public static final DSL.TypeReference GAME_EVENT_NAME = create("game_event_name");
   public static final DSL.TypeReference UNTAGGED_SPAWNER = create("untagged_spawner");
   public static final DSL.TypeReference STRUCTURE_FEATURE = create("structure_feature");
   public static final DSL.TypeReference OBJECTIVE = create("objective");
   public static final DSL.TypeReference TEAM = create("team");
   public static final DSL.TypeReference RECIPE = create("recipe");
   public static final DSL.TypeReference BIOME = create("biome");
   public static final DSL.TypeReference MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST = create("multi_noise_biome_source_parameter_list");
   public static final DSL.TypeReference WORLD_GEN_SETTINGS = create("world_gen_settings");

   public static DSL.TypeReference create(final String typeName) {
      return new DSL.TypeReference() {
         public String typeName() {
            return typeName;
         }

         public String toString() {
            return "@" + typeName;
         }
      };
   }
}
