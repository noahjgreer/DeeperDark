package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ai.brain.MemoryModuleType;

public class PlayDeadTimerTask {
   public static Task create() {
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryValue(MemoryModuleType.PLAY_DEAD_TICKS), context.queryMemoryOptional(MemoryModuleType.HURT_BY_ENTITY)).apply(context, (playDeadTicks, hurtByEntity) -> {
            return (world, entity, time) -> {
               int i = (Integer)context.getValue(playDeadTicks);
               if (i <= 0) {
                  playDeadTicks.forget();
                  hurtByEntity.forget();
                  entity.getBrain().resetPossibleActivities();
               } else {
                  playDeadTicks.remember((Object)(i - 1));
               }

               return true;
            };
         });
      });
   }
}
