package net.minecraft.world.tick;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;

public interface ScheduledTickView {
   OrderedTick createOrderedTick(BlockPos pos, Object type, int delay, TickPriority priority);

   OrderedTick createOrderedTick(BlockPos pos, Object type, int delay);

   QueryableTickScheduler getBlockTickScheduler();

   default void scheduleBlockTick(BlockPos pos, Block block, int delay, TickPriority priority) {
      this.getBlockTickScheduler().scheduleTick(this.createOrderedTick(pos, block, delay, priority));
   }

   default void scheduleBlockTick(BlockPos pos, Block block, int delay) {
      this.getBlockTickScheduler().scheduleTick(this.createOrderedTick(pos, block, delay));
   }

   QueryableTickScheduler getFluidTickScheduler();

   default void scheduleFluidTick(BlockPos pos, Fluid fluid, int delay, TickPriority priority) {
      this.getFluidTickScheduler().scheduleTick(this.createOrderedTick(pos, fluid, delay, priority));
   }

   default void scheduleFluidTick(BlockPos pos, Fluid fluid, int delay) {
      this.getFluidTickScheduler().scheduleTick(this.createOrderedTick(pos, fluid, delay));
   }
}
