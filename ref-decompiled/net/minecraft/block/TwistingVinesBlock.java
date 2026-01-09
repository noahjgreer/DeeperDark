package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;

public class TwistingVinesBlock extends AbstractPlantStemBlock {
   public static final MapCodec CODEC = createCodec(TwistingVinesBlock::new);
   private static final VoxelShape SHAPE = Block.createColumnShape(8.0, 0.0, 15.0);

   public MapCodec getCodec() {
      return CODEC;
   }

   public TwistingVinesBlock(AbstractBlock.Settings settings) {
      super(settings, Direction.UP, SHAPE, false, 0.1);
   }

   protected int getGrowthLength(Random random) {
      return VineLogic.getGrowthLength(random);
   }

   protected Block getPlant() {
      return Blocks.TWISTING_VINES_PLANT;
   }

   protected boolean chooseStemState(BlockState state) {
      return VineLogic.isValidForWeepingStem(state);
   }
}
