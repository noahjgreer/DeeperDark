package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;

public class FindInteractionTargetTask {
   public static Task create(EntityType type, int maxDistance) {
      int i = maxDistance * maxDistance;
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET), context.queryMemoryAbsent(MemoryModuleType.INTERACTION_TARGET), context.queryMemoryValue(MemoryModuleType.VISIBLE_MOBS)).apply(context, (lookTarget, interactionTarget, visibleMobs) -> {
            return (world, entity, time) -> {
               Optional optional = ((LivingTargetCache)context.getValue(visibleMobs)).findFirst((target) -> {
                  return target.squaredDistanceTo(entity) <= (double)i && type.equals(target.getType());
               });
               if (optional.isEmpty()) {
                  return false;
               } else {
                  LivingEntity livingEntity = (LivingEntity)optional.get();
                  interactionTarget.remember((Object)livingEntity);
                  lookTarget.remember((Object)(new EntityLookTarget(livingEntity, true)));
                  return true;
               }
            };
         });
      });
   }
}
