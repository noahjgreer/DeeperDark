package net.minecraft.block;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.block.enums.Tilt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

public class BigDripleafBlock extends HorizontalFacingBlock implements Fertilizable, Waterloggable {
   public static final MapCodec CODEC = createCodec(BigDripleafBlock::new);
   private static final BooleanProperty WATERLOGGED;
   private static final EnumProperty TILT;
   private static final int field_31015 = -1;
   private static final Object2IntMap NEXT_TILT_DELAYS;
   private static final int field_31016 = 5;
   private static final int field_31018 = 11;
   private static final int field_31019 = 13;
   private static final Map SHAPES_BY_TILT;
   private final Function shapeFunction;

   public MapCodec getCodec() {
      return CODEC;
   }

   public BigDripleafBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(WATERLOGGED, false)).with(FACING, Direction.NORTH)).with(TILT, Tilt.NONE));
      this.shapeFunction = this.createShapeFunction();
   }

   private Function createShapeFunction() {
      Map map = VoxelShapes.createHorizontalFacingShapeMap(Block.createColumnShape(6.0, 0.0, 13.0).offset(0.0, 0.0, 0.25).simplify());
      return this.createShapeFunction((state) -> {
         return VoxelShapes.union((VoxelShape)SHAPES_BY_TILT.get(state.get(TILT)), (VoxelShape)map.get(state.get(FACING)));
      }, new Property[]{WATERLOGGED});
   }

   public static void grow(WorldAccess world, Random random, BlockPos pos, Direction direction) {
      int i = MathHelper.nextInt(random, 2, 5);
      BlockPos.Mutable mutable = pos.mutableCopy();
      int j = 0;

      while(j < i && canGrowInto(world, mutable, world.getBlockState(mutable))) {
         ++j;
         mutable.move(Direction.UP);
      }

      int k = pos.getY() + j - 1;
      mutable.setY(pos.getY());

      while(mutable.getY() < k) {
         BigDripleafStemBlock.placeStemAt(world, mutable, world.getFluidState(mutable), direction);
         mutable.move(Direction.UP);
      }

      placeDripleafAt(world, mutable, world.getFluidState(mutable), direction);
   }

   private static boolean canGrowInto(BlockState state) {
      return state.isAir() || state.isOf(Blocks.WATER) || state.isOf(Blocks.SMALL_DRIPLEAF);
   }

   protected static boolean canGrowInto(HeightLimitView world, BlockPos pos, BlockState state) {
      return !world.isOutOfHeightLimit(pos) && canGrowInto(state);
   }

   protected static boolean placeDripleafAt(WorldAccess world, BlockPos pos, FluidState fluidState, Direction direction) {
      BlockState blockState = (BlockState)((BlockState)Blocks.BIG_DRIPLEAF.getDefaultState().with(WATERLOGGED, fluidState.isEqualAndStill(Fluids.WATER))).with(FACING, direction);
      return world.setBlockState(pos, blockState, 3);
   }

   protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
      this.changeTilt(state, world, hit.getBlockPos(), Tilt.FULL, SoundEvents.BLOCK_BIG_DRIPLEAF_TILT_DOWN);
   }

   protected FluidState getFluidState(BlockState state) {
      return (Boolean)state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
   }

   protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
      BlockPos blockPos = pos.down();
      BlockState blockState = world.getBlockState(blockPos);
      return blockState.isOf(this) || blockState.isOf(Blocks.BIG_DRIPLEAF_STEM) || blockState.isIn(BlockTags.BIG_DRIPLEAF_PLACEABLE);
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      if (direction == Direction.DOWN && !state.canPlaceAt(world, pos)) {
         return Blocks.AIR.getDefaultState();
      } else {
         if ((Boolean)state.get(WATERLOGGED)) {
            tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
         }

         return direction == Direction.UP && neighborState.isOf(this) ? Blocks.BIG_DRIPLEAF_STEM.getStateWithProperties(state) : super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
      }
   }

   public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
      BlockState blockState = world.getBlockState(pos.up());
      return canGrowInto(blockState);
   }

   public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
      return true;
   }

   public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
      BlockPos blockPos = pos.up();
      BlockState blockState = world.getBlockState(blockPos);
      if (canGrowInto(world, blockPos, blockState)) {
         Direction direction = (Direction)state.get(FACING);
         BigDripleafStemBlock.placeStemAt(world, pos, state.getFluidState(), direction);
         placeDripleafAt(world, blockPos, blockState.getFluidState(), direction);
      }

   }

   protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler) {
      if (!world.isClient) {
         if (state.get(TILT) == Tilt.NONE && isEntityAbove(pos, entity) && !world.isReceivingRedstonePower(pos)) {
            this.changeTilt(state, world, pos, Tilt.UNSTABLE, (SoundEvent)null);
         }

      }
   }

   protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if (world.isReceivingRedstonePower(pos)) {
         resetTilt(state, world, pos);
      } else {
         Tilt tilt = (Tilt)state.get(TILT);
         if (tilt == Tilt.UNSTABLE) {
            this.changeTilt(state, world, pos, Tilt.PARTIAL, SoundEvents.BLOCK_BIG_DRIPLEAF_TILT_DOWN);
         } else if (tilt == Tilt.PARTIAL) {
            this.changeTilt(state, world, pos, Tilt.FULL, SoundEvents.BLOCK_BIG_DRIPLEAF_TILT_DOWN);
         } else if (tilt == Tilt.FULL) {
            resetTilt(state, world, pos);
         }

      }
   }

   protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
      if (world.isReceivingRedstonePower(pos)) {
         resetTilt(state, world, pos);
      }

   }

   private static void playTiltSound(World world, BlockPos pos, SoundEvent soundEvent) {
      float f = MathHelper.nextBetween(world.random, 0.8F, 1.2F);
      world.playSound((Entity)null, pos, soundEvent, SoundCategory.BLOCKS, 1.0F, f);
   }

   private static boolean isEntityAbove(BlockPos pos, Entity entity) {
      return entity.isOnGround() && entity.getPos().y > (double)((float)pos.getY() + 0.6875F);
   }

   private void changeTilt(BlockState state, World world, BlockPos pos, Tilt tilt, @Nullable SoundEvent sound) {
      changeTilt(state, world, pos, tilt);
      if (sound != null) {
         playTiltSound(world, pos, sound);
      }

      int i = NEXT_TILT_DELAYS.getInt(tilt);
      if (i != -1) {
         world.scheduleBlockTick(pos, this, i);
      }

   }

   private static void resetTilt(BlockState state, World world, BlockPos pos) {
      changeTilt(state, world, pos, Tilt.NONE);
      if (state.get(TILT) != Tilt.NONE) {
         playTiltSound(world, pos, SoundEvents.BLOCK_BIG_DRIPLEAF_TILT_UP);
      }

   }

   private static void changeTilt(BlockState state, World world, BlockPos pos, Tilt tilt) {
      Tilt tilt2 = (Tilt)state.get(TILT);
      world.setBlockState(pos, (BlockState)state.with(TILT, tilt), 2);
      if (tilt.isStable() && tilt != tilt2) {
         world.emitGameEvent((Entity)null, GameEvent.BLOCK_CHANGE, pos);
      }

   }

   protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return (VoxelShape)SHAPES_BY_TILT.get(state.get(TILT));
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return (VoxelShape)this.shapeFunction.apply(state);
   }

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos().down());
      FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
      boolean bl = blockState.isOf(Blocks.BIG_DRIPLEAF) || blockState.isOf(Blocks.BIG_DRIPLEAF_STEM);
      return (BlockState)((BlockState)this.getDefaultState().with(WATERLOGGED, fluidState.isEqualAndStill(Fluids.WATER))).with(FACING, bl ? (Direction)blockState.get(FACING) : ctx.getHorizontalPlayerFacing().getOpposite());
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(WATERLOGGED, FACING, TILT);
   }

   static {
      WATERLOGGED = Properties.WATERLOGGED;
      TILT = Properties.TILT;
      NEXT_TILT_DELAYS = (Object2IntMap)Util.make(new Object2IntArrayMap(), (delays) -> {
         delays.defaultReturnValue(-1);
         delays.put(Tilt.UNSTABLE, 10);
         delays.put(Tilt.PARTIAL, 10);
         delays.put(Tilt.FULL, 100);
      });
      SHAPES_BY_TILT = Maps.newEnumMap(Map.of(Tilt.NONE, Block.createColumnShape(16.0, 11.0, 15.0), Tilt.UNSTABLE, Block.createColumnShape(16.0, 11.0, 15.0), Tilt.PARTIAL, Block.createColumnShape(16.0, 11.0, 13.0), Tilt.FULL, VoxelShapes.empty()));
   }
}
