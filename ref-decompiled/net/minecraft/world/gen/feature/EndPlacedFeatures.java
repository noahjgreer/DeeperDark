package net.minecraft.world.gen.feature;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.FixedPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.RandomOffsetPlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;

public class EndPlacedFeatures {
   public static final RegistryKey END_PLATFORM = PlacedFeatures.of("end_platform");
   public static final RegistryKey END_SPIKE = PlacedFeatures.of("end_spike");
   public static final RegistryKey END_GATEWAY_RETURN = PlacedFeatures.of("end_gateway_return");
   public static final RegistryKey CHORUS_PLANT = PlacedFeatures.of("chorus_plant");
   public static final RegistryKey END_ISLAND_DECORATED = PlacedFeatures.of("end_island_decorated");

   public static void bootstrap(Registerable featureRegisterable) {
      RegistryEntryLookup registryEntryLookup = featureRegisterable.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);
      RegistryEntry registryEntry = registryEntryLookup.getOrThrow(EndConfiguredFeatures.END_PLATFORM);
      RegistryEntry registryEntry2 = registryEntryLookup.getOrThrow(EndConfiguredFeatures.END_SPIKE);
      RegistryEntry registryEntry3 = registryEntryLookup.getOrThrow(EndConfiguredFeatures.END_GATEWAY_RETURN);
      RegistryEntry registryEntry4 = registryEntryLookup.getOrThrow(EndConfiguredFeatures.CHORUS_PLANT);
      RegistryEntry registryEntry5 = registryEntryLookup.getOrThrow(EndConfiguredFeatures.END_ISLAND);
      PlacedFeatures.register(featureRegisterable, END_PLATFORM, registryEntry, (PlacementModifier[])(FixedPlacementModifier.of(ServerWorld.END_SPAWN_POS.down()), BiomePlacementModifier.of()));
      PlacedFeatures.register(featureRegisterable, END_SPIKE, registryEntry2, (PlacementModifier[])(BiomePlacementModifier.of()));
      PlacedFeatures.register(featureRegisterable, END_GATEWAY_RETURN, registryEntry3, (PlacementModifier[])(RarityFilterPlacementModifier.of(700), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, RandomOffsetPlacementModifier.vertically(UniformIntProvider.create(3, 9)), BiomePlacementModifier.of()));
      PlacedFeatures.register(featureRegisterable, CHORUS_PLANT, registryEntry4, (PlacementModifier[])(CountPlacementModifier.of(UniformIntProvider.create(0, 4)), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of()));
      PlacedFeatures.register(featureRegisterable, END_ISLAND_DECORATED, registryEntry5, (PlacementModifier[])(RarityFilterPlacementModifier.of(14), PlacedFeatures.createCountExtraModifier(1, 0.25F, 1), SquarePlacementModifier.of(), HeightRangePlacementModifier.uniform(YOffset.fixed(55), YOffset.fixed(70)), BiomePlacementModifier.of()));
   }
}
