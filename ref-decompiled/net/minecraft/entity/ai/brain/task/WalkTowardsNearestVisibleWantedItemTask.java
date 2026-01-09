package net.minecraft.entity.ai.brain.task;

import java.util.function.Predicate;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;

public class WalkTowardsNearestVisibleWantedItemTask {
   public static Task create(float speed, boolean requiresWalkTarget, int radius) {
      return create((entity) -> {
         return true;
      }, speed, requiresWalkTarget, radius);
   }

   public static Task create(Predicate startCondition, float speed, boolean requiresWalkTarget, int radius) {
      return TaskTriggerer.task((context) -> {
         TaskTriggerer taskTriggerer = requiresWalkTarget ? context.queryMemoryOptional(MemoryModuleType.WALK_TARGET) : context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET);
         return context.group(context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET), taskTriggerer, context.queryMemoryValue(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM), context.queryMemoryOptional(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS)).apply(context, (lookTarget, walkTarget, nearestVisibleWantedItem, itemPickupCooldownTicks) -> {
            return (world, entity, time) -> {
               ItemEntity itemEntity = (ItemEntity)context.getValue(nearestVisibleWantedItem);
               if (context.getOptionalValue(itemPickupCooldownTicks).isEmpty() && startCondition.test(entity) && itemEntity.isInRange(entity, (double)radius) && entity.getWorld().getWorldBorder().contains(itemEntity.getBlockPos()) && entity.canPickUpLoot()) {
                  WalkTarget walkTargetx = new WalkTarget(new EntityLookTarget(itemEntity, false), speed, 0);
                  lookTarget.remember((Object)(new EntityLookTarget(itemEntity, true)));
                  walkTarget.remember((Object)walkTargetx);
                  return true;
               } else {
                  return false;
               }
            };
         });
      });
   }
}
