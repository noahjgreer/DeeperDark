package net.minecraft.util.math.floatprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

public class UniformFloatProvider extends FloatProvider {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codec.FLOAT.fieldOf("min_inclusive").forGetter((provider) -> {
         return provider.min;
      }), Codec.FLOAT.fieldOf("max_exclusive").forGetter((provider) -> {
         return provider.max;
      })).apply(instance, UniformFloatProvider::new);
   }).validate((provider) -> {
      return provider.max <= provider.min ? DataResult.error(() -> {
         return "Max must be larger than min, min_inclusive: " + provider.min + ", max_exclusive: " + provider.max;
      }) : DataResult.success(provider);
   });
   private final float min;
   private final float max;

   private UniformFloatProvider(float min, float max) {
      this.min = min;
      this.max = max;
   }

   public static UniformFloatProvider create(float min, float max) {
      if (max <= min) {
         throw new IllegalArgumentException("Max must exceed min");
      } else {
         return new UniformFloatProvider(min, max);
      }
   }

   public float get(Random random) {
      return MathHelper.nextBetween(random, this.min, this.max);
   }

   public float getMin() {
      return this.min;
   }

   public float getMax() {
      return this.max;
   }

   public FloatProviderType getType() {
      return FloatProviderType.UNIFORM;
   }

   public String toString() {
      return "[" + this.min + "-" + this.max + "]";
   }
}
