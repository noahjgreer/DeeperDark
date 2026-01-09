package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.registry.entry.RegistryEntry;

public class RandomFeatureConfig implements FeatureConfig {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.apply2(RandomFeatureConfig::new, RandomFeatureEntry.CODEC.listOf().fieldOf("features").forGetter((config) -> {
         return config.features;
      }), PlacedFeature.REGISTRY_CODEC.fieldOf("default").forGetter((config) -> {
         return config.defaultFeature;
      }));
   });
   public final List features;
   public final RegistryEntry defaultFeature;

   public RandomFeatureConfig(List features, RegistryEntry defaultFeature) {
      this.features = features;
      this.defaultFeature = defaultFeature;
   }

   public Stream getDecoratedFeatures() {
      return Stream.concat(this.features.stream().flatMap((entry) -> {
         return ((PlacedFeature)entry.feature.value()).getDecoratedFeatures();
      }), ((PlacedFeature)this.defaultFeature.value()).getDecoratedFeatures());
   }
}
