package net.minecraft.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public record LightPredicate(NumberRange.IntRange range) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(NumberRange.IntRange.CODEC.optionalFieldOf("light", NumberRange.IntRange.ANY).forGetter(LightPredicate::range)).apply(instance, LightPredicate::new);
   });

   public LightPredicate(NumberRange.IntRange range) {
      this.range = range;
   }

   public boolean test(ServerWorld world, BlockPos pos) {
      if (!world.isPosLoaded(pos)) {
         return false;
      } else {
         return this.range.test(world.getLightLevel(pos));
      }
   }

   public NumberRange.IntRange range() {
      return this.range;
   }

   public static class Builder {
      private NumberRange.IntRange light;

      public Builder() {
         this.light = NumberRange.IntRange.ANY;
      }

      public static Builder create() {
         return new Builder();
      }

      public Builder light(NumberRange.IntRange light) {
         this.light = light;
         return this;
      }

      public LightPredicate build() {
         return new LightPredicate(this.light);
      }
   }
}
