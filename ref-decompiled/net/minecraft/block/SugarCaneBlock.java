package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class SugarCaneBlock extends Block {
   public static final MapCodec CODEC = createCodec(SugarCaneBlock::new);
   public static final IntProperty AGE;
   private static final VoxelShape SHAPE;

   public MapCodec getCodec() {
      return CODEC;
   }

   public SugarCaneBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(AGE, 0));
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return SHAPE;
   }

   protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if (!state.canPlaceAt(world, pos)) {
         world.breakBlock(pos, true);
      }

   }

   protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if (world.isAir(pos.up())) {
         int i;
         for(i = 1; world.getBlockState(pos.down(i)).isOf(this); ++i) {
         }

         if (i < 3) {
            int j = (Integer)state.get(AGE);
            if (j == 15) {
               world.setBlockState(pos.up(), this.getDefaultState());
               world.setBlockState(pos, (BlockState)state.with(AGE, 0), 260);
            } else {
               world.setBlockState(pos, (BlockState)state.with(AGE, j + 1), 260);
            }
         }
      }

   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      if (!state.canPlaceAt(world, pos)) {
         tickView.scheduleBlockTick(pos, this, 1);
      }

      return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
   }

   protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
      BlockState blockState = world.getBlockState(pos.down());
      if (blockState.isOf(this)) {
         return true;
      } else {
         if (blockState.isIn(BlockTags.DIRT) || blockState.isIn(BlockTags.SAND)) {
            BlockPos blockPos = pos.down();
            Iterator var6 = Direction.Type.HORIZONTAL.iterator();

            while(var6.hasNext()) {
               Direction direction = (Direction)var6.next();
               BlockState blockState2 = world.getBlockState(blockPos.offset(direction));
               FluidState fluidState = world.getFluidState(blockPos.offset(direction));
               if (fluidState.isIn(FluidTags.WATER) || blockState2.isOf(Blocks.FROSTED_ICE)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(AGE);
   }

   static {
      AGE = Properties.AGE_15;
      SHAPE = Block.createColumnShape(12.0, 0.0, 16.0);
   }
}
