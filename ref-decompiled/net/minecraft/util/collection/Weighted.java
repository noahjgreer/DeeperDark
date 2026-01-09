package net.minecraft.util.collection;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import org.slf4j.Logger;

public record Weighted(Object value, int weight) {
   private static final Logger LOGGER = LogUtils.getLogger();

   public Weighted(Object value, int weight) {
      if (weight < 0) {
         throw (IllegalArgumentException)Util.getFatalOrPause(new IllegalArgumentException("Weight should be >= 0"));
      } else {
         if (weight == 0 && SharedConstants.isDevelopment) {
            LOGGER.warn("Found 0 weight, make sure this is intentional!");
         }

         this.value = value;
         this.weight = weight;
      }
   }

   public static Codec createCodec(Codec dataCodec) {
      return createCodec(dataCodec.fieldOf("data"));
   }

   public static Codec createCodec(MapCodec dataCodec) {
      return RecordCodecBuilder.create((instance) -> {
         return instance.group(dataCodec.forGetter(Weighted::value), Codecs.NON_NEGATIVE_INT.fieldOf("weight").forGetter(Weighted::weight)).apply(instance, Weighted::new);
      });
   }

   public Weighted transform(Function function) {
      return new Weighted(function.apply(this.value()), this.weight);
   }

   public Object value() {
      return this.value;
   }

   public int weight() {
      return this.weight;
   }
}
