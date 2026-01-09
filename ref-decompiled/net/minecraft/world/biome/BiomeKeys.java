package net.minecraft.world.biome;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public abstract class BiomeKeys {
   public static final RegistryKey THE_VOID = keyOf("the_void");
   public static final RegistryKey PLAINS = keyOf("plains");
   public static final RegistryKey SUNFLOWER_PLAINS = keyOf("sunflower_plains");
   public static final RegistryKey SNOWY_PLAINS = keyOf("snowy_plains");
   public static final RegistryKey ICE_SPIKES = keyOf("ice_spikes");
   public static final RegistryKey DESERT = keyOf("desert");
   public static final RegistryKey SWAMP = keyOf("swamp");
   public static final RegistryKey MANGROVE_SWAMP = keyOf("mangrove_swamp");
   public static final RegistryKey FOREST = keyOf("forest");
   public static final RegistryKey FLOWER_FOREST = keyOf("flower_forest");
   public static final RegistryKey BIRCH_FOREST = keyOf("birch_forest");
   public static final RegistryKey DARK_FOREST = keyOf("dark_forest");
   public static final RegistryKey PALE_GARDEN = keyOf("pale_garden");
   public static final RegistryKey OLD_GROWTH_BIRCH_FOREST = keyOf("old_growth_birch_forest");
   public static final RegistryKey OLD_GROWTH_PINE_TAIGA = keyOf("old_growth_pine_taiga");
   public static final RegistryKey OLD_GROWTH_SPRUCE_TAIGA = keyOf("old_growth_spruce_taiga");
   public static final RegistryKey TAIGA = keyOf("taiga");
   public static final RegistryKey SNOWY_TAIGA = keyOf("snowy_taiga");
   public static final RegistryKey SAVANNA = keyOf("savanna");
   public static final RegistryKey SAVANNA_PLATEAU = keyOf("savanna_plateau");
   public static final RegistryKey WINDSWEPT_HILLS = keyOf("windswept_hills");
   public static final RegistryKey WINDSWEPT_GRAVELLY_HILLS = keyOf("windswept_gravelly_hills");
   public static final RegistryKey WINDSWEPT_FOREST = keyOf("windswept_forest");
   public static final RegistryKey WINDSWEPT_SAVANNA = keyOf("windswept_savanna");
   public static final RegistryKey JUNGLE = keyOf("jungle");
   public static final RegistryKey SPARSE_JUNGLE = keyOf("sparse_jungle");
   public static final RegistryKey BAMBOO_JUNGLE = keyOf("bamboo_jungle");
   public static final RegistryKey BADLANDS = keyOf("badlands");
   public static final RegistryKey ERODED_BADLANDS = keyOf("eroded_badlands");
   public static final RegistryKey WOODED_BADLANDS = keyOf("wooded_badlands");
   public static final RegistryKey MEADOW = keyOf("meadow");
   public static final RegistryKey CHERRY_GROVE = keyOf("cherry_grove");
   public static final RegistryKey GROVE = keyOf("grove");
   public static final RegistryKey SNOWY_SLOPES = keyOf("snowy_slopes");
   public static final RegistryKey FROZEN_PEAKS = keyOf("frozen_peaks");
   public static final RegistryKey JAGGED_PEAKS = keyOf("jagged_peaks");
   public static final RegistryKey STONY_PEAKS = keyOf("stony_peaks");
   public static final RegistryKey RIVER = keyOf("river");
   public static final RegistryKey FROZEN_RIVER = keyOf("frozen_river");
   public static final RegistryKey BEACH = keyOf("beach");
   public static final RegistryKey SNOWY_BEACH = keyOf("snowy_beach");
   public static final RegistryKey STONY_SHORE = keyOf("stony_shore");
   public static final RegistryKey WARM_OCEAN = keyOf("warm_ocean");
   public static final RegistryKey LUKEWARM_OCEAN = keyOf("lukewarm_ocean");
   public static final RegistryKey DEEP_LUKEWARM_OCEAN = keyOf("deep_lukewarm_ocean");
   public static final RegistryKey OCEAN = keyOf("ocean");
   public static final RegistryKey DEEP_OCEAN = keyOf("deep_ocean");
   public static final RegistryKey COLD_OCEAN = keyOf("cold_ocean");
   public static final RegistryKey DEEP_COLD_OCEAN = keyOf("deep_cold_ocean");
   public static final RegistryKey FROZEN_OCEAN = keyOf("frozen_ocean");
   public static final RegistryKey DEEP_FROZEN_OCEAN = keyOf("deep_frozen_ocean");
   public static final RegistryKey MUSHROOM_FIELDS = keyOf("mushroom_fields");
   public static final RegistryKey DRIPSTONE_CAVES = keyOf("dripstone_caves");
   public static final RegistryKey LUSH_CAVES = keyOf("lush_caves");
   public static final RegistryKey DEEP_DARK = keyOf("deep_dark");
   public static final RegistryKey NETHER_WASTES = keyOf("nether_wastes");
   public static final RegistryKey WARPED_FOREST = keyOf("warped_forest");
   public static final RegistryKey CRIMSON_FOREST = keyOf("crimson_forest");
   public static final RegistryKey SOUL_SAND_VALLEY = keyOf("soul_sand_valley");
   public static final RegistryKey BASALT_DELTAS = keyOf("basalt_deltas");
   public static final RegistryKey THE_END = keyOf("the_end");
   public static final RegistryKey END_HIGHLANDS = keyOf("end_highlands");
   public static final RegistryKey END_MIDLANDS = keyOf("end_midlands");
   public static final RegistryKey SMALL_END_ISLANDS = keyOf("small_end_islands");
   public static final RegistryKey END_BARRENS = keyOf("end_barrens");

   private static RegistryKey keyOf(String id) {
      return RegistryKey.of(RegistryKeys.BIOME, Identifier.ofVanilla(id));
   }
}
