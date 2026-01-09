package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.enums.BambooLeaves;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

public class BambooBlock extends Block implements Fertilizable {
   public static final MapCodec CODEC = createCodec(BambooBlock::new);
   private static final VoxelShape SMALL_LEAVES_SHAPE = Block.createColumnShape(6.0, 0.0, 16.0);
   private static final VoxelShape LARGE_LEAVES_SHAPE = Block.createColumnShape(10.0, 0.0, 16.0);
   private static final VoxelShape NO_LEAVES_SHAPE = Block.createColumnShape(3.0, 0.0, 16.0);
   public static final IntProperty AGE;
   public static final EnumProperty LEAVES;
   public static final IntProperty STAGE;
   public static final int field_31000 = 16;
   public static final int field_31001 = 0;
   public static final int field_31002 = 1;
   public static final int field_31003 = 0;
   public static final int field_31004 = 1;

   public MapCodec getCodec() {
      return CODEC;
   }

   public BambooBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(AGE, 0)).with(LEAVES, BambooLeaves.NONE)).with(STAGE, 0));
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(AGE, LEAVES, STAGE);
   }

   protected boolean isTransparent(BlockState state) {
      return true;
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      VoxelShape voxelShape = state.get(LEAVES) == BambooLeaves.LARGE ? LARGE_LEAVES_SHAPE : SMALL_LEAVES_SHAPE;
      return voxelShape.offset(state.getModelOffset(pos));
   }

   protected boolean canPathfindThrough(BlockState state, NavigationType type) {
      return false;
   }

   protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return NO_LEAVES_SHAPE.offset(state.getModelOffset(pos));
   }

   protected boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
      return false;
   }

   @Nullable
   public BlockState getPlacementState(ItemPlacementContext ctx) {
      FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
      if (!fluidState.isEmpty()) {
         return null;
      } else {
         BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos().down());
         if (blockState.isIn(BlockTags.BAMBOO_PLANTABLE_ON)) {
            if (blockState.isOf(Blocks.BAMBOO_SAPLING)) {
               return (BlockState)this.getDefaultState().with(AGE, 0);
            } else if (blockState.isOf(Blocks.BAMBOO)) {
               int i = (Integer)blockState.get(AGE) > 0 ? 1 : 0;
               return (BlockState)this.getDefaultState().with(AGE, i);
            } else {
               BlockState blockState2 = ctx.getWorld().getBlockState(ctx.getBlockPos().up());
               return blockState2.isOf(Blocks.BAMBOO) ? (BlockState)this.getDefaultState().with(AGE, (Integer)blockState2.get(AGE)) : Blocks.BAMBOO_SAPLING.getDefaultState();
            }
         } else {
            return null;
         }
      }
   }

   protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if (!state.canPlaceAt(world, pos)) {
         world.breakBlock(pos, true);
      }

   }

   protected boolean hasRandomTicks(BlockState state) {
      return (Integer)state.get(STAGE) == 0;
   }

   protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if ((Integer)state.get(STAGE) == 0) {
         if (random.nextInt(3) == 0 && world.isAir(pos.up()) && world.getBaseLightLevel(pos.up(), 0) >= 9) {
            int i = this.countBambooBelow(world, pos) + 1;
            if (i < 16) {
               this.updateLeaves(state, world, pos, random, i);
            }
         }

      }
   }

   protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
      return world.getBlockState(pos.down()).isIn(BlockTags.BAMBOO_PLANTABLE_ON);
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      if (!state.canPlaceAt(world, pos)) {
         tickView.scheduleBlockTick(pos, this, 1);
      }

      return direction == Direction.UP && neighborState.isOf(Blocks.BAMBOO) && (Integer)neighborState.get(AGE) > (Integer)state.get(AGE) ? (BlockState)state.cycle(AGE) : super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
   }

   public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
      int i = this.countBambooAbove(world, pos);
      int j = this.countBambooBelow(world, pos);
      return i + j + 1 < 16 && (Integer)world.getBlockState(pos.up(i)).get(STAGE) != 1;
   }

   public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
      return true;
   }

   public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
      int i = this.countBambooAbove(world, pos);
      int j = this.countBambooBelow(world, pos);
      int k = i + j + 1;
      int l = 1 + random.nextInt(2);

      for(int m = 0; m < l; ++m) {
         BlockPos blockPos = pos.up(i);
         BlockState blockState = world.getBlockState(blockPos);
         if (k >= 16 || (Integer)blockState.get(STAGE) == 1 || !world.isAir(blockPos.up())) {
            return;
         }

         this.updateLeaves(blockState, world, blockPos, random, k);
         ++i;
         ++k;
      }

   }

   protected void updateLeaves(BlockState state, World world, BlockPos pos, Random random, int height) {
      BlockState blockState = world.getBlockState(pos.down());
      BlockPos blockPos = pos.down(2);
      BlockState blockState2 = world.getBlockState(blockPos);
      BambooLeaves bambooLeaves = BambooLeaves.NONE;
      if (height >= 1) {
         if (blockState.isOf(Blocks.BAMBOO) && blockState.get(LEAVES) != BambooLeaves.NONE) {
            if (blockState.isOf(Blocks.BAMBOO) && blockState.get(LEAVES) != BambooLeaves.NONE) {
               bambooLeaves = BambooLeaves.LARGE;
               if (blockState2.isOf(Blocks.BAMBOO)) {
                  world.setBlockState(pos.down(), (BlockState)blockState.with(LEAVES, BambooLeaves.SMALL), 3);
                  world.setBlockState(blockPos, (BlockState)blockState2.with(LEAVES, BambooLeaves.NONE), 3);
               }
            }
         } else {
            bambooLeaves = BambooLeaves.SMALL;
         }
      }

      int i = (Integer)state.get(AGE) != 1 && !blockState2.isOf(Blocks.BAMBOO) ? 0 : 1;
      int j = (height < 11 || !(random.nextFloat() < 0.25F)) && height != 15 ? 0 : 1;
      world.setBlockState(pos.up(), (BlockState)((BlockState)((BlockState)this.getDefaultState().with(AGE, i)).with(LEAVES, bambooLeaves)).with(STAGE, j), 3);
   }

   protected int countBambooAbove(BlockView world, BlockPos pos) {
      int i;
      for(i = 0; i < 16 && world.getBlockState(pos.up(i + 1)).isOf(Blocks.BAMBOO); ++i) {
      }

      return i;
   }

   protected int countBambooBelow(BlockView world, BlockPos pos) {
      int i;
      for(i = 0; i < 16 && world.getBlockState(pos.down(i + 1)).isOf(Blocks.BAMBOO); ++i) {
      }

      return i;
   }

   static {
      AGE = Properties.AGE_1;
      LEAVES = Properties.BAMBOO_LEAVES;
      STAGE = Properties.STAGE;
   }
}
