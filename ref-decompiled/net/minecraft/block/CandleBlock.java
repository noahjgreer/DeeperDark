package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.List;
import java.util.function.ToIntFunction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class CandleBlock extends AbstractCandleBlock implements Waterloggable {
   public static final MapCodec CODEC = createCodec(CandleBlock::new);
   public static final int field_31050 = 1;
   public static final int MAX_CANDLE_AMOUNT = 4;
   public static final IntProperty CANDLES;
   public static final BooleanProperty LIT;
   public static final BooleanProperty WATERLOGGED;
   public static final ToIntFunction STATE_TO_LUMINANCE;
   private static final Int2ObjectMap CANDLES_TO_PARTICLE_OFFSETS;
   private static final VoxelShape[] SHAPES_BY_CANDLES;

   public MapCodec getCodec() {
      return CODEC;
   }

   public CandleBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(CANDLES, 1)).with(LIT, false)).with(WATERLOGGED, false));
   }

   protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
      if (stack.isEmpty() && player.getAbilities().allowModifyWorld && (Boolean)state.get(LIT)) {
         extinguish(player, state, world, pos);
         return ActionResult.SUCCESS;
      } else {
         return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
      }
   }

   protected boolean canReplace(BlockState state, ItemPlacementContext context) {
      return !context.shouldCancelInteraction() && context.getStack().getItem() == this.asItem() && (Integer)state.get(CANDLES) < 4 ? true : super.canReplace(state, context);
   }

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
      if (blockState.isOf(this)) {
         return (BlockState)blockState.cycle(CANDLES);
      } else {
         FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
         boolean bl = fluidState.getFluid() == Fluids.WATER;
         return (BlockState)super.getPlacementState(ctx).with(WATERLOGGED, bl);
      }
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      if ((Boolean)state.get(WATERLOGGED)) {
         tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
      }

      return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
   }

   protected FluidState getFluidState(BlockState state) {
      return (Boolean)state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return SHAPES_BY_CANDLES[(Integer)state.get(CANDLES) - 1];
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(CANDLES, LIT, WATERLOGGED);
   }

   public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
      if (!(Boolean)state.get(WATERLOGGED) && fluidState.getFluid() == Fluids.WATER) {
         BlockState blockState = (BlockState)state.with(WATERLOGGED, true);
         if ((Boolean)state.get(LIT)) {
            extinguish((PlayerEntity)null, blockState, world, pos);
         } else {
            world.setBlockState(pos, blockState, 3);
         }

         world.scheduleFluidTick(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
         return true;
      } else {
         return false;
      }
   }

   public static boolean canBeLit(BlockState state) {
      return state.isIn(BlockTags.CANDLES, (statex) -> {
         return statex.contains(LIT) && statex.contains(WATERLOGGED);
      }) && !(Boolean)state.get(LIT) && !(Boolean)state.get(WATERLOGGED);
   }

   protected Iterable getParticleOffsets(BlockState state) {
      return (Iterable)CANDLES_TO_PARTICLE_OFFSETS.get((Integer)state.get(CANDLES));
   }

   protected boolean isNotLit(BlockState state) {
      return !(Boolean)state.get(WATERLOGGED) && super.isNotLit(state);
   }

   protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
      return Block.sideCoversSmallSquare(world, pos.down(), Direction.UP);
   }

   static {
      CANDLES = Properties.CANDLES;
      LIT = AbstractCandleBlock.LIT;
      WATERLOGGED = Properties.WATERLOGGED;
      STATE_TO_LUMINANCE = (state) -> {
         return (Boolean)state.get(LIT) ? 3 * (Integer)state.get(CANDLES) : 0;
      };
      CANDLES_TO_PARTICLE_OFFSETS = (Int2ObjectMap)Util.make(new Int2ObjectOpenHashMap(4), (int2ObjectOpenHashMap) -> {
         float f = 0.0625F;
         int2ObjectOpenHashMap.put(1, List.of((new Vec3d(8.0, 8.0, 8.0)).multiply(0.0625)));
         int2ObjectOpenHashMap.put(2, List.of((new Vec3d(6.0, 7.0, 8.0)).multiply(0.0625), (new Vec3d(10.0, 8.0, 7.0)).multiply(0.0625)));
         int2ObjectOpenHashMap.put(3, List.of((new Vec3d(8.0, 5.0, 10.0)).multiply(0.0625), (new Vec3d(6.0, 7.0, 8.0)).multiply(0.0625), (new Vec3d(9.0, 8.0, 7.0)).multiply(0.0625)));
         int2ObjectOpenHashMap.put(4, List.of((new Vec3d(7.0, 5.0, 9.0)).multiply(0.0625), (new Vec3d(10.0, 7.0, 9.0)).multiply(0.0625), (new Vec3d(6.0, 7.0, 6.0)).multiply(0.0625), (new Vec3d(9.0, 8.0, 6.0)).multiply(0.0625)));
      });
      SHAPES_BY_CANDLES = new VoxelShape[]{Block.createColumnShape(2.0, 0.0, 6.0), Block.createCuboidShape(5.0, 0.0, 6.0, 11.0, 6.0, 9.0), Block.createCuboidShape(5.0, 0.0, 6.0, 10.0, 6.0, 11.0), Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 6.0, 10.0)};
   }
}
