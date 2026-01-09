package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public interface Task {
   MultiTickTask.Status getStatus();

   boolean tryStarting(ServerWorld world, LivingEntity entity, long time);

   void tick(ServerWorld world, LivingEntity entity, long time);

   void stop(ServerWorld world, LivingEntity entity, long time);

   String getName();
}
