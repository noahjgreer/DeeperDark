package net.minecraft.entity.ai.brain.task;

import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;

public class GoToLookTargetTask {
   public static SingleTickTask create(float speed, int completionRange) {
      return create((entity) -> {
         return true;
      }, (entity) -> {
         return speed;
      }, completionRange);
   }

   public static SingleTickTask create(Predicate predicate, Function speed, int completionRange) {
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET), context.queryMemoryValue(MemoryModuleType.LOOK_TARGET)).apply(context, (walkTarget, lookTarget) -> {
            return (world, entity, time) -> {
               if (!predicate.test(entity)) {
                  return false;
               } else {
                  walkTarget.remember((Object)(new WalkTarget((LookTarget)context.getValue(lookTarget), (Float)speed.apply(entity), completionRange)));
                  return true;
               }
            };
         });
      });
   }
}
