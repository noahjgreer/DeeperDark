package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.village.raid.Raid;

public class HideWhenBellRingsTask {
   public static Task create() {
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryValue(MemoryModuleType.HEARD_BELL_TIME)).apply(context, (heardBellTime) -> {
            return (world, entity, time) -> {
               Raid raid = world.getRaidAt(entity.getBlockPos());
               if (raid == null) {
                  entity.getBrain().doExclusively(Activity.HIDE);
               }

               return true;
            };
         });
      });
   }
}
