package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.StairShape;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.AxisRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.DirectionTransformation;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class StairsBlock extends Block implements Waterloggable {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(BlockState.CODEC.fieldOf("base_state").forGetter((block) -> {
         return block.baseBlockState;
      }), createSettingsCodec()).apply(instance, StairsBlock::new);
   });
   public static final EnumProperty FACING;
   public static final EnumProperty HALF;
   public static final EnumProperty SHAPE;
   public static final BooleanProperty WATERLOGGED;
   private static final VoxelShape OUTER_SHAPE;
   private static final VoxelShape STRAIGHT_SHAPE;
   private static final VoxelShape INNER_SHAPE;
   private static final Map OUTER_BOTTOM_SHAPES;
   private static final Map STRAIGHT_BOTTOM_SHAPES;
   private static final Map INNER_BOTTOM_SHAPES;
   private static final Map OUTER_TOP_SHAPES;
   private static final Map STRAIGHT_TOP_SHAPES;
   private static final Map INNER_TOP_SHAPES;
   private final Block baseBlock;
   protected final BlockState baseBlockState;

   public MapCodec getCodec() {
      return CODEC;
   }

   public StairsBlock(BlockState baseBlockState, AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(HALF, BlockHalf.BOTTOM)).with(SHAPE, StairShape.STRAIGHT)).with(WATERLOGGED, false));
      this.baseBlock = baseBlockState.getBlock();
      this.baseBlockState = baseBlockState;
   }

   protected boolean hasSidedTransparency(BlockState state) {
      return true;
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      boolean bl = state.get(HALF) == BlockHalf.BOTTOM;
      Direction direction = (Direction)state.get(FACING);
      Map var10000;
      switch ((StairShape)state.get(SHAPE)) {
         case STRAIGHT:
            var10000 = bl ? STRAIGHT_BOTTOM_SHAPES : STRAIGHT_TOP_SHAPES;
            break;
         case OUTER_LEFT:
         case OUTER_RIGHT:
            var10000 = bl ? OUTER_BOTTOM_SHAPES : OUTER_TOP_SHAPES;
            break;
         case INNER_RIGHT:
         case INNER_LEFT:
            var10000 = bl ? INNER_BOTTOM_SHAPES : INNER_TOP_SHAPES;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      Direction var10001;
      switch ((StairShape)state.get(SHAPE)) {
         case STRAIGHT:
         case OUTER_LEFT:
         case INNER_RIGHT:
            var10001 = direction;
            break;
         case INNER_LEFT:
            var10001 = direction.rotateYCounterclockwise();
            break;
         case OUTER_RIGHT:
            var10001 = direction.rotateYClockwise();
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return (VoxelShape)var10000.get(var10001);
   }

   public float getBlastResistance() {
      return this.baseBlock.getBlastResistance();
   }

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      Direction direction = ctx.getSide();
      BlockPos blockPos = ctx.getBlockPos();
      FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
      BlockState blockState = (BlockState)((BlockState)((BlockState)this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing())).with(HALF, direction != Direction.DOWN && (direction == Direction.UP || !(ctx.getHitPos().y - (double)blockPos.getY() > 0.5)) ? BlockHalf.BOTTOM : BlockHalf.TOP)).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
      return (BlockState)blockState.with(SHAPE, getStairShape(blockState, ctx.getWorld(), blockPos));
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      if ((Boolean)state.get(WATERLOGGED)) {
         tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
      }

      return direction.getAxis().isHorizontal() ? (BlockState)state.with(SHAPE, getStairShape(state, world, pos)) : super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
   }

   private static StairShape getStairShape(BlockState state, BlockView world, BlockPos pos) {
      Direction direction = (Direction)state.get(FACING);
      BlockState blockState = world.getBlockState(pos.offset(direction));
      if (isStairs(blockState) && state.get(HALF) == blockState.get(HALF)) {
         Direction direction2 = (Direction)blockState.get(FACING);
         if (direction2.getAxis() != ((Direction)state.get(FACING)).getAxis() && isDifferentOrientation(state, world, pos, direction2.getOpposite())) {
            if (direction2 == direction.rotateYCounterclockwise()) {
               return StairShape.OUTER_LEFT;
            }

            return StairShape.OUTER_RIGHT;
         }
      }

      BlockState blockState2 = world.getBlockState(pos.offset(direction.getOpposite()));
      if (isStairs(blockState2) && state.get(HALF) == blockState2.get(HALF)) {
         Direction direction3 = (Direction)blockState2.get(FACING);
         if (direction3.getAxis() != ((Direction)state.get(FACING)).getAxis() && isDifferentOrientation(state, world, pos, direction3)) {
            if (direction3 == direction.rotateYCounterclockwise()) {
               return StairShape.INNER_LEFT;
            }

            return StairShape.INNER_RIGHT;
         }
      }

      return StairShape.STRAIGHT;
   }

   private static boolean isDifferentOrientation(BlockState state, BlockView world, BlockPos pos, Direction dir) {
      BlockState blockState = world.getBlockState(pos.offset(dir));
      return !isStairs(blockState) || blockState.get(FACING) != state.get(FACING) || blockState.get(HALF) != state.get(HALF);
   }

   public static boolean isStairs(BlockState state) {
      return state.getBlock() instanceof StairsBlock;
   }

   protected BlockState rotate(BlockState state, BlockRotation rotation) {
      return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
   }

   protected BlockState mirror(BlockState state, BlockMirror mirror) {
      Direction direction = (Direction)state.get(FACING);
      StairShape stairShape = (StairShape)state.get(SHAPE);
      switch (mirror) {
         case LEFT_RIGHT:
            if (direction.getAxis() == Direction.Axis.Z) {
               switch (stairShape) {
                  case OUTER_LEFT:
                     return (BlockState)state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.OUTER_RIGHT);
                  case INNER_RIGHT:
                     return (BlockState)state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.INNER_LEFT);
                  case INNER_LEFT:
                     return (BlockState)state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.INNER_RIGHT);
                  case OUTER_RIGHT:
                     return (BlockState)state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.OUTER_LEFT);
                  default:
                     return state.rotate(BlockRotation.CLOCKWISE_180);
               }
            }
            break;
         case FRONT_BACK:
            if (direction.getAxis() == Direction.Axis.X) {
               switch (stairShape) {
                  case STRAIGHT:
                     return state.rotate(BlockRotation.CLOCKWISE_180);
                  case OUTER_LEFT:
                     return (BlockState)state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.OUTER_RIGHT);
                  case INNER_RIGHT:
                     return (BlockState)state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.INNER_RIGHT);
                  case INNER_LEFT:
                     return (BlockState)state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.INNER_LEFT);
                  case OUTER_RIGHT:
                     return (BlockState)state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.OUTER_LEFT);
               }
            }
      }

      return super.mirror(state, mirror);
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(FACING, HALF, SHAPE, WATERLOGGED);
   }

   protected FluidState getFluidState(BlockState state) {
      return (Boolean)state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
   }

   protected boolean canPathfindThrough(BlockState state, NavigationType type) {
      return false;
   }

   static {
      FACING = HorizontalFacingBlock.FACING;
      HALF = Properties.BLOCK_HALF;
      SHAPE = Properties.STAIR_SHAPE;
      WATERLOGGED = Properties.WATERLOGGED;
      OUTER_SHAPE = VoxelShapes.union(Block.createColumnShape(16.0, 0.0, 8.0), Block.createCuboidShape(0.0, 8.0, 0.0, 8.0, 16.0, 8.0));
      STRAIGHT_SHAPE = VoxelShapes.union(OUTER_SHAPE, VoxelShapes.transform(OUTER_SHAPE, DirectionTransformation.fromRotations(AxisRotation.R0, AxisRotation.R90)));
      INNER_SHAPE = VoxelShapes.union(STRAIGHT_SHAPE, VoxelShapes.transform(STRAIGHT_SHAPE, DirectionTransformation.fromRotations(AxisRotation.R0, AxisRotation.R90)));
      OUTER_BOTTOM_SHAPES = VoxelShapes.createHorizontalFacingShapeMap(OUTER_SHAPE);
      STRAIGHT_BOTTOM_SHAPES = VoxelShapes.createHorizontalFacingShapeMap(STRAIGHT_SHAPE);
      INNER_BOTTOM_SHAPES = VoxelShapes.createHorizontalFacingShapeMap(INNER_SHAPE);
      OUTER_TOP_SHAPES = VoxelShapes.createHorizontalFacingShapeMap(VoxelShapes.transform(OUTER_SHAPE, DirectionTransformation.INVERT_Y));
      STRAIGHT_TOP_SHAPES = VoxelShapes.createHorizontalFacingShapeMap(VoxelShapes.transform(STRAIGHT_SHAPE, DirectionTransformation.INVERT_Y));
      INNER_TOP_SHAPES = VoxelShapes.createHorizontalFacingShapeMap(VoxelShapes.transform(INNER_SHAPE, DirectionTransformation.INVERT_Y));
   }
}
