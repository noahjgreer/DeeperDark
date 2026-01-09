package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class TallFlowerBlock extends TallPlantBlock implements Fertilizable {
   public static final MapCodec CODEC = createCodec(TallFlowerBlock::new);

   public MapCodec getCodec() {
      return CODEC;
   }

   public TallFlowerBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
      return true;
   }

   public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
      return true;
   }

   public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
      dropStack(world, pos, new ItemStack(this));
   }
}
