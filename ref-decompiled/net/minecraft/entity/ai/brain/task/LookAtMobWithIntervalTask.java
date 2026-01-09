package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;

/** @deprecated */
@Deprecated
public class LookAtMobWithIntervalTask {
   public static Task follow(float maxDistance, UniformIntProvider interval) {
      return follow(maxDistance, interval, (entity) -> {
         return true;
      });
   }

   public static Task follow(EntityType type, float maxDistance, UniformIntProvider interval) {
      return follow(maxDistance, interval, (entity) -> {
         return type.equals(entity.getType());
      });
   }

   private static Task follow(float maxDistance, UniformIntProvider interval, Predicate predicate) {
      float f = maxDistance * maxDistance;
      Interval interval2 = new Interval(interval);
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryAbsent(MemoryModuleType.LOOK_TARGET), context.queryMemoryValue(MemoryModuleType.VISIBLE_MOBS)).apply(context, (lookTarget, visibleMobs) -> {
            return (world, entity, time) -> {
               Optional optional = ((LivingTargetCache)context.getValue(visibleMobs)).findFirst(predicate.and((other) -> {
                  return other.squaredDistanceTo(entity) <= (double)f;
               }));
               if (optional.isEmpty()) {
                  return false;
               } else if (!interval2.shouldRun(world.random)) {
                  return false;
               } else {
                  lookTarget.remember((Object)(new EntityLookTarget((Entity)optional.get(), true)));
                  return true;
               }
            };
         });
      });
   }

   public static final class Interval {
      private final UniformIntProvider interval;
      private int remainingTicks;

      public Interval(UniformIntProvider interval) {
         if (interval.getMin() <= 1) {
            throw new IllegalArgumentException();
         } else {
            this.interval = interval;
         }
      }

      public boolean shouldRun(Random random) {
         if (this.remainingTicks == 0) {
            this.remainingTicks = this.interval.get(random) - 1;
            return false;
         } else {
            return --this.remainingTicks == 0;
         }
      }
   }
}
