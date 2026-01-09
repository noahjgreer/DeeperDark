package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public abstract class SingleTickTask implements Task, TaskRunnable {
   private MultiTickTask.Status status;

   public SingleTickTask() {
      this.status = MultiTickTask.Status.STOPPED;
   }

   public final MultiTickTask.Status getStatus() {
      return this.status;
   }

   public final boolean tryStarting(ServerWorld world, LivingEntity entity, long time) {
      if (this.trigger(world, entity, time)) {
         this.status = MultiTickTask.Status.RUNNING;
         return true;
      } else {
         return false;
      }
   }

   public final void tick(ServerWorld world, LivingEntity entity, long time) {
      this.stop(world, entity, time);
   }

   public final void stop(ServerWorld world, LivingEntity entity, long time) {
      this.status = MultiTickTask.Status.STOPPED;
   }

   public String getName() {
      return this.getClass().getSimpleName();
   }
}
