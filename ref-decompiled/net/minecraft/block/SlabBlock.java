package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

public class SlabBlock extends Block implements Waterloggable {
   public static final MapCodec CODEC = createCodec(SlabBlock::new);
   public static final EnumProperty TYPE;
   public static final BooleanProperty WATERLOGGED;
   private static final VoxelShape BOTTOM_SHAPE;
   private static final VoxelShape TOP_SHAPE;

   public MapCodec getCodec() {
      return CODEC;
   }

   public SlabBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)this.getDefaultState().with(TYPE, SlabType.BOTTOM)).with(WATERLOGGED, false));
   }

   protected boolean hasSidedTransparency(BlockState state) {
      return state.get(TYPE) != SlabType.DOUBLE;
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(TYPE, WATERLOGGED);
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      VoxelShape var10000;
      switch ((SlabType)state.get(TYPE)) {
         case TOP:
            var10000 = TOP_SHAPE;
            break;
         case BOTTOM:
            var10000 = BOTTOM_SHAPE;
            break;
         case DOUBLE:
            var10000 = VoxelShapes.fullCube();
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   @Nullable
   public BlockState getPlacementState(ItemPlacementContext ctx) {
      BlockPos blockPos = ctx.getBlockPos();
      BlockState blockState = ctx.getWorld().getBlockState(blockPos);
      if (blockState.isOf(this)) {
         return (BlockState)((BlockState)blockState.with(TYPE, SlabType.DOUBLE)).with(WATERLOGGED, false);
      } else {
         FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
         BlockState blockState2 = (BlockState)((BlockState)this.getDefaultState().with(TYPE, SlabType.BOTTOM)).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
         Direction direction = ctx.getSide();
         return direction != Direction.DOWN && (direction == Direction.UP || !(ctx.getHitPos().y - (double)blockPos.getY() > 0.5)) ? blockState2 : (BlockState)blockState2.with(TYPE, SlabType.TOP);
      }
   }

   protected boolean canReplace(BlockState state, ItemPlacementContext context) {
      ItemStack itemStack = context.getStack();
      SlabType slabType = (SlabType)state.get(TYPE);
      if (slabType != SlabType.DOUBLE && itemStack.isOf(this.asItem())) {
         if (context.canReplaceExisting()) {
            boolean bl = context.getHitPos().y - (double)context.getBlockPos().getY() > 0.5;
            Direction direction = context.getSide();
            if (slabType == SlabType.BOTTOM) {
               return direction == Direction.UP || bl && direction.getAxis().isHorizontal();
            } else {
               return direction == Direction.DOWN || !bl && direction.getAxis().isHorizontal();
            }
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   protected FluidState getFluidState(BlockState state) {
      return (Boolean)state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
   }

   public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
      return state.get(TYPE) != SlabType.DOUBLE ? Waterloggable.super.tryFillWithFluid(world, pos, state, fluidState) : false;
   }

   public boolean canFillWithFluid(@Nullable LivingEntity filler, BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
      return state.get(TYPE) != SlabType.DOUBLE ? Waterloggable.super.canFillWithFluid(filler, world, pos, state, fluid) : false;
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      if ((Boolean)state.get(WATERLOGGED)) {
         tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
      }

      return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
   }

   protected boolean canPathfindThrough(BlockState state, NavigationType type) {
      switch (type) {
         case LAND:
            return false;
         case WATER:
            return state.getFluidState().isIn(FluidTags.WATER);
         case AIR:
            return false;
         default:
            return false;
      }
   }

   static {
      TYPE = Properties.SLAB_TYPE;
      WATERLOGGED = Properties.WATERLOGGED;
      BOTTOM_SHAPE = Block.createColumnShape(16.0, 0.0, 8.0);
      TOP_SHAPE = Block.createColumnShape(16.0, 8.0, 16.0);
   }
}
