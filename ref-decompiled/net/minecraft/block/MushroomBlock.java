package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class MushroomBlock extends Block {
   public static final MapCodec CODEC = createCodec(MushroomBlock::new);
   public static final BooleanProperty NORTH;
   public static final BooleanProperty EAST;
   public static final BooleanProperty SOUTH;
   public static final BooleanProperty WEST;
   public static final BooleanProperty UP;
   public static final BooleanProperty DOWN;
   private static final Map FACING_PROPERTIES;

   public MapCodec getCodec() {
      return CODEC;
   }

   public MushroomBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(NORTH, true)).with(EAST, true)).with(SOUTH, true)).with(WEST, true)).with(UP, true)).with(DOWN, true));
   }

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      BlockView blockView = ctx.getWorld();
      BlockPos blockPos = ctx.getBlockPos();
      return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(DOWN, !blockView.getBlockState(blockPos.down()).isOf(this))).with(UP, !blockView.getBlockState(blockPos.up()).isOf(this))).with(NORTH, !blockView.getBlockState(blockPos.north()).isOf(this))).with(EAST, !blockView.getBlockState(blockPos.east()).isOf(this))).with(SOUTH, !blockView.getBlockState(blockPos.south()).isOf(this))).with(WEST, !blockView.getBlockState(blockPos.west()).isOf(this));
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      return neighborState.isOf(this) ? (BlockState)state.with((Property)FACING_PROPERTIES.get(direction), false) : super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
   }

   protected BlockState rotate(BlockState state, BlockRotation rotation) {
      return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)state.with((Property)FACING_PROPERTIES.get(rotation.rotate(Direction.NORTH)), (Boolean)state.get(NORTH))).with((Property)FACING_PROPERTIES.get(rotation.rotate(Direction.SOUTH)), (Boolean)state.get(SOUTH))).with((Property)FACING_PROPERTIES.get(rotation.rotate(Direction.EAST)), (Boolean)state.get(EAST))).with((Property)FACING_PROPERTIES.get(rotation.rotate(Direction.WEST)), (Boolean)state.get(WEST))).with((Property)FACING_PROPERTIES.get(rotation.rotate(Direction.UP)), (Boolean)state.get(UP))).with((Property)FACING_PROPERTIES.get(rotation.rotate(Direction.DOWN)), (Boolean)state.get(DOWN));
   }

   protected BlockState mirror(BlockState state, BlockMirror mirror) {
      return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)state.with((Property)FACING_PROPERTIES.get(mirror.apply(Direction.NORTH)), (Boolean)state.get(NORTH))).with((Property)FACING_PROPERTIES.get(mirror.apply(Direction.SOUTH)), (Boolean)state.get(SOUTH))).with((Property)FACING_PROPERTIES.get(mirror.apply(Direction.EAST)), (Boolean)state.get(EAST))).with((Property)FACING_PROPERTIES.get(mirror.apply(Direction.WEST)), (Boolean)state.get(WEST))).with((Property)FACING_PROPERTIES.get(mirror.apply(Direction.UP)), (Boolean)state.get(UP))).with((Property)FACING_PROPERTIES.get(mirror.apply(Direction.DOWN)), (Boolean)state.get(DOWN));
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
   }

   static {
      NORTH = ConnectingBlock.NORTH;
      EAST = ConnectingBlock.EAST;
      SOUTH = ConnectingBlock.SOUTH;
      WEST = ConnectingBlock.WEST;
      UP = ConnectingBlock.UP;
      DOWN = ConnectingBlock.DOWN;
      FACING_PROPERTIES = ConnectingBlock.FACING_PROPERTIES;
   }
}
