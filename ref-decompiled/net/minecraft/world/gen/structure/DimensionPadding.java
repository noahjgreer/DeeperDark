package net.minecraft.world.gen.structure;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.util.dynamic.Codecs;

public record DimensionPadding(int bottom, int top) {
   private static final Codec OBJECT_CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Codecs.NON_NEGATIVE_INT.lenientOptionalFieldOf("bottom", 0).forGetter((padding) -> {
         return padding.bottom;
      }), Codecs.NON_NEGATIVE_INT.lenientOptionalFieldOf("top", 0).forGetter((padding) -> {
         return padding.top;
      })).apply(instance, DimensionPadding::new);
   });
   public static final Codec CODEC;
   public static final DimensionPadding NONE;

   public DimensionPadding(int value) {
      this(value, value);
   }

   public DimensionPadding(int i, int j) {
      this.bottom = i;
      this.top = j;
   }

   public boolean paddedBySameDistance() {
      return this.top == this.bottom;
   }

   public int bottom() {
      return this.bottom;
   }

   public int top() {
      return this.top;
   }

   static {
      CODEC = Codec.either(Codecs.NON_NEGATIVE_INT, OBJECT_CODEC).xmap((either) -> {
         return (DimensionPadding)either.map(DimensionPadding::new, Function.identity());
      }, (padding) -> {
         return padding.paddedBySameDistance() ? Either.left(padding.bottom) : Either.right(padding);
      });
      NONE = new DimensionPadding(0);
   }
}
