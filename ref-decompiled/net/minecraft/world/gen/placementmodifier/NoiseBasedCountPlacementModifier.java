package net.minecraft.world.gen.placementmodifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;

public class NoiseBasedCountPlacementModifier extends AbstractCountPlacementModifier {
   public static final MapCodec MODIFIER_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codec.INT.fieldOf("noise_to_count_ratio").forGetter((placementModifier) -> {
         return placementModifier.noiseToCountRatio;
      }), Codec.DOUBLE.fieldOf("noise_factor").forGetter((placementModifier) -> {
         return placementModifier.noiseFactor;
      }), Codec.DOUBLE.fieldOf("noise_offset").orElse(0.0).forGetter((placementModifier) -> {
         return placementModifier.noiseOffset;
      })).apply(instance, NoiseBasedCountPlacementModifier::new);
   });
   private final int noiseToCountRatio;
   private final double noiseFactor;
   private final double noiseOffset;

   private NoiseBasedCountPlacementModifier(int noiseToCountRatio, double noiseFactor, double noiseOffset) {
      this.noiseToCountRatio = noiseToCountRatio;
      this.noiseFactor = noiseFactor;
      this.noiseOffset = noiseOffset;
   }

   public static NoiseBasedCountPlacementModifier of(int noiseToCountRatio, double noiseFactor, double noiseOffset) {
      return new NoiseBasedCountPlacementModifier(noiseToCountRatio, noiseFactor, noiseOffset);
   }

   protected int getCount(Random random, BlockPos pos) {
      double d = Biome.FOLIAGE_NOISE.sample((double)pos.getX() / this.noiseFactor, (double)pos.getZ() / this.noiseFactor, false);
      return (int)Math.ceil((d + this.noiseOffset) * (double)this.noiseToCountRatio);
   }

   public PlacementModifierType getType() {
      return PlacementModifierType.NOISE_BASED_COUNT;
   }
}
