package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.intprovider.IntProvider;

public class DeltaFeatureConfig implements FeatureConfig {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(BlockState.CODEC.fieldOf("contents").forGetter((config) -> {
         return config.contents;
      }), BlockState.CODEC.fieldOf("rim").forGetter((config) -> {
         return config.rim;
      }), IntProvider.createValidatingCodec(0, 16).fieldOf("size").forGetter((config) -> {
         return config.size;
      }), IntProvider.createValidatingCodec(0, 16).fieldOf("rim_size").forGetter((config) -> {
         return config.rimSize;
      })).apply(instance, DeltaFeatureConfig::new);
   });
   private final BlockState contents;
   private final BlockState rim;
   private final IntProvider size;
   private final IntProvider rimSize;

   public DeltaFeatureConfig(BlockState contents, BlockState rim, IntProvider size, IntProvider rimSize) {
      this.contents = contents;
      this.rim = rim;
      this.size = size;
      this.rimSize = rimSize;
   }

   public BlockState getContents() {
      return this.contents;
   }

   public BlockState getRim() {
      return this.rim;
   }

   public IntProvider getSize() {
      return this.size;
   }

   public IntProvider getRimSize() {
      return this.rimSize;
   }
}
