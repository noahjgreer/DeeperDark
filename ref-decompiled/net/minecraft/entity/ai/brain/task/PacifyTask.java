package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ai.brain.MemoryModuleType;

public class PacifyTask {
   public static Task create(MemoryModuleType requiredMemory, int duration) {
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryOptional(MemoryModuleType.ATTACK_TARGET), context.queryMemoryAbsent(MemoryModuleType.PACIFIED), context.queryMemoryValue(requiredMemory)).apply(context, context.supply(() -> {
            return "[BecomePassive if " + String.valueOf(requiredMemory) + " present]";
         }, (attackTarget, pacified, requiredMemoryResult) -> {
            return (world, entity, time) -> {
               pacified.remember(true, (long)duration);
               attackTarget.forget();
               return true;
            };
         }));
      });
   }
}
