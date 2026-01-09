package net.minecraft.world.gen.feature;

import java.util.stream.Stream;

public interface FeatureConfig {
   DefaultFeatureConfig DEFAULT = DefaultFeatureConfig.INSTANCE;

   default Stream getDecoratedFeatures() {
      return Stream.empty();
   }
}
