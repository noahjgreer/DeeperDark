package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.enums.RailShape;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RailBlock extends AbstractRailBlock {
   public static final MapCodec CODEC = createCodec(RailBlock::new);
   public static final EnumProperty SHAPE;

   public MapCodec getCodec() {
      return CODEC;
   }

   public RailBlock(AbstractBlock.Settings settings) {
      super(false, settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(SHAPE, RailShape.NORTH_SOUTH)).with(WATERLOGGED, false));
   }

   protected void updateBlockState(BlockState state, World world, BlockPos pos, Block neighbor) {
      if (neighbor.getDefaultState().emitsRedstonePower() && (new RailPlacementHelper(world, pos, state)).getNeighborCount() == 3) {
         this.updateBlockState(world, pos, state, false);
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
      builder.add(SHAPE, WATERLOGGED);
   }

   static {
      SHAPE = Properties.RAIL_SHAPE;
   }
}
