package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.enums.RailShape;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PoweredRailBlock extends AbstractRailBlock {
   public static final MapCodec CODEC = createCodec(PoweredRailBlock::new);
   public static final EnumProperty SHAPE;
   public static final BooleanProperty POWERED;

   public MapCodec getCodec() {
      return CODEC;
   }

   public PoweredRailBlock(AbstractBlock.Settings settings) {
      super(true, settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(SHAPE, RailShape.NORTH_SOUTH)).with(POWERED, false)).with(WATERLOGGED, false));
   }

   protected boolean isPoweredByOtherRails(World world, BlockPos pos, BlockState state, boolean bl, int distance) {
      if (distance >= 8) {
         return false;
      } else {
         int i = pos.getX();
         int j = pos.getY();
         int k = pos.getZ();
         boolean bl2 = true;
         RailShape railShape = (RailShape)state.get(SHAPE);
         switch (railShape) {
            case NORTH_SOUTH:
               if (bl) {
                  ++k;
               } else {
                  --k;
               }
               break;
            case EAST_WEST:
               if (bl) {
                  --i;
               } else {
                  ++i;
               }
               break;
            case ASCENDING_EAST:
               if (bl) {
                  --i;
               } else {
                  ++i;
                  ++j;
                  bl2 = false;
               }

               railShape = RailShape.EAST_WEST;
               break;
            case ASCENDING_WEST:
               if (bl) {
                  --i;
                  ++j;
                  bl2 = false;
               } else {
                  ++i;
               }

               railShape = RailShape.EAST_WEST;
               break;
            case ASCENDING_NORTH:
               if (bl) {
                  ++k;
               } else {
                  --k;
                  ++j;
                  bl2 = false;
               }

               railShape = RailShape.NORTH_SOUTH;
               break;
            case ASCENDING_SOUTH:
               if (bl) {
                  ++k;
                  ++j;
                  bl2 = false;
               } else {
                  --k;
               }

               railShape = RailShape.NORTH_SOUTH;
         }

         if (this.isPoweredByOtherRails(world, new BlockPos(i, j, k), bl, distance, railShape)) {
            return true;
         } else {
            return bl2 && this.isPoweredByOtherRails(world, new BlockPos(i, j - 1, k), bl, distance, railShape);
         }
      }
   }

   protected boolean isPoweredByOtherRails(World world, BlockPos pos, boolean bl, int distance, RailShape shape) {
      BlockState blockState = world.getBlockState(pos);
      if (!blockState.isOf(this)) {
         return false;
      } else {
         RailShape railShape = (RailShape)blockState.get(SHAPE);
         if (shape == RailShape.EAST_WEST && (railShape == RailShape.NORTH_SOUTH || railShape == RailShape.ASCENDING_NORTH || railShape == RailShape.ASCENDING_SOUTH)) {
            return false;
         } else if (shape == RailShape.NORTH_SOUTH && (railShape == RailShape.EAST_WEST || railShape == RailShape.ASCENDING_EAST || railShape == RailShape.ASCENDING_WEST)) {
            return false;
         } else if ((Boolean)blockState.get(POWERED)) {
            return world.isReceivingRedstonePower(pos) ? true : this.isPoweredByOtherRails(world, pos, blockState, bl, distance + 1);
         } else {
            return false;
         }
      }
   }

   protected void updateBlockState(BlockState state, World world, BlockPos pos, Block neighbor) {
      boolean bl = (Boolean)state.get(POWERED);
      boolean bl2 = world.isReceivingRedstonePower(pos) || this.isPoweredByOtherRails(world, pos, state, true, 0) || this.isPoweredByOtherRails(world, pos, state, false, 0);
      if (bl2 != bl) {
         world.setBlockState(pos, (BlockState)state.with(POWERED, bl2), 3);
         world.updateNeighbors(pos.down(), this);
         if (((RailShape)state.get(SHAPE)).isAscending()) {
            world.updateNeighbors(pos.up(), this);
         }
      }

   }

   public Property getShapeProperty() {
      return SHAPE;
   }

   protected BlockState rotate(BlockState state, BlockRotation rotation) {
      RailShape railShape = (RailShape)state.get(SHAPE);
      RailShape railShape2 = this.rotateShape(railShape, rotation);
      return (BlockState)state.with(SHAPE, railShape2);
   }

   protected BlockState mirror(BlockState state, BlockMirror mirror) {
      RailShape railShape = (RailShape)state.get(SHAPE);
      RailShape railShape2 = this.mirrorShape(railShape, mirror);
      return (BlockState)state.with(SHAPE, railShape2);
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(SHAPE, POWERED, WATERLOGGED);
   }

   static {
      SHAPE = Properties.STRAIGHT_RAIL_SHAPE;
      POWERED = Properties.POWERED;
   }
}
