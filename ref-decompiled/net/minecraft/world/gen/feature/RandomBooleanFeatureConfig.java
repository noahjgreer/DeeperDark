package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.registry.entry.RegistryEntry;

public class RandomBooleanFeatureConfig implements FeatureConfig {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(PlacedFeature.REGISTRY_CODEC.fieldOf("feature_true").forGetter((config) -> {
         return config.featureTrue;
      }), PlacedFeature.REGISTRY_CODEC.fieldOf("feature_false").forGetter((config) -> {
         return config.featureFalse;
      })).apply(instance, RandomBooleanFeatureConfig::new);
   });
   public final RegistryEntry featureTrue;
   public final RegistryEntry featureFalse;

   public RandomBooleanFeatureConfig(RegistryEntry featureTrue, RegistryEntry featureFalse) {
      this.featureTrue = featureTrue;
      this.featureFalse = featureFalse;
   }

   public Stream getDecoratedFeatures() {
      return Stream.concat(((PlacedFeature)this.featureTrue.value()).getDecoratedFeatures(), ((PlacedFeature)this.featureFalse.value()).getDecoratedFeatures());
   }
}
