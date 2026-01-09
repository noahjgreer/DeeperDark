package net.minecraft.item;

import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class VerticallyAttachableBlockItem extends BlockItem {
   protected final Block wallBlock;
   private final Direction verticalAttachmentDirection;

   public VerticallyAttachableBlockItem(Block standingBlock, Block wallBlock, Direction verticalAttachmentDirection, Item.Settings settings) {
      super(standingBlock, settings);
      this.wallBlock = wallBlock;
      this.verticalAttachmentDirection = verticalAttachmentDirection;
   }

   protected boolean canPlaceAt(WorldView world, BlockState state, BlockPos pos) {
      return state.canPlaceAt(world, pos);
   }

   @Nullable
   protected BlockState getPlacementState(ItemPlacementContext context) {
      BlockState blockState = this.wallBlock.getPlacementState(context);
      BlockState blockState2 = null;
      WorldView worldView = context.getWorld();
      BlockPos blockPos = context.getBlockPos();
      Direction[] var6 = context.getPlacementDirections();
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Direction direction = var6[var8];
         if (direction != this.verticalAttachmentDirection.getOpposite()) {
            BlockState blockState3 = direction == this.verticalAttachmentDirection ? this.getBlock().getPlacementState(context) : blockState;
            if (blockState3 != null && this.canPlaceAt(worldView, blockState3, blockPos)) {
               blockState2 = blockState3;
               break;
            }
         }
      }

      return blockState2 != null && worldView.canPlace(blockState2, blockPos, ShapeContext.absent()) ? blockState2 : null;
   }

   public void appendBlocks(Map map, Item item) {
      super.appendBlocks(map, item);
      map.put(this.wallBlock, item);
   }
}
