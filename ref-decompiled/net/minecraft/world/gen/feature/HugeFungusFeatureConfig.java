package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;

public class HugeFungusFeatureConfig implements FeatureConfig {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(BlockState.CODEC.fieldOf("valid_base_block").forGetter((config) -> {
         return config.validBaseBlock;
      }), BlockState.CODEC.fieldOf("stem_state").forGetter((config) -> {
         return config.stemState;
      }), BlockState.CODEC.fieldOf("hat_state").forGetter((config) -> {
         return config.hatState;
      }), BlockState.CODEC.fieldOf("decor_state").forGetter((config) -> {
         return config.decorationState;
      }), BlockPredicate.BASE_CODEC.fieldOf("replaceable_blocks").forGetter((config) -> {
         return config.replaceableBlocks;
      }), Codec.BOOL.fieldOf("planted").orElse(false).forGetter((config) -> {
         return config.planted;
      })).apply(instance, HugeFungusFeatureConfig::new);
   });
   public final BlockState validBaseBlock;
   public final BlockState stemState;
   public final BlockState hatState;
   public final BlockState decorationState;
   public final BlockPredicate replaceableBlocks;
   public final boolean planted;

   public HugeFungusFeatureConfig(BlockState validBaseBlock, BlockState stemState, BlockState hatState, BlockState decorationState, BlockPredicate replaceableBlocks, boolean planted) {
      this.validBaseBlock = validBaseBlock;
      this.stemState = stemState;
      this.hatState = hatState;
      this.decorationState = decorationState;
      this.replaceableBlocks = replaceableBlocks;
      this.planted = planted;
   }
}
