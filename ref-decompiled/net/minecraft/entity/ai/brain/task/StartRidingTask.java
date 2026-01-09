package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;

public class StartRidingTask {
   private static final int COMPLETION_RANGE = 1;

   public static Task create(float speed) {
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET), context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET), context.queryMemoryValue(MemoryModuleType.RIDE_TARGET)).apply(context, (lookTarget, walkTarget, rideTarget) -> {
            return (world, entity, time) -> {
               if (entity.hasVehicle()) {
                  return false;
               } else {
                  Entity entity2 = (Entity)context.getValue(rideTarget);
                  if (entity2.isInRange(entity, 1.0)) {
                     entity.startRiding(entity2);
                  } else {
                     lookTarget.remember((Object)(new EntityLookTarget(entity2, true)));
                     walkTarget.remember((Object)(new WalkTarget(new EntityLookTarget(entity2, false), speed, 1)));
                  }

                  return true;
               }
            };
         });
      });
   }
}
