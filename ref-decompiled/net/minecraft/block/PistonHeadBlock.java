package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import net.minecraft.block.enums.PistonType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.OrientationHelper;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

public class PistonHeadBlock extends FacingBlock {
   public static final MapCodec CODEC = createCodec(PistonHeadBlock::new);
   public static final EnumProperty TYPE;
   public static final BooleanProperty SHORT;
   public static final int field_55825 = 4;
   private static final VoxelShape BASE_SHAPE;
   private static final Map SHORT_SHAPES;
   private static final Map LONG_SHAPES;

   protected MapCodec getCodec() {
      return CODEC;
   }

   public PistonHeadBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(TYPE, PistonType.DEFAULT)).with(SHORT, false));
   }

   protected boolean hasSidedTransparency(BlockState state) {
      return true;
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return (VoxelShape)((Boolean)state.get(SHORT) ? SHORT_SHAPES : LONG_SHAPES).get(state.get(FACING));
   }

   private boolean isAttached(BlockState headState, BlockState pistonState) {
      Block block = headState.get(TYPE) == PistonType.DEFAULT ? Blocks.PISTON : Blocks.STICKY_PISTON;
      return pistonState.isOf(block) && (Boolean)pistonState.get(PistonBlock.EXTENDED) && pistonState.get(FACING) == headState.get(FACING);
   }

   public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
      if (!world.isClient && player.shouldSkipBlockDrops()) {
         BlockPos blockPos = pos.offset(((Direction)state.get(FACING)).getOpposite());
         if (this.isAttached(state, world.getBlockState(blockPos))) {
            world.breakBlock(blockPos, false);
         }
      }

      return super.onBreak(world, pos, state, player);
   }

   protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
      BlockPos blockPos = pos.offset(((Direction)state.get(FACING)).getOpposite());
      if (this.isAttached(state, world.getBlockState(blockPos))) {
         world.breakBlock(blockPos, true);
      }

   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      return direction.getOpposite() == state.get(FACING) && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
   }

   protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
      BlockState blockState = world.getBlockState(pos.offset(((Direction)state.get(FACING)).getOpposite()));
      return this.isAttached(state, blockState) || blockState.isOf(Blocks.MOVING_PISTON) && blockState.get(FACING) == state.get(FACING);
   }

   protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
      if (state.canPlaceAt(world, pos)) {
         world.updateNeighbor(pos.offset(((Direction)state.get(FACING)).getOpposite()), sourceBlock, OrientationHelper.withFrontNullable(wireOrientation, ((Direction)state.get(FACING)).getOpposite()));
      }

   }

   protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
      return new ItemStack(state.get(TYPE) == PistonType.STICKY ? Blocks.STICKY_PISTON : Blocks.PISTON);
   }

   protected BlockState rotate(BlockState state, BlockRotation rotation) {
      return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
   }

   protected BlockState mirror(BlockState state, BlockMirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(FACING, TYPE, SHORT);
   }

   protected boolean canPathfindThrough(BlockState state, NavigationType type) {
      return false;
   }

   static {
      TYPE = Properties.PISTON_TYPE;
      SHORT = Properties.SHORT;
      BASE_SHAPE = Block.createCuboidZShape(16.0, 0.0, 4.0);
      SHORT_SHAPES = VoxelShapes.createFacingShapeMap(VoxelShapes.union(BASE_SHAPE, Block.createCuboidZShape(4.0, 4.0, 16.0)));
      LONG_SHAPES = VoxelShapes.createFacingShapeMap(VoxelShapes.union(BASE_SHAPE, Block.createCuboidZShape(4.0, 4.0, 20.0)));
   }
}
