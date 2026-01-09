package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.stateprovider.PredicatedStateProvider;

public record DiskFeatureConfig(PredicatedStateProvider stateProvider, BlockPredicate target, IntProvider radius, int halfHeight) implements FeatureConfig {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(PredicatedStateProvider.CODEC.fieldOf("state_provider").forGetter(DiskFeatureConfig::stateProvider), BlockPredicate.BASE_CODEC.fieldOf("target").forGetter(DiskFeatureConfig::target), IntProvider.createValidatingCodec(0, 8).fieldOf("radius").forGetter(DiskFeatureConfig::radius), Codec.intRange(0, 4).fieldOf("half_height").forGetter(DiskFeatureConfig::halfHeight)).apply(instance, DiskFeatureConfig::new);
   });

   public DiskFeatureConfig(PredicatedStateProvider predicatedStateProvider, BlockPredicate blockPredicate, IntProvider intProvider, int i) {
      this.stateProvider = predicatedStateProvider;
      this.target = blockPredicate;
      this.radius = intProvider;
      this.halfHeight = i;
   }

   public PredicatedStateProvider stateProvider() {
      return this.stateProvider;
   }

   public BlockPredicate target() {
      return this.target;
   }

   public IntProvider radius() {
      return this.radius;
   }

   public int halfHeight() {
      return this.halfHeight;
   }
}
