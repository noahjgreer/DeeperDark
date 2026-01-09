package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.HeightProvider;

public class RavineCarverConfig extends CarverConfig {
   public static final Codec RAVINE_CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(CarverConfig.CONFIG_CODEC.forGetter((ravineCarverConfig) -> {
         return ravineCarverConfig;
      }), FloatProvider.VALUE_CODEC.fieldOf("vertical_rotation").forGetter((config) -> {
         return config.verticalRotation;
      }), RavineCarverConfig.Shape.CODEC.fieldOf("shape").forGetter((config) -> {
         return config.shape;
      })).apply(instance, RavineCarverConfig::new);
   });
   public final FloatProvider verticalRotation;
   public final Shape shape;

   public RavineCarverConfig(float probability, HeightProvider y, FloatProvider yScale, YOffset lavaLevel, CarverDebugConfig debugConfig, RegistryEntryList replaceable, FloatProvider verticalRotation, Shape shape) {
      super(probability, y, yScale, lavaLevel, debugConfig, replaceable);
      this.verticalRotation = verticalRotation;
      this.shape = shape;
   }

   public RavineCarverConfig(CarverConfig config, FloatProvider verticalRotation, Shape shape) {
      this(config.probability, config.y, config.yScale, config.lavaLevel, config.debugConfig, config.replaceable, verticalRotation, shape);
   }

   public static class Shape {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(FloatProvider.VALUE_CODEC.fieldOf("distance_factor").forGetter((shape) -> {
            return shape.distanceFactor;
         }), FloatProvider.VALUE_CODEC.fieldOf("thickness").forGetter((shape) -> {
            return shape.thickness;
         }), Codecs.NON_NEGATIVE_INT.fieldOf("width_smoothness").forGetter((shape) -> {
            return shape.widthSmoothness;
         }), FloatProvider.VALUE_CODEC.fieldOf("horizontal_radius_factor").forGetter((shape) -> {
            return shape.horizontalRadiusFactor;
         }), Codec.FLOAT.fieldOf("vertical_radius_default_factor").forGetter((shape) -> {
            return shape.verticalRadiusDefaultFactor;
         }), Codec.FLOAT.fieldOf("vertical_radius_center_factor").forGetter((shape) -> {
            return shape.verticalRadiusCenterFactor;
         })).apply(instance, Shape::new);
      });
      public final FloatProvider distanceFactor;
      public final FloatProvider thickness;
      public final int widthSmoothness;
      public final FloatProvider horizontalRadiusFactor;
      public final float verticalRadiusDefaultFactor;
      public final float verticalRadiusCenterFactor;

      public Shape(FloatProvider distanceFactor, FloatProvider thickness, int widthSmoothness, FloatProvider horizontalRadiusFactor, float verticalRadiusDefaultFactor, float verticalRadiusCenterFactor) {
         this.widthSmoothness = widthSmoothness;
         this.horizontalRadiusFactor = horizontalRadiusFactor;
         this.verticalRadiusDefaultFactor = verticalRadiusDefaultFactor;
         this.verticalRadiusCenterFactor = verticalRadiusCenterFactor;
         this.distanceFactor = distanceFactor;
         this.thickness = thickness;
      }
   }
}
