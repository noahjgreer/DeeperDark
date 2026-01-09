package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ai.brain.Activity;

public class WakeUpTask {
   public static Task create() {
      return TaskTriggerer.task((context) -> {
         return context.point((world, entity, time) -> {
            if (!entity.getBrain().hasActivity(Activity.REST) && entity.isSleeping()) {
               entity.wakeUp();
               return true;
            } else {
               return false;
            }
         });
      });
   }
}
