package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

public class LanternBlock extends Block implements Waterloggable {
   public static final MapCodec CODEC = createCodec(LanternBlock::new);
   public static final BooleanProperty HANGING;
   public static final BooleanProperty WATERLOGGED;
   private static final VoxelShape STANDING_SHAPE;
   private static final VoxelShape HANGING_SHAPE;

   public MapCodec getCodec() {
      return CODEC;
   }

   public LanternBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(HANGING, false)).with(WATERLOGGED, false));
   }

   @Nullable
   public BlockState getPlacementState(ItemPlacementContext ctx) {
      FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
      Direction[] var3 = ctx.getPlacementDirections();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Direction direction = var3[var5];
         if (direction.getAxis() == Direction.Axis.Y) {
            BlockState blockState = (BlockState)this.getDefaultState().with(HANGING, direction == Direction.UP);
            if (blockState.canPlaceAt(ctx.getWorld(), ctx.getBlockPos())) {
               return (BlockState)blockState.with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
            }
         }
      }

      return null;
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return (Boolean)state.get(HANGING) ? HANGING_SHAPE : STANDING_SHAPE;
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(HANGING, WATERLOGGED);
   }

   protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
      Direction direction = attachedDirection(state).getOpposite();
      return Block.sideCoversSmallSquare(world, pos.offset(direction), direction.getOpposite());
   }

   protected static Direction attachedDirection(BlockState state) {
      return (Boolean)state.get(HANGING) ? Direction.DOWN : Direction.UP;
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      if ((Boolean)state.get(WATERLOGGED)) {
         tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
      }

      return attachedDirection(state).getOpposite() == direction && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
   }

   protected FluidState getFluidState(BlockState state) {
      return (Boolean)state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
   }

   protected boolean canPathfindThrough(BlockState state, NavigationType type) {
      return false;
   }

   static {
      HANGING = Properties.HANGING;
      WATERLOGGED = Properties.WATERLOGGED;
      STANDING_SHAPE = VoxelShapes.union(Block.createColumnShape(4.0, 7.0, 9.0), Block.createColumnShape(6.0, 0.0, 7.0));
      HANGING_SHAPE = STANDING_SHAPE.offset(0.0, 0.0625, 0.0).simplify();
   }
}
