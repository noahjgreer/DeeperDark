package net.minecraft.world.gen.feature;

import java.util.List;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;

public class ConfiguredFeatures {
   public static void bootstrap(Registerable featureRegisterable) {
      OceanConfiguredFeatures.bootstrap(featureRegisterable);
      UndergroundConfiguredFeatures.bootstrap(featureRegisterable);
      EndConfiguredFeatures.bootstrap(featureRegisterable);
      MiscConfiguredFeatures.bootstrap(featureRegisterable);
      NetherConfiguredFeatures.bootstrap(featureRegisterable);
      OreConfiguredFeatures.bootstrap(featureRegisterable);
      PileConfiguredFeatures.bootstrap(featureRegisterable);
      TreeConfiguredFeatures.bootstrap(featureRegisterable);
      VegetationConfiguredFeatures.bootstrap(featureRegisterable);
   }

   private static BlockPredicate createBlockPredicate(List validGround) {
      BlockPredicate blockPredicate;
      if (!validGround.isEmpty()) {
         blockPredicate = BlockPredicate.bothOf(BlockPredicate.IS_AIR, BlockPredicate.matchingBlocks(Direction.DOWN.getVector(), validGround));
      } else {
         blockPredicate = BlockPredicate.IS_AIR;
      }

      return blockPredicate;
   }

   public static RandomPatchFeatureConfig createRandomPatchFeatureConfig(int tries, RegistryEntry feature) {
      return new RandomPatchFeatureConfig(tries, 7, 3, feature);
   }

   public static RandomPatchFeatureConfig createRandomPatchFeatureConfig(Feature feature, FeatureConfig config, List predicateBlocks, int tries) {
      return createRandomPatchFeatureConfig(tries, PlacedFeatures.createEntry(feature, config, createBlockPredicate(predicateBlocks)));
   }

   public static RandomPatchFeatureConfig createRandomPatchFeatureConfig(Feature feature, FeatureConfig config, List predicateBlocks) {
      return createRandomPatchFeatureConfig(feature, config, predicateBlocks, 96);
   }

   public static RandomPatchFeatureConfig createRandomPatchFeatureConfig(Feature feature, FeatureConfig config) {
      return createRandomPatchFeatureConfig(feature, config, List.of(), 96);
   }

   public static RegistryKey of(String id) {
      return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Identifier.ofVanilla(id));
   }

   public static void register(Registerable registerable, RegistryKey key, Feature feature) {
      register(registerable, key, feature, FeatureConfig.DEFAULT);
   }

   public static void register(Registerable registerable, RegistryKey key, Feature feature, FeatureConfig config) {
      registerable.register(key, new ConfiguredFeature(feature, config));
   }
}
