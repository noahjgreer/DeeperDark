package net.minecraft.util.math.floatprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

public class ClampedNormalFloatProvider extends FloatProvider {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codec.FLOAT.fieldOf("mean").forGetter((provider) -> {
         return provider.mean;
      }), Codec.FLOAT.fieldOf("deviation").forGetter((provider) -> {
         return provider.deviation;
      }), Codec.FLOAT.fieldOf("min").forGetter((provider) -> {
         return provider.min;
      }), Codec.FLOAT.fieldOf("max").forGetter((provider) -> {
         return provider.max;
      })).apply(instance, ClampedNormalFloatProvider::new);
   }).validate((provider) -> {
      return provider.max < provider.min ? DataResult.error(() -> {
         return "Max must be larger than min: [" + provider.min + ", " + provider.max + "]";
      }) : DataResult.success(provider);
   });
   private final float mean;
   private final float deviation;
   private final float min;
   private final float max;

   public static ClampedNormalFloatProvider create(float mean, float deviation, float min, float max) {
      return new ClampedNormalFloatProvider(mean, deviation, min, max);
   }

   private ClampedNormalFloatProvider(float mean, float deviation, float min, float max) {
      this.mean = mean;
      this.deviation = deviation;
      this.min = min;
      this.max = max;
   }

   public float get(Random random) {
      return get(random, this.mean, this.deviation, this.min, this.max);
   }

   public static float get(Random random, float mean, float deviation, float min, float max) {
      return MathHelper.clamp(MathHelper.nextGaussian(random, mean, deviation), min, max);
   }

   public float getMin() {
      return this.min;
   }

   public float getMax() {
      return this.max;
   }

   public FloatProviderType getType() {
      return FloatProviderType.CLAMPED_NORMAL;
   }

   public String toString() {
      return "normal(" + this.mean + ", " + this.deviation + ") in [" + this.min + "-" + this.max + "]";
   }
}
