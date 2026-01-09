package net.minecraft.world.tick;

import net.minecraft.util.math.BlockPos;

public interface TickScheduler {
   void scheduleTick(OrderedTick orderedTick);

   boolean isQueued(BlockPos pos, Object type);

   int getTickCount();
}
