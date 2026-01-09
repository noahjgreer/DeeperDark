package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.stream.Stream;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.dynamic.Codecs;

public class SimpleRandomFeatureConfig implements FeatureConfig {
   public static final Codec CODEC;
   public final RegistryEntryList features;

   public SimpleRandomFeatureConfig(RegistryEntryList features) {
      this.features = features;
   }

   public Stream getDecoratedFeatures() {
      return this.features.stream().flatMap((feature) -> {
         return ((PlacedFeature)feature.value()).getDecoratedFeatures();
      });
   }

   static {
      CODEC = Codecs.nonEmptyEntryList(PlacedFeature.LIST_CODEC).fieldOf("features").xmap(SimpleRandomFeatureConfig::new, (config) -> {
         return config.features;
      }).codec();
   }
}
