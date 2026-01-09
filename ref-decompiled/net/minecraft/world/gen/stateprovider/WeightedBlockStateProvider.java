package net.minecraft.world.gen.stateprovider;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class WeightedBlockStateProvider extends BlockStateProvider {
   public static final MapCodec CODEC;
   private final Pool states;

   private static DataResult wrap(Pool states) {
      return states.isEmpty() ? DataResult.error(() -> {
         return "WeightedStateProvider with no states";
      }) : DataResult.success(new WeightedBlockStateProvider(states));
   }

   public WeightedBlockStateProvider(Pool states) {
      this.states = states;
   }

   public WeightedBlockStateProvider(Pool.Builder states) {
      this(states.build());
   }

   protected BlockStateProviderType getType() {
      return BlockStateProviderType.WEIGHTED_STATE_PROVIDER;
   }

   public BlockState get(Random random, BlockPos pos) {
      return (BlockState)this.states.get(random);
   }

   static {
      CODEC = Pool.createNonEmptyCodec(BlockState.CODEC).comapFlatMap(WeightedBlockStateProvider::wrap, (weightedBlockStateProvider) -> {
         return weightedBlockStateProvider.states;
      }).fieldOf("entries");
   }
}
