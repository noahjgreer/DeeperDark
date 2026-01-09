package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class RootedDirtBlock extends Block implements Fertilizable {
   public static final MapCodec CODEC = createCodec(RootedDirtBlock::new);

   public MapCodec getCodec() {
      return CODEC;
   }

   public RootedDirtBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
      return world.getBlockState(pos.down()).isAir();
   }

   public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
      return true;
   }

   public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
      world.setBlockState(pos.down(), Blocks.HANGING_ROOTS.getDefaultState());
   }

   public BlockPos getFertilizeParticlePos(BlockPos pos) {
      return pos.down();
   }
}
