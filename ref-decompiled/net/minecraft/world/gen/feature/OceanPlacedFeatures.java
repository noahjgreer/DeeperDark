package net.minecraft.world.gen.feature;

import java.util.List;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.NoiseBasedCountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;

public class OceanPlacedFeatures {
   public static final RegistryKey SEAGRASS_WARM = PlacedFeatures.of("seagrass_warm");
   public static final RegistryKey SEAGRASS_NORMAL = PlacedFeatures.of("seagrass_normal");
   public static final RegistryKey SEAGRASS_COLD = PlacedFeatures.of("seagrass_cold");
   public static final RegistryKey SEAGRASS_RIVER = PlacedFeatures.of("seagrass_river");
   public static final RegistryKey SEAGRASS_SWAMP = PlacedFeatures.of("seagrass_swamp");
   public static final RegistryKey SEAGRASS_DEEP_WARM = PlacedFeatures.of("seagrass_deep_warm");
   public static final RegistryKey SEAGRASS_DEEP = PlacedFeatures.of("seagrass_deep");
   public static final RegistryKey SEAGRASS_DEEP_COLD = PlacedFeatures.of("seagrass_deep_cold");
   public static final RegistryKey SEA_PICKLE = PlacedFeatures.of("sea_pickle");
   public static final RegistryKey KELP_COLD = PlacedFeatures.of("kelp_cold");
   public static final RegistryKey KELP_WARM = PlacedFeatures.of("kelp_warm");
   public static final RegistryKey WARM_OCEAN_VEGETATION = PlacedFeatures.of("warm_ocean_vegetation");

   private static List seagrassModifiers(int count) {
      return List.of(SquarePlacementModifier.of(), PlacedFeatures.OCEAN_FLOOR_WG_HEIGHTMAP, CountPlacementModifier.of(count), BiomePlacementModifier.of());
   }

   public static void bootstrap(Registerable featureRegisterable) {
      RegistryEntryLookup registryEntryLookup = featureRegisterable.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);
      RegistryEntry.Reference reference = registryEntryLookup.getOrThrow(OceanConfiguredFeatures.SEAGRASS_SHORT);
      RegistryEntry.Reference reference2 = registryEntryLookup.getOrThrow(OceanConfiguredFeatures.SEAGRASS_SLIGHTLY_LESS_SHORT);
      RegistryEntry.Reference reference3 = registryEntryLookup.getOrThrow(OceanConfiguredFeatures.SEAGRASS_MID);
      RegistryEntry.Reference reference4 = registryEntryLookup.getOrThrow(OceanConfiguredFeatures.SEAGRASS_TALL);
      RegistryEntry.Reference reference5 = registryEntryLookup.getOrThrow(OceanConfiguredFeatures.SEA_PICKLE);
      RegistryEntry.Reference reference6 = registryEntryLookup.getOrThrow(OceanConfiguredFeatures.KELP);
      RegistryEntry.Reference reference7 = registryEntryLookup.getOrThrow(OceanConfiguredFeatures.WARM_OCEAN_VEGETATION);
      PlacedFeatures.register(featureRegisterable, SEAGRASS_WARM, reference, (List)seagrassModifiers(80));
      PlacedFeatures.register(featureRegisterable, SEAGRASS_NORMAL, reference, (List)seagrassModifiers(48));
      PlacedFeatures.register(featureRegisterable, SEAGRASS_COLD, reference, (List)seagrassModifiers(32));
      PlacedFeatures.register(featureRegisterable, SEAGRASS_RIVER, reference2, (List)seagrassModifiers(48));
      PlacedFeatures.register(featureRegisterable, SEAGRASS_SWAMP, reference3, (List)seagrassModifiers(64));
      PlacedFeatures.register(featureRegisterable, SEAGRASS_DEEP_WARM, reference4, (List)seagrassModifiers(80));
      PlacedFeatures.register(featureRegisterable, SEAGRASS_DEEP, reference4, (List)seagrassModifiers(48));
      PlacedFeatures.register(featureRegisterable, SEAGRASS_DEEP_COLD, reference4, (List)seagrassModifiers(40));
      PlacedFeatures.register(featureRegisterable, SEA_PICKLE, reference5, (PlacementModifier[])(RarityFilterPlacementModifier.of(16), SquarePlacementModifier.of(), PlacedFeatures.OCEAN_FLOOR_WG_HEIGHTMAP, BiomePlacementModifier.of()));
      PlacedFeatures.register(featureRegisterable, KELP_COLD, reference6, (PlacementModifier[])(NoiseBasedCountPlacementModifier.of(120, 80.0, 0.0), SquarePlacementModifier.of(), PlacedFeatures.OCEAN_FLOOR_WG_HEIGHTMAP, BiomePlacementModifier.of()));
      PlacedFeatures.register(featureRegisterable, KELP_WARM, reference6, (PlacementModifier[])(NoiseBasedCountPlacementModifier.of(80, 80.0, 0.0), SquarePlacementModifier.of(), PlacedFeatures.OCEAN_FLOOR_WG_HEIGHTMAP, BiomePlacementModifier.of()));
      PlacedFeatures.register(featureRegisterable, WARM_OCEAN_VEGETATION, reference7, (PlacementModifier[])(NoiseBasedCountPlacementModifier.of(20, 400.0, 0.0), SquarePlacementModifier.of(), PlacedFeatures.OCEAN_FLOOR_WG_HEIGHTMAP, BiomePlacementModifier.of()));
   }
}
