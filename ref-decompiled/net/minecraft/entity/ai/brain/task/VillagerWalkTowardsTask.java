package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;

public class VillagerWalkTowardsTask {
   public static SingleTickTask create(MemoryModuleType destination, float speed, int completionRange, int maxDistance, int maxRunTime) {
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryOptional(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE), context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET), context.queryMemoryValue(destination)).apply(context, (cantReachWalkTargetSince, walkTarget, destinationResult) -> {
            return (world, entity, time) -> {
               GlobalPos globalPos = (GlobalPos)context.getValue(destinationResult);
               Optional optional = context.getOptionalValue(cantReachWalkTargetSince);
               if (globalPos.dimension() == world.getRegistryKey() && (!optional.isPresent() || world.getTime() - (Long)optional.get() <= (long)maxRunTime)) {
                  if (globalPos.pos().getManhattanDistance(entity.getBlockPos()) > maxDistance) {
                     Vec3d vec3d = null;
                     int l = 0;
                     int m = true;

                     while(vec3d == null || BlockPos.ofFloored(vec3d).getManhattanDistance(entity.getBlockPos()) > maxDistance) {
                        vec3d = NoPenaltyTargeting.findTo(entity, 15, 7, Vec3d.ofBottomCenter(globalPos.pos()), 1.5707963705062866);
                        ++l;
                        if (l == 1000) {
                           entity.releaseTicketFor(destination);
                           destinationResult.forget();
                           cantReachWalkTargetSince.remember((Object)time);
                           return true;
                        }
                     }

                     walkTarget.remember((Object)(new WalkTarget(vec3d, speed, completionRange)));
                  } else if (globalPos.pos().getManhattanDistance(entity.getBlockPos()) > completionRange) {
                     walkTarget.remember((Object)(new WalkTarget(globalPos.pos(), speed, completionRange)));
                  }
               } else {
                  entity.releaseTicketFor(destination);
                  destinationResult.forget();
                  cantReachWalkTargetSince.remember((Object)time);
               }

               return true;
            };
         });
      });
   }
}
