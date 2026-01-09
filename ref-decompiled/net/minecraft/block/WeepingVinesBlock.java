package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;

public class WeepingVinesBlock extends AbstractPlantStemBlock {
   public static final MapCodec CODEC = createCodec(WeepingVinesBlock::new);
   private static final VoxelShape SHAPE = Block.createColumnShape(8.0, 9.0, 16.0);

   public MapCodec getCodec() {
      return CODEC;
   }

   public WeepingVinesBlock(AbstractBlock.Settings settings) {
      super(settings, Direction.DOWN, SHAPE, false, 0.1);
   }

   protected int getGrowthLength(Random random) {
      return VineLogic.getGrowthLength(random);
   }

   protected Block getPlant() {
      return Blocks.WEEPING_VINES_PLANT;
   }

   protected boolean chooseStemState(BlockState state) {
      return VineLogic.isValidForWeepingStem(state);
   }
}
