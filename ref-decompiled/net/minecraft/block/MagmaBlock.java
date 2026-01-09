package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class MagmaBlock extends Block {
   public static final MapCodec CODEC = createCodec(MagmaBlock::new);
   private static final int SCHEDULED_TICK_DELAY = 20;

   public MapCodec getCodec() {
      return CODEC;
   }

   public MagmaBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
      if (!entity.bypassesSteppingEffects() && entity instanceof LivingEntity) {
         entity.serverDamage(world.getDamageSources().hotFloor(), 1.0F);
      }

      super.onSteppedOn(world, pos, state, entity);
   }

   protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      BubbleColumnBlock.update(world, pos.up(), state);
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      if (direction == Direction.UP && neighborState.isOf(Blocks.WATER)) {
         tickView.scheduleBlockTick(pos, this, 20);
      }

      return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
   }

   protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
      world.scheduleBlockTick(pos, this, 20);
   }
}
