package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public abstract class RodBlock extends FacingBlock {
   private static final Map SHAPES_BY_AXIS = VoxelShapes.createAxisShapeMap(Block.createCuboidShape(4.0, 4.0, 16.0));

   protected RodBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   protected abstract MapCodec getCodec();

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return (VoxelShape)SHAPES_BY_AXIS.get(((Direction)state.get(FACING)).getAxis());
   }

   protected BlockState rotate(BlockState state, BlockRotation rotation) {
      return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
   }

   protected BlockState mirror(BlockState state, BlockMirror mirror) {
      return (BlockState)state.with(FACING, mirror.apply((Direction)state.get(FACING)));
   }

   protected boolean canPathfindThrough(BlockState state, NavigationType type) {
      return false;
   }
}
