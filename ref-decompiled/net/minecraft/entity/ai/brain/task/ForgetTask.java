package net.minecraft.entity.ai.brain.task;

import java.util.function.Predicate;
import net.minecraft.entity.ai.brain.MemoryModuleType;

public class ForgetTask {
   public static Task create(Predicate condition, MemoryModuleType memory) {
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryValue(memory)).apply(context, (queryResult) -> {
            return (world, entity, time) -> {
               if (condition.test(entity)) {
                  queryResult.forget();
                  return true;
               } else {
                  return false;
               }
            };
         });
      });
   }
}
