package net.minecraft.world.gen.stateprovider;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class BlockStateProviderType {
   public static final BlockStateProviderType SIMPLE_STATE_PROVIDER;
   public static final BlockStateProviderType WEIGHTED_STATE_PROVIDER;
   public static final BlockStateProviderType NOISE_THRESHOLD_PROVIDER;
   public static final BlockStateProviderType NOISE_PROVIDER;
   public static final BlockStateProviderType DUAL_NOISE_PROVIDER;
   public static final BlockStateProviderType ROTATED_BLOCK_PROVIDER;
   public static final BlockStateProviderType RANDOMIZED_INT_STATE_PROVIDER;
   private final MapCodec codec;

   private static BlockStateProviderType register(String id, MapCodec codec) {
      return (BlockStateProviderType)Registry.register(Registries.BLOCK_STATE_PROVIDER_TYPE, (String)id, new BlockStateProviderType(codec));
   }

   public BlockStateProviderType(MapCodec codec) {
      this.codec = codec;
   }

   public MapCodec getCodec() {
      return this.codec;
   }

   static {
      SIMPLE_STATE_PROVIDER = register("simple_state_provider", SimpleBlockStateProvider.CODEC);
      WEIGHTED_STATE_PROVIDER = register("weighted_state_provider", WeightedBlockStateProvider.CODEC);
      NOISE_THRESHOLD_PROVIDER = register("noise_threshold_provider", NoiseThresholdBlockStateProvider.CODEC);
      NOISE_PROVIDER = register("noise_provider", NoiseBlockStateProvider.CODEC);
      DUAL_NOISE_PROVIDER = register("dual_noise_provider", DualNoiseBlockStateProvider.DUAL_CODEC);
      ROTATED_BLOCK_PROVIDER = register("rotated_block_provider", PillarBlockStateProvider.CODEC);
      RANDOMIZED_INT_STATE_PROVIDER = register("randomized_int_state_provider", RandomizedIntBlockStateProvider.CODEC);
   }
}
