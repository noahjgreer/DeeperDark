package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;

public class WalkTowardsLookTargetTask {
   public static Task create(Function lookTargetFunction, Predicate predicate, int completionRange, int searchRange, float speed) {
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET), context.queryMemoryOptional(MemoryModuleType.WALK_TARGET)).apply(context, (lookTarget, walkTarget) -> {
            return (world, entity, time) -> {
               Optional optional = (Optional)lookTargetFunction.apply(entity);
               if (!optional.isEmpty() && predicate.test(entity)) {
                  LookTarget lookTargetx = (LookTarget)optional.get();
                  if (entity.getPos().isInRange(lookTargetx.getPos(), (double)searchRange)) {
                     return false;
                  } else {
                     LookTarget lookTarget2 = (LookTarget)optional.get();
                     lookTarget.remember((Object)lookTarget2);
                     walkTarget.remember((Object)(new WalkTarget(lookTarget2, speed, completionRange)));
                     return true;
                  }
               } else {
                  return false;
               }
            };
         });
      });
   }
}
