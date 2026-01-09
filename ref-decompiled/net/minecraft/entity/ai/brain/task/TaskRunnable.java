package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public interface TaskRunnable {
   boolean trigger(ServerWorld world, LivingEntity entity, long time);
}
