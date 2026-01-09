package net.minecraft.world.gen.stateprovider;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

public class PillarBlockStateProvider extends BlockStateProvider {
   public static final MapCodec CODEC;
   private final Block block;

   public PillarBlockStateProvider(Block block) {
      this.block = block;
   }

   protected BlockStateProviderType getType() {
      return BlockStateProviderType.ROTATED_BLOCK_PROVIDER;
   }

   public BlockState get(Random random, BlockPos pos) {
      Direction.Axis axis = Direction.Axis.pickRandomAxis(random);
      return (BlockState)this.block.getDefaultState().withIfExists(PillarBlock.AXIS, axis);
   }

   static {
      CODEC = BlockState.CODEC.fieldOf("state").xmap(AbstractBlock.AbstractBlockState::getBlock, Block::getDefaultState).xmap(PillarBlockStateProvider::new, (provider) -> {
         return provider.block;
      });
   }
}
