package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.AmbientDesertBlockSounds;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class TallDryGrassBlock extends DryVegetationBlock implements Fertilizable {
   public static final MapCodec CODEC = createCodec(TallDryGrassBlock::new);
   private static final VoxelShape SHAPE = Block.createColumnShape(14.0, 0.0, 16.0);

   public MapCodec getCodec() {
      return CODEC;
   }

   public TallDryGrassBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return SHAPE;
   }

   public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
      AmbientDesertBlockSounds.tryPlayDryGrassSounds(world, pos, random);
   }

   public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
      return Fertilizable.canSpread(world, pos, Blocks.SHORT_DRY_GRASS.getDefaultState());
   }

   public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
      return true;
   }

   public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
      Fertilizable.findPosToSpreadTo(world, pos, Blocks.SHORT_DRY_GRASS.getDefaultState()).ifPresent((posx) -> {
         world.setBlockState(posx, Blocks.SHORT_DRY_GRASS.getDefaultState());
      });
   }
}
