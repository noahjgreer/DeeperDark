package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.treedecorator.TreeDecorator;

public class FallenTreeFeatureConfig implements FeatureConfig {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(BlockStateProvider.TYPE_CODEC.fieldOf("trunk_provider").forGetter((featureConfig) -> {
         return featureConfig.trunkProvider;
      }), IntProvider.createValidatingCodec(0, 16).fieldOf("log_length").forGetter((featureConfig) -> {
         return featureConfig.logLength;
      }), TreeDecorator.TYPE_CODEC.listOf().fieldOf("stump_decorators").forGetter((featureConfig) -> {
         return featureConfig.stumpDecorators;
      }), TreeDecorator.TYPE_CODEC.listOf().fieldOf("log_decorators").forGetter((featureConfig) -> {
         return featureConfig.logDecorators;
      })).apply(instance, FallenTreeFeatureConfig::new);
   });
   public final BlockStateProvider trunkProvider;
   public final IntProvider logLength;
   public final List stumpDecorators;
   public final List logDecorators;

   protected FallenTreeFeatureConfig(BlockStateProvider trunkProvider, IntProvider logLength, List stumpDecorators, List logDecorators) {
      this.trunkProvider = trunkProvider;
      this.logLength = logLength;
      this.stumpDecorators = stumpDecorators;
      this.logDecorators = logDecorators;
   }

   public static class Builder {
      private final BlockStateProvider trunkProvider;
      private final IntProvider logLength;
      private List stumpDecorators = new ArrayList();
      private List logDecorators = new ArrayList();

      public Builder(BlockStateProvider trunkProvider, IntProvider logLength) {
         this.trunkProvider = trunkProvider;
         this.logLength = logLength;
      }

      public Builder stumpDecorators(List stumpDecorators) {
         this.stumpDecorators = stumpDecorators;
         return this;
      }

      public Builder logDecorators(List logDecorators) {
         this.logDecorators = logDecorators;
         return this;
      }

      public FallenTreeFeatureConfig build() {
         return new FallenTreeFeatureConfig(this.trunkProvider, this.logLength, this.stumpDecorators, this.logDecorators);
      }
   }
}
