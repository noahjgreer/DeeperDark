package net.minecraft.world.gen.feature;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.WeightedListIntProvider;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.placementmodifier.AbstractConditionalPlacementModifier;
import net.minecraft.world.gen.placementmodifier.BlockFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightmapPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;

public class PlacedFeatures {
   public static final PlacementModifier MOTION_BLOCKING_HEIGHTMAP;
   public static final PlacementModifier MOTION_BLOCKING_NO_LEAVES_HEIGHTMAP;
   public static final PlacementModifier OCEAN_FLOOR_WG_HEIGHTMAP;
   public static final PlacementModifier WORLD_SURFACE_WG_HEIGHTMAP;
   public static final PlacementModifier OCEAN_FLOOR_HEIGHTMAP;
   public static final PlacementModifier BOTTOM_TO_TOP_RANGE;
   public static final PlacementModifier TEN_ABOVE_AND_BELOW_RANGE;
   public static final PlacementModifier EIGHT_ABOVE_AND_BELOW_RANGE;
   public static final PlacementModifier FOUR_ABOVE_AND_BELOW_RANGE;
   public static final PlacementModifier BOTTOM_TO_120_RANGE;

   public static void bootstrap(Registerable featureRegisterable) {
      OceanPlacedFeatures.bootstrap(featureRegisterable);
      UndergroundPlacedFeatures.bootstrap(featureRegisterable);
      EndPlacedFeatures.bootstrap(featureRegisterable);
      MiscPlacedFeatures.bootstrap(featureRegisterable);
      NetherPlacedFeatures.bootstrap(featureRegisterable);
      OrePlacedFeatures.bootstrap(featureRegisterable);
      TreePlacedFeatures.bootstrap(featureRegisterable);
      VegetationPlacedFeatures.bootstrap(featureRegisterable);
      VillagePlacedFeatures.bootstrap(featureRegisterable);
   }

   public static RegistryKey of(String id) {
      return RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.ofVanilla(id));
   }

   public static void register(Registerable featureRegisterable, RegistryKey key, RegistryEntry feature, List modifiers) {
      featureRegisterable.register(key, new PlacedFeature(feature, List.copyOf(modifiers)));
   }

   public static void register(Registerable featureRegisterable, RegistryKey key, RegistryEntry feature, PlacementModifier... modifiers) {
      register(featureRegisterable, key, feature, List.of(modifiers));
   }

   public static PlacementModifier createCountExtraModifier(int count, float extraChance, int extraCount) {
      float f = 1.0F / extraChance;
      if (Math.abs(f - (float)((int)f)) > 1.0E-5F) {
         throw new IllegalStateException("Chance data cannot be represented as list weight");
      } else {
         Pool pool = Pool.builder().add(ConstantIntProvider.create(count), (int)f - 1).add(ConstantIntProvider.create(count + extraCount), 1).build();
         return CountPlacementModifier.of(new WeightedListIntProvider(pool));
      }
   }

   public static AbstractConditionalPlacementModifier isAir() {
      return BlockFilterPlacementModifier.of(BlockPredicate.IS_AIR);
   }

   public static BlockFilterPlacementModifier wouldSurvive(Block block) {
      return BlockFilterPlacementModifier.of(BlockPredicate.wouldSurvive(block.getDefaultState(), BlockPos.ORIGIN));
   }

   public static RegistryEntry createEntry(RegistryEntry feature, PlacementModifier... modifiers) {
      return RegistryEntry.of(new PlacedFeature(feature, List.of(modifiers)));
   }

   public static RegistryEntry createEntry(Feature feature, FeatureConfig featureConfig, PlacementModifier... modifiers) {
      return createEntry(RegistryEntry.of(new ConfiguredFeature(feature, featureConfig)), modifiers);
   }

   public static RegistryEntry createEntry(Feature feature, FeatureConfig featureConfig) {
      return createEntry(feature, featureConfig, BlockPredicate.IS_AIR);
   }

   public static RegistryEntry createEntry(Feature feature, FeatureConfig featureConfig, BlockPredicate predicate) {
      return createEntry(feature, featureConfig, BlockFilterPlacementModifier.of(predicate));
   }

   static {
      MOTION_BLOCKING_HEIGHTMAP = HeightmapPlacementModifier.of(Heightmap.Type.MOTION_BLOCKING);
      MOTION_BLOCKING_NO_LEAVES_HEIGHTMAP = HeightmapPlacementModifier.of(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      OCEAN_FLOOR_WG_HEIGHTMAP = HeightmapPlacementModifier.of(Heightmap.Type.OCEAN_FLOOR_WG);
      WORLD_SURFACE_WG_HEIGHTMAP = HeightmapPlacementModifier.of(Heightmap.Type.WORLD_SURFACE_WG);
      OCEAN_FLOOR_HEIGHTMAP = HeightmapPlacementModifier.of(Heightmap.Type.OCEAN_FLOOR);
      BOTTOM_TO_TOP_RANGE = HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.getTop());
      TEN_ABOVE_AND_BELOW_RANGE = HeightRangePlacementModifier.uniform(YOffset.aboveBottom(10), YOffset.belowTop(10));
      EIGHT_ABOVE_AND_BELOW_RANGE = HeightRangePlacementModifier.uniform(YOffset.aboveBottom(8), YOffset.belowTop(8));
      FOUR_ABOVE_AND_BELOW_RANGE = HeightRangePlacementModifier.uniform(YOffset.aboveBottom(4), YOffset.belowTop(4));
      BOTTOM_TO_120_RANGE = HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(256));
   }
}
