package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.Codecs;

public record TwistingVinesFeatureConfig(int spreadWidth, int spreadHeight, int maxHeight) implements FeatureConfig {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Codecs.POSITIVE_INT.fieldOf("spread_width").forGetter(TwistingVinesFeatureConfig::spreadWidth), Codecs.POSITIVE_INT.fieldOf("spread_height").forGetter(TwistingVinesFeatureConfig::spreadHeight), Codecs.POSITIVE_INT.fieldOf("max_height").forGetter(TwistingVinesFeatureConfig::maxHeight)).apply(instance, TwistingVinesFeatureConfig::new);
   });

   public TwistingVinesFeatureConfig(int i, int j, int k) {
      this.spreadWidth = i;
      this.spreadHeight = j;
      this.maxHeight = k;
   }

   public int spreadWidth() {
      return this.spreadWidth;
   }

   public int spreadHeight() {
      return this.spreadHeight;
   }

   public int maxHeight() {
      return this.maxHeight;
   }
}
