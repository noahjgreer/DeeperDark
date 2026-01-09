package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

public class FrostedIceBlock extends IceBlock {
   public static final MapCodec CODEC = createCodec(FrostedIceBlock::new);
   public static final int MAX_AGE = 3;
   public static final IntProperty AGE;
   private static final int NEIGHBORS_CHECKED_ON_SCHEDULED_TICK = 4;
   private static final int NEIGHBORS_CHECKED_ON_NEIGHBOR_UPDATE = 2;

   public MapCodec getCodec() {
      return CODEC;
   }

   public FrostedIceBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(AGE, 0));
   }

   public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
      world.scheduleBlockTick(pos, this, MathHelper.nextInt(world.getRandom(), 60, 120));
   }

   protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if ((random.nextInt(3) == 0 || this.canMelt(world, pos, 4)) && world.getLightLevel(pos) > 11 - (Integer)state.get(AGE) - state.getOpacity() && this.increaseAge(state, world, pos)) {
         BlockPos.Mutable mutable = new BlockPos.Mutable();
         Direction[] var6 = Direction.values();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            Direction direction = var6[var8];
            mutable.set(pos, (Direction)direction);
            BlockState blockState = world.getBlockState(mutable);
            if (blockState.isOf(this) && !this.increaseAge(blockState, world, mutable)) {
               world.scheduleBlockTick(mutable, this, MathHelper.nextInt(random, 20, 40));
            }
         }

      } else {
         world.scheduleBlockTick(pos, this, MathHelper.nextInt(random, 20, 40));
      }
   }

   private boolean increaseAge(BlockState state, World world, BlockPos pos) {
      int i = (Integer)state.get(AGE);
      if (i < 3) {
         world.setBlockState(pos, (BlockState)state.with(AGE, i + 1), 2);
         return false;
      } else {
         this.melt(state, world, pos);
         return true;
      }
   }

   protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
      if (sourceBlock.getDefaultState().isOf(this) && this.canMelt(world, pos, 2)) {
         this.melt(state, world, pos);
      }

      super.neighborUpdate(state, world, pos, sourceBlock, wireOrientation, notify);
   }

   private boolean canMelt(BlockView world, BlockPos pos, int maxNeighbors) {
      int i = 0;
      BlockPos.Mutable mutable = new BlockPos.Mutable();
      Direction[] var6 = Direction.values();
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Direction direction = var6[var8];
         mutable.set(pos, (Direction)direction);
         if (world.getBlockState(mutable).isOf(this)) {
            ++i;
            if (i >= maxNeighbors) {
               return false;
            }
         }
      }

      return true;
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(AGE);
   }

   protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
      return ItemStack.EMPTY;
   }

   static {
      AGE = Properties.AGE_3;
   }
}
