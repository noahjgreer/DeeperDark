package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.OrientationHelper;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.tick.ScheduledTickView;

public class ObserverBlock extends FacingBlock {
   public static final MapCodec CODEC = createCodec(ObserverBlock::new);
   public static final BooleanProperty POWERED;

   public MapCodec getCodec() {
      return CODEC;
   }

   public ObserverBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.SOUTH)).with(POWERED, false));
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(FACING, POWERED);
   }

   protected BlockState rotate(BlockState state, BlockRotation rotation) {
      return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
   }

   protected BlockState mirror(BlockState state, BlockMirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
   }

   protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if ((Boolean)state.get(POWERED)) {
         world.setBlockState(pos, (BlockState)state.with(POWERED, false), 2);
      } else {
         world.setBlockState(pos, (BlockState)state.with(POWERED, true), 2);
         world.scheduleBlockTick(pos, this, 2);
      }

      this.updateNeighbors(world, pos, state);
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      if (state.get(FACING) == direction && !(Boolean)state.get(POWERED)) {
         this.scheduleTick(world, tickView, pos);
      }

      return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
   }

   private void scheduleTick(WorldView world, ScheduledTickView tickView, BlockPos pos) {
      if (!world.isClient() && !tickView.getBlockTickScheduler().isQueued(pos, this)) {
         tickView.scheduleBlockTick(pos, this, 2);
      }

   }

   protected void updateNeighbors(World world, BlockPos pos, BlockState state) {
      Direction direction = (Direction)state.get(FACING);
      BlockPos blockPos = pos.offset(direction.getOpposite());
      WireOrientation wireOrientation = OrientationHelper.getEmissionOrientation(world, direction.getOpposite(), (Direction)null);
      world.updateNeighbor(blockPos, this, wireOrientation);
      world.updateNeighborsExcept(blockPos, this, direction, wireOrientation);
   }

   protected boolean emitsRedstonePower(BlockState state) {
      return true;
   }

   protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
      return state.getWeakRedstonePower(world, pos, direction);
   }

   protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
      return (Boolean)state.get(POWERED) && state.get(FACING) == direction ? 15 : 0;
   }

   protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
      if (!state.isOf(oldState.getBlock())) {
         if (!world.isClient() && (Boolean)state.get(POWERED) && !world.getBlockTickScheduler().isQueued(pos, this)) {
            BlockState blockState = (BlockState)state.with(POWERED, false);
            world.setBlockState(pos, blockState, 18);
            this.updateNeighbors(world, pos, blockState);
         }

      }
   }

   protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
      if ((Boolean)state.get(POWERED) && world.getBlockTickScheduler().isQueued(pos, this)) {
         this.updateNeighbors(world, pos, (BlockState)state.with(POWERED, false));
      }

   }

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      return (BlockState)this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite().getOpposite());
   }

   static {
      POWERED = Properties.POWERED;
   }
}
