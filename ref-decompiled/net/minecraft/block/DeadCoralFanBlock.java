package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class DeadCoralFanBlock extends AbstractCoralBlock {
   public static final MapCodec CODEC = createCodec(DeadCoralFanBlock::new);
   private static final VoxelShape SHAPE = Block.createColumnShape(12.0, 0.0, 4.0);

   public MapCodec getCodec() {
      return CODEC;
   }

   public DeadCoralFanBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return SHAPE;
   }
}
