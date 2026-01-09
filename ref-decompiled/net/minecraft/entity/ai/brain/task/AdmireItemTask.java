package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.PiglinBrain;

public class AdmireItemTask {
   public static Task create(int duration) {
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryValue(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM), context.queryMemoryAbsent(MemoryModuleType.ADMIRING_ITEM), context.queryMemoryAbsent(MemoryModuleType.ADMIRING_DISABLED), context.queryMemoryAbsent(MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM)).apply(context, (nearestVisibleWantedItem, admiringItem, admiringDisabled, disableWalkToAdmireItem) -> {
            return (world, entity, time) -> {
               ItemEntity itemEntity = (ItemEntity)context.getValue(nearestVisibleWantedItem);
               if (!PiglinBrain.isGoldenItem(itemEntity.getStack())) {
                  return false;
               } else {
                  admiringItem.remember(true, (long)duration);
                  return true;
               }
            };
         });
      });
   }
}
