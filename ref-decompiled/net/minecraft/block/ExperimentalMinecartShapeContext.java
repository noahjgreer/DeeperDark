package net.minecraft.block;

import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.CollisionView;
import org.jetbrains.annotations.Nullable;

public class ExperimentalMinecartShapeContext extends EntityShapeContext {
   @Nullable
   private BlockPos belowPos;
   @Nullable
   private BlockPos ascendingPos;

   protected ExperimentalMinecartShapeContext(AbstractMinecartEntity minecart, boolean collidesWithFluid) {
      super(minecart, collidesWithFluid, false);
      this.setIgnoredPositions(minecart);
   }

   private void setIgnoredPositions(AbstractMinecartEntity minecart) {
      BlockPos blockPos = minecart.getRailOrMinecartPos();
      BlockState blockState = minecart.getWorld().getBlockState(blockPos);
      boolean bl = AbstractRailBlock.isRail(blockState);
      if (bl) {
         this.belowPos = blockPos.down();
         RailShape railShape = (RailShape)blockState.get(((AbstractRailBlock)blockState.getBlock()).getShapeProperty());
         if (railShape.isAscending()) {
            BlockPos var10001;
            switch (railShape) {
               case ASCENDING_EAST:
                  var10001 = blockPos.east();
                  break;
               case ASCENDING_WEST:
                  var10001 = blockPos.west();
                  break;
               case ASCENDING_NORTH:
                  var10001 = blockPos.north();
                  break;
               case ASCENDING_SOUTH:
                  var10001 = blockPos.south();
                  break;
               default:
                  var10001 = null;
            }

            this.ascendingPos = var10001;
         }
      }

   }

   public VoxelShape getCollisionShape(BlockState state, CollisionView world, BlockPos pos) {
      return !pos.equals(this.belowPos) && !pos.equals(this.ascendingPos) ? super.getCollisionShape(state, world, pos) : VoxelShapes.empty();
   }
}
