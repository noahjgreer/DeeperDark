package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class TransparentBlock extends TranslucentBlock {
   public static final MapCodec CODEC = createCodec(TransparentBlock::new);

   public TransparentBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   protected MapCodec getCodec() {
      return CODEC;
   }

   protected VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return VoxelShapes.empty();
   }

   protected float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
      return 1.0F;
   }

   protected boolean isTransparent(BlockState state) {
      return true;
   }
}
