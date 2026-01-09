package net.minecraft.world.gen.stateprovider;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class SimpleBlockStateProvider extends BlockStateProvider {
   public static final MapCodec CODEC;
   private final BlockState state;

   protected SimpleBlockStateProvider(BlockState state) {
      this.state = state;
   }

   protected BlockStateProviderType getType() {
      return BlockStateProviderType.SIMPLE_STATE_PROVIDER;
   }

   public BlockState get(Random random, BlockPos pos) {
      return this.state;
   }

   static {
      CODEC = BlockState.CODEC.fieldOf("state").xmap(SimpleBlockStateProvider::new, (simpleBlockStateProvider) -> {
         return simpleBlockStateProvider.state;
      });
   }
}
