package net.minecraft.entity.ai.brain.task;

import java.util.Iterator;
import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public abstract class MultiTickTask implements Task {
   public static final int DEFAULT_RUN_TIME = 60;
   protected final Map requiredMemoryStates;
   private Status status;
   private long endTime;
   private final int minRunTime;
   private final int maxRunTime;

   public MultiTickTask(Map requiredMemoryState) {
      this(requiredMemoryState, 60);
   }

   public MultiTickTask(Map requiredMemoryState, int runTime) {
      this(requiredMemoryState, runTime, runTime);
   }

   public MultiTickTask(Map requiredMemoryState, int minRunTime, int maxRunTime) {
      this.status = MultiTickTask.Status.STOPPED;
      this.minRunTime = minRunTime;
      this.maxRunTime = maxRunTime;
      this.requiredMemoryStates = requiredMemoryState;
   }

   public Status getStatus() {
      return this.status;
   }

   public final boolean tryStarting(ServerWorld world, LivingEntity entity, long time) {
      if (this.hasRequiredMemoryState(entity) && this.shouldRun(world, entity)) {
         this.status = MultiTickTask.Status.RUNNING;
         int i = this.minRunTime + world.getRandom().nextInt(this.maxRunTime + 1 - this.minRunTime);
         this.endTime = time + (long)i;
         this.run(world, entity, time);
         return true;
      } else {
         return false;
      }
   }

   protected void run(ServerWorld world, LivingEntity entity, long time) {
   }

   public final void tick(ServerWorld world, LivingEntity entity, long time) {
      if (!this.isTimeLimitExceeded(time) && this.shouldKeepRunning(world, entity, time)) {
         this.keepRunning(world, entity, time);
      } else {
         this.stop(world, entity, time);
      }

   }

   protected void keepRunning(ServerWorld world, LivingEntity entity, long time) {
   }

   public final void stop(ServerWorld world, LivingEntity entity, long time) {
      this.status = MultiTickTask.Status.STOPPED;
      this.finishRunning(world, entity, time);
   }

   protected void finishRunning(ServerWorld world, LivingEntity entity, long time) {
   }

   protected boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
      return false;
   }

   protected boolean isTimeLimitExceeded(long time) {
      return time > this.endTime;
   }

   protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
      return true;
   }

   public String getName() {
      return this.getClass().getSimpleName();
   }

   protected boolean hasRequiredMemoryState(LivingEntity entity) {
      Iterator var2 = this.requiredMemoryStates.entrySet().iterator();

      MemoryModuleType memoryModuleType;
      MemoryModuleState memoryModuleState;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         Map.Entry entry = (Map.Entry)var2.next();
         memoryModuleType = (MemoryModuleType)entry.getKey();
         memoryModuleState = (MemoryModuleState)entry.getValue();
      } while(entity.getBrain().isMemoryInState(memoryModuleType, memoryModuleState));

      return false;
   }

   public static enum Status {
      STOPPED,
      RUNNING;

      // $FF: synthetic method
      private static Status[] method_36615() {
         return new Status[]{STOPPED, RUNNING};
      }
   }
}
