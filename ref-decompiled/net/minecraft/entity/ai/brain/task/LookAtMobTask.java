package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;

public class LookAtMobTask {
   public static Task create(SpawnGroup spawnGroup, float maxDistance) {
      return create((entity) -> {
         return spawnGroup.equals(entity.getType().getSpawnGroup());
      }, maxDistance);
   }

   public static SingleTickTask create(EntityType type, float maxDistance) {
      return create((entity) -> {
         return type.equals(entity.getType());
      }, maxDistance);
   }

   public static SingleTickTask create(float maxDistance) {
      return create((entity) -> {
         return true;
      }, maxDistance);
   }

   public static SingleTickTask create(Predicate predicate, float maxDistance) {
      float f = maxDistance * maxDistance;
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryAbsent(MemoryModuleType.LOOK_TARGET), context.queryMemoryValue(MemoryModuleType.VISIBLE_MOBS)).apply(context, (lookTarget, visibleMobs) -> {
            return (world, entity, time) -> {
               Optional optional = ((LivingTargetCache)context.getValue(visibleMobs)).findFirst(predicate.and((target) -> {
                  return target.squaredDistanceTo(entity) <= (double)f && !entity.hasPassenger(target);
               }));
               if (optional.isEmpty()) {
                  return false;
               } else {
                  lookTarget.remember((Object)(new EntityLookTarget((Entity)optional.get(), true)));
                  return true;
               }
            };
         });
      });
   }
}
