package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class GeodeLayerThicknessConfig {
   private static final Codec RANGE = Codec.doubleRange(0.01, 50.0);
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(RANGE.fieldOf("filling").orElse(1.7).forGetter((config) -> {
         return config.filling;
      }), RANGE.fieldOf("inner_layer").orElse(2.2).forGetter((config) -> {
         return config.innerLayer;
      }), RANGE.fieldOf("middle_layer").orElse(3.2).forGetter((config) -> {
         return config.middleLayer;
      }), RANGE.fieldOf("outer_layer").orElse(4.2).forGetter((config) -> {
         return config.outerLayer;
      })).apply(instance, GeodeLayerThicknessConfig::new);
   });
   public final double filling;
   public final double innerLayer;
   public final double middleLayer;
   public final double outerLayer;

   public GeodeLayerThicknessConfig(double filling, double innerLayer, double middleLayer, double outerLayer) {
      this.filling = filling;
      this.innerLayer = innerLayer;
      this.middleLayer = middleLayer;
      this.outerLayer = outerLayer;
   }
}
