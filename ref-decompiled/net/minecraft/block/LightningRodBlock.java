package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ParticleUtil;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.OrientationHelper;
import net.minecraft.world.tick.ScheduledTickView;

public class LightningRodBlock extends RodBlock implements Waterloggable {
   public static final MapCodec CODEC = createCodec(LightningRodBlock::new);
   public static final BooleanProperty WATERLOGGED;
   public static final BooleanProperty POWERED;
   private static final int SCHEDULED_TICK_DELAY = 8;
   public static final int MAX_REDIRECT_DISTANCE = 128;
   private static final int field_31191 = 200;

   public MapCodec getCodec() {
      return CODEC;
   }

   public LightningRodBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.UP)).with(WATERLOGGED, false)).with(POWERED, false));
   }

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
      boolean bl = fluidState.getFluid() == Fluids.WATER;
      return (BlockState)((BlockState)this.getDefaultState().with(FACING, ctx.getSide())).with(WATERLOGGED, bl);
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

   protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
      return (Boolean)state.get(POWERED) ? 15 : 0;
   }

   protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
      return (Boolean)state.get(POWERED) && state.get(FACING) == direction ? 15 : 0;
   }

   public void setPowered(BlockState state, World world, BlockPos pos) {
      world.setBlockState(pos, (BlockState)state.with(POWERED, true), 3);
      this.updateNeighbors(state, world, pos);
      world.scheduleBlockTick(pos, this, 8);
      world.syncWorldEvent(3002, pos, ((Direction)state.get(FACING)).getAxis().ordinal());
   }

   private void updateNeighbors(BlockState state, World world, BlockPos pos) {
      Direction direction = ((Direction)state.get(FACING)).getOpposite();
      world.updateNeighborsAlways(pos.offset(direction), this, OrientationHelper.getEmissionOrientation(world, direction, (Direction)null));
   }

   protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      world.setBlockState(pos, (BlockState)state.with(POWERED, false), 3);
      this.updateNeighbors(state, world, pos);
   }

   public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
      if (world.isThundering() && (long)world.random.nextInt(200) <= world.getTime() % 200L && pos.getY() == world.getTopY(Heightmap.Type.WORLD_SURFACE, pos.getX(), pos.getZ()) - 1) {
         ParticleUtil.spawnParticle(((Direction)state.get(FACING)).getAxis(), world, pos, 0.125, ParticleTypes.ELECTRIC_SPARK, UniformIntProvider.create(1, 2));
      }
   }

   protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
      if ((Boolean)state.get(POWERED)) {
         this.updateNeighbors(state, world, pos);
      }

   }

   protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
      if (!state.isOf(oldState.getBlock())) {
         if ((Boolean)state.get(POWERED) && !world.getBlockTickScheduler().isQueued(pos, this)) {
            world.setBlockState(pos, (BlockState)state.with(POWERED, false), 18);
         }

      }
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(FACING, POWERED, WATERLOGGED);
   }

   protected boolean emitsRedstonePower(BlockState state) {
      return true;
   }

   static {
      WATERLOGGED = Properties.WATERLOGGED;
      POWERED = Properties.POWERED;
   }
}
