package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class MudBlock extends Block {
   public static final MapCodec CODEC = createCodec(MudBlock::new);
   private static final VoxelShape COLLISION_SHAPE = Block.createColumnShape(16.0, 0.0, 14.0);

   public MapCodec getCodec() {
      return CODEC;
   }

   public MudBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return COLLISION_SHAPE;
   }

   protected VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
      return VoxelShapes.fullCube();
   }

   protected VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return VoxelShapes.fullCube();
   }

   protected boolean canPathfindThrough(BlockState state, NavigationType type) {
      return false;
   }

   protected float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
      return 0.2F;
   }
}
