package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public record SimpleBlockFeatureConfig(BlockStateProvider toPlace, boolean scheduleTick) implements FeatureConfig {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(BlockStateProvider.TYPE_CODEC.fieldOf("to_place").forGetter((config) -> {
         return config.toPlace;
      }), Codec.BOOL.optionalFieldOf("schedule_tick", false).forGetter((simpleBlockFeatureConfig) -> {
         return simpleBlockFeatureConfig.scheduleTick;
      })).apply(instance, SimpleBlockFeatureConfig::new);
   });

   public SimpleBlockFeatureConfig(BlockStateProvider toPlace) {
      this(toPlace, false);
   }

   public SimpleBlockFeatureConfig(BlockStateProvider blockStateProvider, boolean bl) {
      this.toPlace = blockStateProvider;
      this.scheduleTick = bl;
   }

   public BlockStateProvider toPlace() {
      return this.toPlace;
   }

   public boolean scheduleTick() {
      return this.scheduleTick;
   }
}
