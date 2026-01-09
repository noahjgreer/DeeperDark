package net.minecraft.util.math.intprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

public class ClampedIntProvider extends IntProvider {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(IntProvider.VALUE_CODEC.fieldOf("source").forGetter((provider) -> {
         return provider.source;
      }), Codec.INT.fieldOf("min_inclusive").forGetter((provider) -> {
         return provider.min;
      }), Codec.INT.fieldOf("max_inclusive").forGetter((provider) -> {
         return provider.max;
      })).apply(instance, ClampedIntProvider::new);
   }).validate((provider) -> {
      return provider.max < provider.min ? DataResult.error(() -> {
         return "Max must be at least min, min_inclusive: " + provider.min + ", max_inclusive: " + provider.max;
      }) : DataResult.success(provider);
   });
   private final IntProvider source;
   private final int min;
   private final int max;

   public static ClampedIntProvider create(IntProvider source, int min, int max) {
      return new ClampedIntProvider(source, min, max);
   }

   public ClampedIntProvider(IntProvider source, int min, int max) {
      this.source = source;
      this.min = min;
      this.max = max;
   }

   public int get(Random random) {
      return MathHelper.clamp(this.source.get(random), this.min, this.max);
   }

   public int getMin() {
      return Math.max(this.min, this.source.getMin());
   }

   public int getMax() {
      return Math.min(this.max, this.source.getMax());
   }

   public IntProviderType getType() {
      return IntProviderType.CLAMPED;
   }
}
