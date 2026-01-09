package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.HeightProvider;

public class CarverConfig extends ProbabilityConfig {
   public static final MapCodec CONFIG_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter((config) -> {
         return config.probability;
      }), HeightProvider.CODEC.fieldOf("y").forGetter((config) -> {
         return config.y;
      }), FloatProvider.VALUE_CODEC.fieldOf("yScale").forGetter((config) -> {
         return config.yScale;
      }), YOffset.OFFSET_CODEC.fieldOf("lava_level").forGetter((config) -> {
         return config.lavaLevel;
      }), CarverDebugConfig.CODEC.optionalFieldOf("debug_settings", CarverDebugConfig.DEFAULT).forGetter((config) -> {
         return config.debugConfig;
      }), RegistryCodecs.entryList(RegistryKeys.BLOCK).fieldOf("replaceable").forGetter((config) -> {
         return config.replaceable;
      })).apply(instance, CarverConfig::new);
   });
   public final HeightProvider y;
   public final FloatProvider yScale;
   public final YOffset lavaLevel;
   public final CarverDebugConfig debugConfig;
   public final RegistryEntryList replaceable;

   public CarverConfig(float probability, HeightProvider y, FloatProvider yScale, YOffset lavaLevel, CarverDebugConfig debugConfig, RegistryEntryList replaceable) {
      super(probability);
      this.y = y;
      this.yScale = yScale;
      this.lavaLevel = lavaLevel;
      this.debugConfig = debugConfig;
      this.replaceable = replaceable;
   }
}
