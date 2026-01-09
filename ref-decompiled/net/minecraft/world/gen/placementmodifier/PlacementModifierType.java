package net.minecraft.world.gen.placementmodifier;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface PlacementModifierType {
   PlacementModifierType BLOCK_PREDICATE_FILTER = register("block_predicate_filter", BlockFilterPlacementModifier.MODIFIER_CODEC);
   PlacementModifierType RARITY_FILTER = register("rarity_filter", RarityFilterPlacementModifier.MODIFIER_CODEC);
   PlacementModifierType SURFACE_RELATIVE_THRESHOLD_FILTER = register("surface_relative_threshold_filter", SurfaceThresholdFilterPlacementModifier.MODIFIER_CODEC);
   PlacementModifierType SURFACE_WATER_DEPTH_FILTER = register("surface_water_depth_filter", SurfaceWaterDepthFilterPlacementModifier.MODIFIER_CODEC);
   PlacementModifierType BIOME = register("biome", BiomePlacementModifier.MODIFIER_CODEC);
   PlacementModifierType COUNT = register("count", CountPlacementModifier.MODIFIER_CODEC);
   PlacementModifierType NOISE_BASED_COUNT = register("noise_based_count", NoiseBasedCountPlacementModifier.MODIFIER_CODEC);
   PlacementModifierType NOISE_THRESHOLD_COUNT = register("noise_threshold_count", NoiseThresholdCountPlacementModifier.MODIFIER_CODEC);
   PlacementModifierType COUNT_ON_EVERY_LAYER = register("count_on_every_layer", CountMultilayerPlacementModifier.MODIFIER_CODEC);
   PlacementModifierType ENVIRONMENT_SCAN = register("environment_scan", EnvironmentScanPlacementModifier.MODIFIER_CODEC);
   PlacementModifierType HEIGHTMAP = register("heightmap", HeightmapPlacementModifier.MODIFIER_CODEC);
   PlacementModifierType HEIGHT_RANGE = register("height_range", HeightRangePlacementModifier.MODIFIER_CODEC);
   PlacementModifierType IN_SQUARE = register("in_square", SquarePlacementModifier.MODIFIER_CODEC);
   PlacementModifierType RANDOM_OFFSET = register("random_offset", RandomOffsetPlacementModifier.MODIFIER_CODEC);
   PlacementModifierType FIXED_PLACEMENT = register("fixed_placement", FixedPlacementModifier.CODEC);

   MapCodec codec();

   private static PlacementModifierType register(String id, MapCodec codec) {
      return (PlacementModifierType)Registry.register(Registries.PLACEMENT_MODIFIER_TYPE, (String)id, () -> {
         return codec;
      });
   }
}
