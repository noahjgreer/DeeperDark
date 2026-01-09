package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public class UpdateAttackTargetTask {
   public static Task create(TargetGetter targetGetter) {
      return create((world, entity) -> {
         return true;
      }, targetGetter);
   }

   public static Task create(StartCondition condition, TargetGetter targetGetter) {
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryAbsent(MemoryModuleType.ATTACK_TARGET), context.queryMemoryOptional(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)).apply(context, (attackTarget, cantReachWalkTargetSince) -> {
            return (world, entity, time) -> {
               if (!condition.test(world, entity)) {
                  return false;
               } else {
                  Optional optional = targetGetter.get(world, entity);
                  if (optional.isEmpty()) {
                     return false;
                  } else {
                     LivingEntity livingEntity = (LivingEntity)optional.get();
                     if (!entity.canTarget(livingEntity)) {
                        return false;
                     } else {
                        attackTarget.remember((Object)livingEntity);
                        cantReachWalkTargetSince.forget();
                        return true;
                     }
                  }
               }
            };
         });
      });
   }

   @FunctionalInterface
   public interface StartCondition {
      boolean test(ServerWorld world, Object entity);
   }

   @FunctionalInterface
   public interface TargetGetter {
      Optional get(ServerWorld world, Object entity);
   }
}
