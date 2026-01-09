package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class GeodeLayerConfig {
   public final BlockStateProvider fillingProvider;
   public final BlockStateProvider innerLayerProvider;
   public final BlockStateProvider alternateInnerLayerProvider;
   public final BlockStateProvider middleLayerProvider;
   public final BlockStateProvider outerLayerProvider;
   public final List innerBlocks;
   public final TagKey cannotReplace;
   public final TagKey invalidBlocks;
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(BlockStateProvider.TYPE_CODEC.fieldOf("filling_provider").forGetter((config) -> {
         return config.fillingProvider;
      }), BlockStateProvider.TYPE_CODEC.fieldOf("inner_layer_provider").forGetter((config) -> {
         return config.innerLayerProvider;
      }), BlockStateProvider.TYPE_CODEC.fieldOf("alternate_inner_layer_provider").forGetter((config) -> {
         return config.alternateInnerLayerProvider;
      }), BlockStateProvider.TYPE_CODEC.fieldOf("middle_layer_provider").forGetter((config) -> {
         return config.middleLayerProvider;
      }), BlockStateProvider.TYPE_CODEC.fieldOf("outer_layer_provider").forGetter((config) -> {
         return config.outerLayerProvider;
      }), Codecs.nonEmptyList(BlockState.CODEC.listOf()).fieldOf("inner_placements").forGetter((config) -> {
         return config.innerBlocks;
      }), TagKey.codec(RegistryKeys.BLOCK).fieldOf("cannot_replace").forGetter((config) -> {
         return config.cannotReplace;
      }), TagKey.codec(RegistryKeys.BLOCK).fieldOf("invalid_blocks").forGetter((config) -> {
         return config.invalidBlocks;
      })).apply(instance, GeodeLayerConfig::new);
   });

   public GeodeLayerConfig(BlockStateProvider fillingProvider, BlockStateProvider innerLayerProvider, BlockStateProvider alternateInnerLayerProvider, BlockStateProvider middleLayerProvider, BlockStateProvider outerLayerProvider, List innerBlocks, TagKey cannotReplace, TagKey invalidBlocks) {
      this.fillingProvider = fillingProvider;
      this.innerLayerProvider = innerLayerProvider;
      this.alternateInnerLayerProvider = alternateInnerLayerProvider;
      this.middleLayerProvider = middleLayerProvider;
      this.outerLayerProvider = outerLayerProvider;
      this.innerBlocks = innerBlocks;
      this.cannotReplace = cannotReplace;
      this.invalidBlocks = invalidBlocks;
   }
}
