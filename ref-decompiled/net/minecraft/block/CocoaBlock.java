package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
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
import org.jetbrains.annotations.Nullable;

public class CocoaBlock extends HorizontalFacingBlock implements Fertilizable {
   public static final MapCodec CODEC = createCodec(CocoaBlock::new);
   public static final int MAX_AGE = 2;
   public static final IntProperty AGE;
   private static final List SHAPES;

   public MapCodec getCodec() {
      return CODEC;
   }

   public CocoaBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(AGE, 0));
   }

   protected boolean hasRandomTicks(BlockState state) {
      return (Integer)state.get(AGE) < 2;
   }

   protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if (world.random.nextInt(5) == 0) {
         int i = (Integer)state.get(AGE);
         if (i < 2) {
            world.setBlockState(pos, (BlockState)state.with(AGE, i + 1), 2);
         }
      }

   }

   protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
      BlockState blockState = world.getBlockState(pos.offset((Direction)state.get(FACING)));
      return blockState.isIn(BlockTags.JUNGLE_LOGS);
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return (VoxelShape)((Map)SHAPES.get((Integer)state.get(AGE))).get(state.get(FACING));
   }

   @Nullable
   public BlockState getPlacementState(ItemPlacementContext ctx) {
      BlockState blockState = this.getDefaultState();
      WorldView worldView = ctx.getWorld();
      BlockPos blockPos = ctx.getBlockPos();
      Direction[] var5 = ctx.getPlacementDirections();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Direction direction = var5[var7];
         if (direction.getAxis().isHorizontal()) {
            blockState = (BlockState)blockState.with(FACING, direction);
            if (blockState.canPlaceAt(worldView, blockPos)) {
               return blockState;
            }
         }
      }

      return null;
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      return direction == state.get(FACING) && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
   }

   public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
      return (Integer)state.get(AGE) < 2;
   }

   public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
      return true;
   }

   public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
      world.setBlockState(pos, (BlockState)state.with(AGE, (Integer)state.get(AGE) + 1), 2);
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(FACING, AGE);
   }

   protected boolean canPathfindThrough(BlockState state, NavigationType type) {
      return false;
   }

   static {
      AGE = Properties.AGE_2;
      SHAPES = IntStream.rangeClosed(0, 2).mapToObj((age) -> {
         return VoxelShapes.createHorizontalFacingShapeMap(Block.createColumnShape((double)(4 + age * 2), (double)(7 - age * 2), 12.0).offset(0.0, 0.0, (double)(age - 5) / 16.0).simplify());
      }).toList();
   }
}
