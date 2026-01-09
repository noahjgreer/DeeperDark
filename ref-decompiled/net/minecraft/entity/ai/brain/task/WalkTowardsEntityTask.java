package net.minecraft.entity.ai.brain.task;

import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class WalkTowardsEntityTask {
   public static SingleTickTask createNearestVisibleAdult(UniformIntProvider executionRange, float speed) {
      return create(executionRange, (entity) -> {
         return speed;
      }, MemoryModuleType.NEAREST_VISIBLE_ADULT, false);
   }

   public static SingleTickTask create(UniformIntProvider executionRange, Function speed, MemoryModuleType targetType, boolean eyeHeight) {
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryValue(targetType), context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET), context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET)).apply(context, (target, lookTarget, walkTarget) -> {
            return (world, entity, time) -> {
               if (!entity.isBaby()) {
                  return false;
               } else {
                  LivingEntity livingEntity = (LivingEntity)context.getValue(target);
                  if (entity.isInRange(livingEntity, (double)(executionRange.getMax() + 1)) && !entity.isInRange(livingEntity, (double)executionRange.getMin())) {
                     WalkTarget walkTargetx = new WalkTarget(new EntityLookTarget(livingEntity, eyeHeight, eyeHeight), (Float)speed.apply(entity), executionRange.getMin() - 1);
                     lookTarget.remember((Object)(new EntityLookTarget(livingEntity, true, eyeHeight)));
                     walkTarget.remember((Object)walkTargetx);
                     return true;
                  } else {
                     return false;
                  }
               }
            };
         });
      });
   }
}
