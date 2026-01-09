package net.minecraft.world.tick;

import net.minecraft.util.math.BlockPos;

public interface QueryableTickScheduler extends TickScheduler {
   boolean isTicking(BlockPos pos, Object type);
}
