package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.dynamic.Codecs;

public record RandomPatchFeatureConfig(int tries, int xzSpread, int ySpread, RegistryEntry feature) implements FeatureConfig {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Codecs.POSITIVE_INT.fieldOf("tries").orElse(128).forGetter(RandomPatchFeatureConfig::tries), Codecs.NON_NEGATIVE_INT.fieldOf("xz_spread").orElse(7).forGetter(RandomPatchFeatureConfig::xzSpread), Codecs.NON_NEGATIVE_INT.fieldOf("y_spread").orElse(3).forGetter(RandomPatchFeatureConfig::ySpread), PlacedFeature.REGISTRY_CODEC.fieldOf("feature").forGetter(RandomPatchFeatureConfig::feature)).apply(instance, RandomPatchFeatureConfig::new);
   });

   public RandomPatchFeatureConfig(int i, int j, int k, RegistryEntry registryEntry) {
      this.tries = i;
      this.xzSpread = j;
      this.ySpread = k;
      this.feature = registryEntry;
   }

   public int tries() {
      return this.tries;
   }

   public int xzSpread() {
      return this.xzSpread;
   }

   public int ySpread() {
      return this.ySpread;
   }

   public RegistryEntry feature() {
      return this.feature;
   }
}
