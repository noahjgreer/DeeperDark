package net.minecraft.entity.ai.brain.task;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.PiglinBrain;

public class RemoveOffHandItemTask {
   public static Task create() {
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryAbsent(MemoryModuleType.ADMIRING_ITEM)).apply(context, (admiringItem) -> {
            return (world, entity, time) -> {
               if (!entity.getOffHandStack().isEmpty() && !entity.getOffHandStack().contains(DataComponentTypes.BLOCKS_ATTACKS)) {
                  PiglinBrain.consumeOffHandItem(world, entity, true);
                  return true;
               } else {
                  return false;
               }
            };
         });
      });
   }
}
