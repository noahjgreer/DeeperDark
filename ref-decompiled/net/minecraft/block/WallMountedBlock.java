package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

public abstract class WallMountedBlock extends HorizontalFacingBlock {
   public static final EnumProperty FACE;

   protected WallMountedBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   protected abstract MapCodec getCodec();

   protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
      return canPlaceAt(world, pos, getDirection(state).getOpposite());
   }

   public static boolean canPlaceAt(WorldView world, BlockPos pos, Direction direction) {
      BlockPos blockPos = pos.offset(direction);
      return world.getBlockState(blockPos).isSideSolidFullSquare(world, blockPos, direction.getOpposite());
   }

   @Nullable
   public BlockState getPlacementState(ItemPlacementContext ctx) {
      Direction[] var2 = ctx.getPlacementDirections();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Direction direction = var2[var4];
         BlockState blockState;
         if (direction.getAxis() == Direction.Axis.Y) {
            blockState = (BlockState)((BlockState)this.getDefaultState().with(FACE, direction == Direction.UP ? BlockFace.CEILING : BlockFace.FLOOR)).with(FACING, ctx.getHorizontalPlayerFacing());
         } else {
            blockState = (BlockState)((BlockState)this.getDefaultState().with(FACE, BlockFace.WALL)).with(FACING, direction.getOpposite());
         }

         if (blockState.canPlaceAt(ctx.getWorld(), ctx.getBlockPos())) {
            return blockState;
         }
      }

      return null;
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      return getDirection(state).getOpposite() == direction && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
   }

   protected static Direction getDirection(BlockState state) {
      switch ((BlockFace)state.get(FACE)) {
         case CEILING:
            return Direction.DOWN;
         case FLOOR:
            return Direction.UP;
         default:
            return (Direction)state.get(FACING);
      }
   }

   static {
      FACE = Properties.BLOCK_FACE;
   }
}
