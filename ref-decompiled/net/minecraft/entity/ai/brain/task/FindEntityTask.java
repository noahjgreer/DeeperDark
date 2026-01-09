package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;

public class FindEntityTask {
   public static Task create(EntityType type, int maxDistance, MemoryModuleType targetModule, float speed, int completionRange) {
      return create(type, maxDistance, (entity) -> {
         return true;
      }, (entity) -> {
         return true;
      }, targetModule, speed, completionRange);
   }

   public static Task create(EntityType type, int maxDistance, Predicate entityPredicate, Predicate targetPredicate, MemoryModuleType targetModule, float speed, int completionRange) {
      int i = maxDistance * maxDistance;
      Predicate predicate = (entity) -> {
         return type.equals(entity.getType()) && targetPredicate.test(entity);
      };
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryOptional(targetModule), context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET), context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET), context.queryMemoryValue(MemoryModuleType.VISIBLE_MOBS)).apply(context, (targetValue, lookTarget, walkTarget, visibleMobs) -> {
            return (world, entity, time) -> {
               LivingTargetCache livingTargetCache = (LivingTargetCache)context.getValue(visibleMobs);
               if (entityPredicate.test(entity) && livingTargetCache.anyMatch(predicate)) {
                  Optional optional = livingTargetCache.findFirst((target) -> {
                     return target.squaredDistanceTo(entity) <= (double)i && predicate.test(target);
                  });
                  optional.ifPresent((target) -> {
                     targetValue.remember((Object)target);
                     lookTarget.remember((Object)(new EntityLookTarget(target, true)));
                     walkTarget.remember((Object)(new WalkTarget(new EntityLookTarget(target, false), speed, completionRange)));
                  });
                  return true;
               } else {
                  return false;
               }
            };
         });
      });
   }
}
