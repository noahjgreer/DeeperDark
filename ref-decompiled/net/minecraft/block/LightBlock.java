package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.function.ToIntFunction;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class LightBlock extends Block implements Waterloggable {
   public static final MapCodec CODEC = createCodec(LightBlock::new);
   public static final int field_33722 = 15;
   public static final IntProperty LEVEL_15;
   public static final BooleanProperty WATERLOGGED;
   public static final ToIntFunction STATE_TO_LUMINANCE;

   public MapCodec getCodec() {
      return CODEC;
   }

   public LightBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(LEVEL_15, 15)).with(WATERLOGGED, false));
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(LEVEL_15, WATERLOGGED);
   }

   protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      if (!world.isClient && player.isCreativeLevelTwoOp()) {
         world.setBlockState(pos, (BlockState)state.cycle(LEVEL_15), 2);
         return ActionResult.SUCCESS_SERVER;
      } else {
         return ActionResult.CONSUME;
      }
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return context.isHolding(Items.LIGHT) ? VoxelShapes.fullCube() : VoxelShapes.empty();
   }

   protected boolean isTransparent(BlockState state) {
      return state.getFluidState().isEmpty();
   }

   protected BlockRenderType getRenderType(BlockState state) {
      return BlockRenderType.INVISIBLE;
   }

   protected float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
      return 1.0F;
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

   protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
      return addNbtForLevel(super.getPickStack(world, pos, state, includeData), (Integer)state.get(LEVEL_15));
   }

   public static ItemStack addNbtForLevel(ItemStack stack, int level) {
      stack.set(DataComponentTypes.BLOCK_STATE, BlockStateComponent.DEFAULT.with(LEVEL_15, (Comparable)level));
      return stack;
   }

   static {
      LEVEL_15 = Properties.LEVEL_15;
      WATERLOGGED = Properties.WATERLOGGED;
      STATE_TO_LUMINANCE = (state) -> {
         return (Integer)state.get(LEVEL_15);
      };
   }
}
