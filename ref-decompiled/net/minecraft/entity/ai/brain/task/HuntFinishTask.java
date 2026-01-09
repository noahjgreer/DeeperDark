package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.PiglinBrain;

public class HuntFinishTask {
   public static Task create() {
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryValue(MemoryModuleType.ATTACK_TARGET), context.queryMemoryOptional(MemoryModuleType.HUNTED_RECENTLY)).apply(context, (attackTarget, huntedRecently) -> {
            return (world, entity, time) -> {
               LivingEntity livingEntity = (LivingEntity)context.getValue(attackTarget);
               if (livingEntity.getType() == EntityType.HOGLIN && livingEntity.isDead()) {
                  huntedRecently.remember(true, (long)PiglinBrain.HUNT_MEMORY_DURATION.get(entity.getWorld().random));
               }

               return true;
            };
         });
      });
   }
}
