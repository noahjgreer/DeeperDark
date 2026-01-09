package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class ScaffoldingBlock extends Block implements Waterloggable {
   public static final MapCodec CODEC = createCodec(ScaffoldingBlock::new);
   private static final int field_31238 = 1;
   private static final VoxelShape NORMAL_OUTLINE_SHAPE = VoxelShapes.union(Block.createColumnShape(16.0, 14.0, 16.0), (VoxelShape)VoxelShapes.createHorizontalFacingShapeMap(Block.createCuboidShape(0.0, 0.0, 0.0, 2.0, 16.0, 2.0)).values().stream().reduce(VoxelShapes.empty(), VoxelShapes::union));
   private static final VoxelShape COLLISION_SHAPE = Block.createColumnShape(16.0, 0.0, 2.0);
   private static final VoxelShape BOTTOM_OUTLINE_SHAPE;
   private static final VoxelShape OUTLINE_SHAPE;
   public static final int MAX_DISTANCE = 7;
   public static final IntProperty DISTANCE;
   public static final BooleanProperty WATERLOGGED;
   public static final BooleanProperty BOTTOM;

   public MapCodec getCodec() {
      return CODEC;
   }

   public ScaffoldingBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(DISTANCE, 7)).with(WATERLOGGED, false)).with(BOTTOM, false));
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(DISTANCE, WATERLOGGED, BOTTOM);
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      if (!context.isHolding(state.getBlock().asItem())) {
         return (Boolean)state.get(BOTTOM) ? BOTTOM_OUTLINE_SHAPE : NORMAL_OUTLINE_SHAPE;
      } else {
         return VoxelShapes.fullCube();
      }
   }

   protected VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
      return VoxelShapes.fullCube();
   }

   protected boolean canReplace(BlockState state, ItemPlacementContext context) {
      return context.getStack().isOf(this.asItem());
   }

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      BlockPos blockPos = ctx.getBlockPos();
      World world = ctx.getWorld();
      int i = calculateDistance(world, blockPos);
      return (BlockState)((BlockState)((BlockState)this.getDefaultState().with(WATERLOGGED, world.getFluidState(blockPos).getFluid() == Fluids.WATER)).with(DISTANCE, i)).with(BOTTOM, this.shouldBeBottom(world, blockPos, i));
   }

   protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
      if (!world.isClient) {
         world.scheduleBlockTick(pos, this, 1);
      }

   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      if ((Boolean)state.get(WATERLOGGED)) {
         tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
      }

      if (!world.isClient()) {
         tickView.scheduleBlockTick(pos, this, 1);
      }

      return state;
   }

   protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      int i = calculateDistance(world, pos);
      BlockState blockState = (BlockState)((BlockState)state.with(DISTANCE, i)).with(BOTTOM, this.shouldBeBottom(world, pos, i));
      if ((Integer)blockState.get(DISTANCE) == 7) {
         if ((Integer)state.get(DISTANCE) == 7) {
            FallingBlockEntity.spawnFromBlock(world, pos, blockState);
         } else {
            world.breakBlock(pos, true);
         }
      } else if (state != blockState) {
         world.setBlockState(pos, blockState, 3);
      }

   }

   protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
      return calculateDistance(world, pos) < 7;
   }

   protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      if (context.isPlacement()) {
         return VoxelShapes.empty();
      } else if (context.isAbove(VoxelShapes.fullCube(), pos, true) && !context.isDescending()) {
         return NORMAL_OUTLINE_SHAPE;
      } else {
         return (Integer)state.get(DISTANCE) != 0 && (Boolean)state.get(BOTTOM) && context.isAbove(OUTLINE_SHAPE, pos, true) ? COLLISION_SHAPE : VoxelShapes.empty();
      }
   }

   protected FluidState getFluidState(BlockState state) {
      return (Boolean)state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
   }

   private boolean shouldBeBottom(BlockView world, BlockPos pos, int distance) {
      return distance > 0 && !world.getBlockState(pos.down()).isOf(this);
   }

   public static int calculateDistance(BlockView world, BlockPos pos) {
      BlockPos.Mutable mutable = pos.mutableCopy().move(Direction.DOWN);
      BlockState blockState = world.getBlockState(mutable);
      int i = 7;
      if (blockState.isOf(Blocks.SCAFFOLDING)) {
         i = (Integer)blockState.get(DISTANCE);
      } else if (blockState.isSideSolidFullSquare(world, mutable, Direction.UP)) {
         return 0;
      }

      Iterator var5 = Direction.Type.HORIZONTAL.iterator();

      while(var5.hasNext()) {
         Direction direction = (Direction)var5.next();
         BlockState blockState2 = world.getBlockState(mutable.set(pos, (Direction)direction));
         if (blockState2.isOf(Blocks.SCAFFOLDING)) {
            i = Math.min(i, (Integer)blockState2.get(DISTANCE) + 1);
            if (i == 1) {
               break;
            }
         }
      }

      return i;
   }

   static {
      BOTTOM_OUTLINE_SHAPE = VoxelShapes.union(NORMAL_OUTLINE_SHAPE, COLLISION_SHAPE, (VoxelShape)VoxelShapes.createHorizontalFacingShapeMap(Block.createCuboidZShape(16.0, 0.0, 2.0, 0.0, 2.0)).values().stream().reduce(VoxelShapes.empty(), VoxelShapes::union));
      OUTLINE_SHAPE = VoxelShapes.fullCube().offset(0.0, -1.0, 0.0).simplify();
      DISTANCE = Properties.DISTANCE_0_7;
      WATERLOGGED = Properties.WATERLOGGED;
      BOTTOM = Properties.BOTTOM;
   }
}
