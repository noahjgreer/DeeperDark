package net.minecraft.entity.ai.brain.task;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;

public class FindRoarTargetTask {
   public static Task create(Function targetFinder) {
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryAbsent(MemoryModuleType.ROAR_TARGET), context.queryMemoryAbsent(MemoryModuleType.ATTACK_TARGET), context.queryMemoryOptional(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)).apply(context, (roarTarget, attackTarget, cantReachWalkTargetSince) -> {
            return (world, entity, time) -> {
               Optional optional = (Optional)targetFinder.apply(entity);
               Objects.requireNonNull(entity);
               if (optional.filter(entity::isValidTarget).isEmpty()) {
                  return false;
               } else {
                  roarTarget.remember((Object)((LivingEntity)optional.get()));
                  cantReachWalkTargetSince.forget();
                  return true;
               }
            };
         });
      });
   }
}
