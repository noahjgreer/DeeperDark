package net.minecraft.data.tag.vanilla;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.tag.SimpleTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterList;

public class VanillaBiomeTagProvider extends SimpleTagProvider {
   public VanillaBiomeTagProvider(DataOutput output, CompletableFuture registriesFuture) {
      super(output, RegistryKeys.BIOME, registriesFuture);
   }

   protected void configure(RegistryWrapper.WrapperLookup registries) {
      this.builder(BiomeTags.IS_DEEP_OCEAN).add((Object)BiomeKeys.DEEP_FROZEN_OCEAN).add((Object)BiomeKeys.DEEP_COLD_OCEAN).add((Object)BiomeKeys.DEEP_OCEAN).add((Object)BiomeKeys.DEEP_LUKEWARM_OCEAN);
      this.builder(BiomeTags.IS_OCEAN).addTag(BiomeTags.IS_DEEP_OCEAN).add((Object)BiomeKeys.FROZEN_OCEAN).add((Object)BiomeKeys.OCEAN).add((Object)BiomeKeys.COLD_OCEAN).add((Object)BiomeKeys.LUKEWARM_OCEAN).add((Object)BiomeKeys.WARM_OCEAN);
      this.builder(BiomeTags.IS_BEACH).add((Object)BiomeKeys.BEACH).add((Object)BiomeKeys.SNOWY_BEACH);
      this.builder(BiomeTags.IS_RIVER).add((Object)BiomeKeys.RIVER).add((Object)BiomeKeys.FROZEN_RIVER);
      this.builder(BiomeTags.IS_MOUNTAIN).add((Object)BiomeKeys.MEADOW).add((Object)BiomeKeys.FROZEN_PEAKS).add((Object)BiomeKeys.JAGGED_PEAKS).add((Object)BiomeKeys.STONY_PEAKS).add((Object)BiomeKeys.SNOWY_SLOPES).add((Object)BiomeKeys.CHERRY_GROVE);
      this.builder(BiomeTags.IS_BADLANDS).add((Object)BiomeKeys.BADLANDS).add((Object)BiomeKeys.ERODED_BADLANDS).add((Object)BiomeKeys.WOODED_BADLANDS);
      this.builder(BiomeTags.IS_HILL).add((Object)BiomeKeys.WINDSWEPT_HILLS).add((Object)BiomeKeys.WINDSWEPT_FOREST).add((Object)BiomeKeys.WINDSWEPT_GRAVELLY_HILLS);
      this.builder(BiomeTags.IS_TAIGA).add((Object)BiomeKeys.TAIGA).add((Object)BiomeKeys.SNOWY_TAIGA).add((Object)BiomeKeys.OLD_GROWTH_PINE_TAIGA).add((Object)BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA);
      this.builder(BiomeTags.IS_JUNGLE).add((Object)BiomeKeys.BAMBOO_JUNGLE).add((Object)BiomeKeys.JUNGLE).add((Object)BiomeKeys.SPARSE_JUNGLE);
      this.builder(BiomeTags.IS_FOREST).add((Object)BiomeKeys.FOREST).add((Object)BiomeKeys.FLOWER_FOREST).add((Object)BiomeKeys.BIRCH_FOREST).add((Object)BiomeKeys.OLD_GROWTH_BIRCH_FOREST).add((Object)BiomeKeys.DARK_FOREST).add((Object)BiomeKeys.PALE_GARDEN).add((Object)BiomeKeys.GROVE);
      this.builder(BiomeTags.IS_SAVANNA).add((Object)BiomeKeys.SAVANNA).add((Object)BiomeKeys.SAVANNA_PLATEAU).add((Object)BiomeKeys.WINDSWEPT_SAVANNA);
      this.builder(BiomeTags.IS_NETHER).add(MultiNoiseBiomeSourceParameterList.Preset.NETHER.biomeStream());
      List list = MultiNoiseBiomeSourceParameterList.Preset.OVERWORLD.biomeStream().toList();
      this.builder(BiomeTags.IS_OVERWORLD).add((Collection)list);
      this.builder(BiomeTags.IS_END).add((Object)BiomeKeys.THE_END).add((Object)BiomeKeys.END_HIGHLANDS).add((Object)BiomeKeys.END_MIDLANDS).add((Object)BiomeKeys.SMALL_END_ISLANDS).add((Object)BiomeKeys.END_BARRENS);
      this.builder(BiomeTags.BURIED_TREASURE_HAS_STRUCTURE).addTag(BiomeTags.IS_BEACH);
      this.builder(BiomeTags.DESERT_PYRAMID_HAS_STRUCTURE).add((Object)BiomeKeys.DESERT);
      this.builder(BiomeTags.IGLOO_HAS_STRUCTURE).add((Object)BiomeKeys.SNOWY_TAIGA).add((Object)BiomeKeys.SNOWY_PLAINS).add((Object)BiomeKeys.SNOWY_SLOPES);
      this.builder(BiomeTags.JUNGLE_TEMPLE_HAS_STRUCTURE).add((Object)BiomeKeys.BAMBOO_JUNGLE).add((Object)BiomeKeys.JUNGLE);
      this.builder(BiomeTags.MINESHAFT_HAS_STRUCTURE).addTag(BiomeTags.IS_OCEAN).addTag(BiomeTags.IS_RIVER).addTag(BiomeTags.IS_BEACH).addTag(BiomeTags.IS_MOUNTAIN).addTag(BiomeTags.IS_HILL).addTag(BiomeTags.IS_TAIGA).addTag(BiomeTags.IS_JUNGLE).addTag(BiomeTags.IS_FOREST).add((Object)BiomeKeys.STONY_SHORE).add((Object)BiomeKeys.MUSHROOM_FIELDS).add((Object)BiomeKeys.ICE_SPIKES).add((Object)BiomeKeys.WINDSWEPT_SAVANNA).add((Object)BiomeKeys.DESERT).add((Object)BiomeKeys.SAVANNA).add((Object)BiomeKeys.SNOWY_PLAINS).add((Object)BiomeKeys.PLAINS).add((Object)BiomeKeys.SUNFLOWER_PLAINS).add((Object)BiomeKeys.SWAMP).add((Object)BiomeKeys.MANGROVE_SWAMP).add((Object)BiomeKeys.SAVANNA_PLATEAU).add((Object)BiomeKeys.DRIPSTONE_CAVES).add((Object)BiomeKeys.LUSH_CAVES);
      this.builder(BiomeTags.MINESHAFT_MESA_HAS_STRUCTURE).addTag(BiomeTags.IS_BADLANDS);
      this.builder(BiomeTags.MINESHAFT_BLOCKING).add((Object)BiomeKeys.DEEP_DARK);
      this.builder(BiomeTags.OCEAN_MONUMENT_HAS_STRUCTURE).addTag(BiomeTags.IS_DEEP_OCEAN);
      this.builder(BiomeTags.REQUIRED_OCEAN_MONUMENT_SURROUNDING).addTag(BiomeTags.IS_OCEAN).addTag(BiomeTags.IS_RIVER);
      this.builder(BiomeTags.OCEAN_RUIN_COLD_HAS_STRUCTURE).add((Object)BiomeKeys.FROZEN_OCEAN).add((Object)BiomeKeys.COLD_OCEAN).add((Object)BiomeKeys.OCEAN).add((Object)BiomeKeys.DEEP_FROZEN_OCEAN).add((Object)BiomeKeys.DEEP_COLD_OCEAN).add((Object)BiomeKeys.DEEP_OCEAN);
      this.builder(BiomeTags.OCEAN_RUIN_WARM_HAS_STRUCTURE).add((Object)BiomeKeys.LUKEWARM_OCEAN).add((Object)BiomeKeys.WARM_OCEAN).add((Object)BiomeKeys.DEEP_LUKEWARM_OCEAN);
      this.builder(BiomeTags.PILLAGER_OUTPOST_HAS_STRUCTURE).add((Object)BiomeKeys.DESERT).add((Object)BiomeKeys.PLAINS).add((Object)BiomeKeys.SAVANNA).add((Object)BiomeKeys.SNOWY_PLAINS).add((Object)BiomeKeys.TAIGA).addTag(BiomeTags.IS_MOUNTAIN).add((Object)BiomeKeys.GROVE);
      this.builder(BiomeTags.RUINED_PORTAL_DESERT_HAS_STRUCTURE).add((Object)BiomeKeys.DESERT);
      this.builder(BiomeTags.RUINED_PORTAL_JUNGLE_HAS_STRUCTURE).addTag(BiomeTags.IS_JUNGLE);
      this.builder(BiomeTags.RUINED_PORTAL_OCEAN_HAS_STRUCTURE).addTag(BiomeTags.IS_OCEAN);
      this.builder(BiomeTags.RUINED_PORTAL_SWAMP_HAS_STRUCTURE).add((Object)BiomeKeys.SWAMP).add((Object)BiomeKeys.MANGROVE_SWAMP);
      this.builder(BiomeTags.RUINED_PORTAL_MOUNTAIN_HAS_STRUCTURE).addTag(BiomeTags.IS_BADLANDS).addTag(BiomeTags.IS_HILL).add((Object)BiomeKeys.SAVANNA_PLATEAU).add((Object)BiomeKeys.WINDSWEPT_SAVANNA).add((Object)BiomeKeys.STONY_SHORE).addTag(BiomeTags.IS_MOUNTAIN);
      this.builder(BiomeTags.RUINED_PORTAL_STANDARD_HAS_STRUCTURE).addTag(BiomeTags.IS_BEACH).addTag(BiomeTags.IS_RIVER).addTag(BiomeTags.IS_TAIGA).addTag(BiomeTags.IS_FOREST).add((Object)BiomeKeys.MUSHROOM_FIELDS).add((Object)BiomeKeys.ICE_SPIKES).add((Object)BiomeKeys.DRIPSTONE_CAVES).add((Object)BiomeKeys.LUSH_CAVES).add((Object)BiomeKeys.SAVANNA).add((Object)BiomeKeys.SNOWY_PLAINS).add((Object)BiomeKeys.PLAINS).add((Object)BiomeKeys.SUNFLOWER_PLAINS);
      this.builder(BiomeTags.SHIPWRECK_BEACHED_HAS_STRUCTURE).addTag(BiomeTags.IS_BEACH);
      this.builder(BiomeTags.SHIPWRECK_HAS_STRUCTURE).addTag(BiomeTags.IS_OCEAN);
      this.builder(BiomeTags.SWAMP_HUT_HAS_STRUCTURE).add((Object)BiomeKeys.SWAMP);
      this.builder(BiomeTags.VILLAGE_DESERT_HAS_STRUCTURE).add((Object)BiomeKeys.DESERT);
      this.builder(BiomeTags.VILLAGE_PLAINS_HAS_STRUCTURE).add((Object)BiomeKeys.PLAINS).add((Object)BiomeKeys.MEADOW);
      this.builder(BiomeTags.VILLAGE_SAVANNA_HAS_STRUCTURE).add((Object)BiomeKeys.SAVANNA);
      this.builder(BiomeTags.VILLAGE_SNOWY_HAS_STRUCTURE).add((Object)BiomeKeys.SNOWY_PLAINS);
      this.builder(BiomeTags.VILLAGE_TAIGA_HAS_STRUCTURE).add((Object)BiomeKeys.TAIGA);
      this.builder(BiomeTags.TRAIL_RUINS_HAS_STRUCTURE).add((Object)BiomeKeys.TAIGA).add((Object)BiomeKeys.SNOWY_TAIGA).add((Object)BiomeKeys.OLD_GROWTH_PINE_TAIGA).add((Object)BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA).add((Object)BiomeKeys.OLD_GROWTH_BIRCH_FOREST).add((Object)BiomeKeys.JUNGLE);
      this.builder(BiomeTags.WOODLAND_MANSION_HAS_STRUCTURE).add((Object)BiomeKeys.DARK_FOREST).add((Object)BiomeKeys.PALE_GARDEN);
      this.builder(BiomeTags.STRONGHOLD_BIASED_TO).add((Object)BiomeKeys.PLAINS).add((Object)BiomeKeys.SUNFLOWER_PLAINS).add((Object)BiomeKeys.SNOWY_PLAINS).add((Object)BiomeKeys.ICE_SPIKES).add((Object)BiomeKeys.DESERT).add((Object)BiomeKeys.FOREST).add((Object)BiomeKeys.FLOWER_FOREST).add((Object)BiomeKeys.BIRCH_FOREST).add((Object)BiomeKeys.DARK_FOREST).add((Object)BiomeKeys.PALE_GARDEN).add((Object)BiomeKeys.OLD_GROWTH_BIRCH_FOREST).add((Object)BiomeKeys.OLD_GROWTH_PINE_TAIGA).add((Object)BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA).add((Object)BiomeKeys.TAIGA).add((Object)BiomeKeys.SNOWY_TAIGA).add((Object)BiomeKeys.SAVANNA).add((Object)BiomeKeys.SAVANNA_PLATEAU).add((Object)BiomeKeys.WINDSWEPT_HILLS).add((Object)BiomeKeys.WINDSWEPT_GRAVELLY_HILLS).add((Object)BiomeKeys.WINDSWEPT_FOREST).add((Object)BiomeKeys.WINDSWEPT_SAVANNA).add((Object)BiomeKeys.JUNGLE).add((Object)BiomeKeys.SPARSE_JUNGLE).add((Object)BiomeKeys.BAMBOO_JUNGLE).add((Object)BiomeKeys.BADLANDS).add((Object)BiomeKeys.ERODED_BADLANDS).add((Object)BiomeKeys.WOODED_BADLANDS).add((Object)BiomeKeys.MEADOW).add((Object)BiomeKeys.GROVE).add((Object)BiomeKeys.SNOWY_SLOPES).add((Object)BiomeKeys.FROZEN_PEAKS).add((Object)BiomeKeys.JAGGED_PEAKS).add((Object)BiomeKeys.STONY_PEAKS).add((Object)BiomeKeys.MUSHROOM_FIELDS).add((Object)BiomeKeys.DRIPSTONE_CAVES).add((Object)BiomeKeys.LUSH_CAVES);
      this.builder(BiomeTags.STRONGHOLD_HAS_STRUCTURE).addTag(BiomeTags.IS_OVERWORLD);
      this.builder(BiomeTags.TRIAL_CHAMBERS_HAS_STRUCTURE).add(list.stream().filter((registryKey) -> {
         return registryKey != BiomeKeys.DEEP_DARK;
      }));
      this.builder(BiomeTags.NETHER_FORTRESS_HAS_STRUCTURE).addTag(BiomeTags.IS_NETHER);
      this.builder(BiomeTags.NETHER_FOSSIL_HAS_STRUCTURE).add((Object)BiomeKeys.SOUL_SAND_VALLEY);
      this.builder(BiomeTags.BASTION_REMNANT_HAS_STRUCTURE).add((Object)BiomeKeys.CRIMSON_FOREST).add((Object)BiomeKeys.NETHER_WASTES).add((Object)BiomeKeys.SOUL_SAND_VALLEY).add((Object)BiomeKeys.WARPED_FOREST);
      this.builder(BiomeTags.ANCIENT_CITY_HAS_STRUCTURE).add((Object)BiomeKeys.DEEP_DARK);
      this.builder(BiomeTags.RUINED_PORTAL_NETHER_HAS_STRUCTURE).addTag(BiomeTags.IS_NETHER);
      this.builder(BiomeTags.END_CITY_HAS_STRUCTURE).add((Object)BiomeKeys.END_HIGHLANDS).add((Object)BiomeKeys.END_MIDLANDS);
      this.builder(BiomeTags.PRODUCES_CORALS_FROM_BONEMEAL).add((Object)BiomeKeys.WARM_OCEAN);
      this.builder(BiomeTags.PLAYS_UNDERWATER_MUSIC).addTag(BiomeTags.IS_OCEAN).addTag(BiomeTags.IS_RIVER);
      this.builder(BiomeTags.HAS_CLOSER_WATER_FOG).add((Object)BiomeKeys.SWAMP).add((Object)BiomeKeys.MANGROVE_SWAMP);
      this.builder(BiomeTags.WATER_ON_MAP_OUTLINES).addTag(BiomeTags.IS_OCEAN).addTag(BiomeTags.IS_RIVER).add((Object)BiomeKeys.SWAMP).add((Object)BiomeKeys.MANGROVE_SWAMP);
      this.builder(BiomeTags.WITHOUT_ZOMBIE_SIEGES).add((Object)BiomeKeys.MUSHROOM_FIELDS);
      this.builder(BiomeTags.WITHOUT_PATROL_SPAWNS).add((Object)BiomeKeys.MUSHROOM_FIELDS);
      this.builder(BiomeTags.WITHOUT_WANDERING_TRADER_SPAWNS).add((Object)BiomeKeys.THE_VOID);
      this.builder(BiomeTags.SPAWNS_COLD_VARIANT_FROGS).add((Object)BiomeKeys.SNOWY_PLAINS).add((Object)BiomeKeys.ICE_SPIKES).add((Object)BiomeKeys.FROZEN_PEAKS).add((Object)BiomeKeys.JAGGED_PEAKS).add((Object)BiomeKeys.SNOWY_SLOPES).add((Object)BiomeKeys.FROZEN_OCEAN).add((Object)BiomeKeys.DEEP_FROZEN_OCEAN).add((Object)BiomeKeys.GROVE).add((Object)BiomeKeys.DEEP_DARK).add((Object)BiomeKeys.FROZEN_RIVER).add((Object)BiomeKeys.SNOWY_TAIGA).add((Object)BiomeKeys.SNOWY_BEACH).addTag(BiomeTags.IS_END);
      this.builder(BiomeTags.SPAWNS_WARM_VARIANT_FROGS).add((Object)BiomeKeys.DESERT).add((Object)BiomeKeys.WARM_OCEAN).addTag(BiomeTags.IS_JUNGLE).addTag(BiomeTags.IS_SAVANNA).addTag(BiomeTags.IS_NETHER).addTag(BiomeTags.IS_BADLANDS).add((Object)BiomeKeys.MANGROVE_SWAMP);
      this.builder(BiomeTags.SPAWNS_COLD_VARIANT_FARM_ANIMALS).add((Object)BiomeKeys.SNOWY_PLAINS).add((Object)BiomeKeys.ICE_SPIKES).add((Object)BiomeKeys.FROZEN_PEAKS).add((Object)BiomeKeys.JAGGED_PEAKS).add((Object)BiomeKeys.SNOWY_SLOPES).add((Object)BiomeKeys.FROZEN_OCEAN).add((Object)BiomeKeys.DEEP_FROZEN_OCEAN).add((Object)BiomeKeys.GROVE).add((Object)BiomeKeys.DEEP_DARK).add((Object)BiomeKeys.FROZEN_RIVER).add((Object)BiomeKeys.SNOWY_TAIGA).add((Object)BiomeKeys.SNOWY_BEACH).addTag(BiomeTags.IS_END).add((Object)BiomeKeys.COLD_OCEAN).add((Object)BiomeKeys.DEEP_COLD_OCEAN).add((Object)BiomeKeys.OLD_GROWTH_PINE_TAIGA).add((Object)BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA).add((Object)BiomeKeys.TAIGA).add((Object)BiomeKeys.WINDSWEPT_FOREST).add((Object)BiomeKeys.WINDSWEPT_GRAVELLY_HILLS).add((Object)BiomeKeys.WINDSWEPT_HILLS).add((Object)BiomeKeys.STONY_PEAKS);
      this.builder(BiomeTags.SPAWNS_WARM_VARIANT_FARM_ANIMALS).add((Object)BiomeKeys.DESERT).add((Object)BiomeKeys.WARM_OCEAN).addTag(BiomeTags.IS_JUNGLE).addTag(BiomeTags.IS_SAVANNA).addTag(BiomeTags.IS_NETHER).addTag(BiomeTags.IS_BADLANDS).add((Object)BiomeKeys.MANGROVE_SWAMP).add((Object)BiomeKeys.DEEP_LUKEWARM_OCEAN).add((Object)BiomeKeys.LUKEWARM_OCEAN);
      this.builder(BiomeTags.SPAWNS_GOLD_RABBITS).add((Object)BiomeKeys.DESERT);
      this.builder(BiomeTags.SPAWNS_WHITE_RABBITS).add((Object)BiomeKeys.SNOWY_PLAINS).add((Object)BiomeKeys.ICE_SPIKES).add((Object)BiomeKeys.FROZEN_OCEAN).add((Object)BiomeKeys.SNOWY_TAIGA).add((Object)BiomeKeys.FROZEN_RIVER).add((Object)BiomeKeys.SNOWY_BEACH).add((Object)BiomeKeys.FROZEN_PEAKS).add((Object)BiomeKeys.JAGGED_PEAKS).add((Object)BiomeKeys.SNOWY_SLOPES).add((Object)BiomeKeys.GROVE);
      this.builder(BiomeTags.REDUCE_WATER_AMBIENT_SPAWNS).addTag(BiomeTags.IS_RIVER);
      this.builder(BiomeTags.ALLOWS_TROPICAL_FISH_SPAWNS_AT_ANY_HEIGHT).add((Object)BiomeKeys.LUSH_CAVES);
      this.builder(BiomeTags.POLAR_BEARS_SPAWN_ON_ALTERNATE_BLOCKS).add((Object)BiomeKeys.FROZEN_OCEAN).add((Object)BiomeKeys.DEEP_FROZEN_OCEAN);
      this.builder(BiomeTags.MORE_FREQUENT_DROWNED_SPAWNS).addTag(BiomeTags.IS_RIVER);
      this.builder(BiomeTags.ALLOWS_SURFACE_SLIME_SPAWNS).add((Object)BiomeKeys.SWAMP).add((Object)BiomeKeys.MANGROVE_SWAMP);
      this.builder(BiomeTags.SPAWNS_SNOW_FOXES).add((Object)BiomeKeys.SNOWY_PLAINS).add((Object)BiomeKeys.ICE_SPIKES).add((Object)BiomeKeys.FROZEN_OCEAN).add((Object)BiomeKeys.SNOWY_TAIGA).add((Object)BiomeKeys.FROZEN_RIVER).add((Object)BiomeKeys.SNOWY_BEACH).add((Object)BiomeKeys.FROZEN_PEAKS).add((Object)BiomeKeys.JAGGED_PEAKS).add((Object)BiomeKeys.SNOWY_SLOPES).add((Object)BiomeKeys.GROVE);
      this.builder(BiomeTags.INCREASED_FIRE_BURNOUT).add((Object)BiomeKeys.BAMBOO_JUNGLE).add((Object)BiomeKeys.MUSHROOM_FIELDS).add((Object)BiomeKeys.MANGROVE_SWAMP).add((Object)BiomeKeys.SNOWY_SLOPES).add((Object)BiomeKeys.FROZEN_PEAKS).add((Object)BiomeKeys.JAGGED_PEAKS).add((Object)BiomeKeys.SWAMP).add((Object)BiomeKeys.JUNGLE);
      this.builder(BiomeTags.SNOW_GOLEM_MELTS).add((Object)BiomeKeys.BADLANDS).add((Object)BiomeKeys.BASALT_DELTAS).add((Object)BiomeKeys.CRIMSON_FOREST).add((Object)BiomeKeys.DESERT).add((Object)BiomeKeys.ERODED_BADLANDS).add((Object)BiomeKeys.NETHER_WASTES).add((Object)BiomeKeys.SAVANNA).add((Object)BiomeKeys.SAVANNA_PLATEAU).add((Object)BiomeKeys.SOUL_SAND_VALLEY).add((Object)BiomeKeys.WARPED_FOREST).add((Object)BiomeKeys.WINDSWEPT_SAVANNA).add((Object)BiomeKeys.WOODED_BADLANDS);
   }
}
