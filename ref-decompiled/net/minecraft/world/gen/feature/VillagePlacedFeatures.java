package net.minecraft.world.gen.feature;

import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;

public class VillagePlacedFeatures {
   public static final RegistryKey PILE_HAY = PlacedFeatures.of("pile_hay");
   public static final RegistryKey PILE_MELON = PlacedFeatures.of("pile_melon");
   public static final RegistryKey PILE_SNOW = PlacedFeatures.of("pile_snow");
   public static final RegistryKey PILE_ICE = PlacedFeatures.of("pile_ice");
   public static final RegistryKey PILE_PUMPKIN = PlacedFeatures.of("pile_pumpkin");
   public static final RegistryKey OAK = PlacedFeatures.of("oak");
   public static final RegistryKey ACACIA = PlacedFeatures.of("acacia");
   public static final RegistryKey SPRUCE = PlacedFeatures.of("spruce");
   public static final RegistryKey PINE = PlacedFeatures.of("pine");
   public static final RegistryKey PATCH_CACTUS = PlacedFeatures.of("patch_cactus");
   public static final RegistryKey FLOWER_PLAIN = PlacedFeatures.of("flower_plain");
   public static final RegistryKey PATCH_TAIGA_GRASS = PlacedFeatures.of("patch_taiga_grass");
   public static final RegistryKey PATCH_BERRY_BUSH = PlacedFeatures.of("patch_berry_bush");

   public static void bootstrap(Registerable featureRegisterable) {
      RegistryEntryLookup registryEntryLookup = featureRegisterable.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);
      RegistryEntry registryEntry = registryEntryLookup.getOrThrow(PileConfiguredFeatures.PILE_HAY);
      RegistryEntry registryEntry2 = registryEntryLookup.getOrThrow(PileConfiguredFeatures.PILE_MELON);
      RegistryEntry registryEntry3 = registryEntryLookup.getOrThrow(PileConfiguredFeatures.PILE_SNOW);
      RegistryEntry registryEntry4 = registryEntryLookup.getOrThrow(PileConfiguredFeatures.PILE_ICE);
      RegistryEntry registryEntry5 = registryEntryLookup.getOrThrow(PileConfiguredFeatures.PILE_PUMPKIN);
      RegistryEntry registryEntry6 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.OAK);
      RegistryEntry registryEntry7 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.ACACIA);
      RegistryEntry registryEntry8 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.SPRUCE);
      RegistryEntry registryEntry9 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.PINE);
      RegistryEntry registryEntry10 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.PATCH_CACTUS);
      RegistryEntry registryEntry11 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.FLOWER_PLAIN);
      RegistryEntry registryEntry12 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.PATCH_TAIGA_GRASS);
      RegistryEntry registryEntry13 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.PATCH_BERRY_BUSH);
      PlacedFeatures.register(featureRegisterable, PILE_HAY, registryEntry, (PlacementModifier[])());
      PlacedFeatures.register(featureRegisterable, PILE_MELON, registryEntry2, (PlacementModifier[])());
      PlacedFeatures.register(featureRegisterable, PILE_SNOW, registryEntry3, (PlacementModifier[])());
      PlacedFeatures.register(featureRegisterable, PILE_ICE, registryEntry4, (PlacementModifier[])());
      PlacedFeatures.register(featureRegisterable, PILE_PUMPKIN, registryEntry5, (PlacementModifier[])());
      PlacedFeatures.register(featureRegisterable, OAK, registryEntry6, (PlacementModifier[])(PlacedFeatures.wouldSurvive(Blocks.OAK_SAPLING)));
      PlacedFeatures.register(featureRegisterable, ACACIA, registryEntry7, (PlacementModifier[])(PlacedFeatures.wouldSurvive(Blocks.ACACIA_SAPLING)));
      PlacedFeatures.register(featureRegisterable, SPRUCE, registryEntry8, (PlacementModifier[])(PlacedFeatures.wouldSurvive(Blocks.SPRUCE_SAPLING)));
      PlacedFeatures.register(featureRegisterable, PINE, registryEntry9, (PlacementModifier[])(PlacedFeatures.wouldSurvive(Blocks.SPRUCE_SAPLING)));
      PlacedFeatures.register(featureRegisterable, PATCH_CACTUS, registryEntry10, (PlacementModifier[])());
      PlacedFeatures.register(featureRegisterable, FLOWER_PLAIN, registryEntry11, (PlacementModifier[])());
      PlacedFeatures.register(featureRegisterable, PATCH_TAIGA_GRASS, registryEntry12, (PlacementModifier[])());
      PlacedFeatures.register(featureRegisterable, PATCH_BERRY_BUSH, registryEntry13, (PlacementModifier[])());
   }
}
