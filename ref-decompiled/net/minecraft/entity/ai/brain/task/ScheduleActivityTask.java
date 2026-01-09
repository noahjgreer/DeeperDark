package net.minecraft.entity.ai.brain.task;

public class ScheduleActivityTask {
   public static Task create() {
      return TaskTriggerer.task((context) -> {
         return context.point((world, entity, time) -> {
            entity.getBrain().refreshActivities(world.getTimeOfDay(), world.getTime());
            return true;
         });
      });
   }
}
