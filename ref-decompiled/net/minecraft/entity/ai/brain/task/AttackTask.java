package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.util.math.MathHelper;

public class AttackTask {
   public static SingleTickTask create(int distance, float forwardMovement) {
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET), context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET), context.queryMemoryValue(MemoryModuleType.ATTACK_TARGET), context.queryMemoryValue(MemoryModuleType.VISIBLE_MOBS)).apply(context, (walkTarget, lookTarget, attackTarget, visibleMobs) -> {
            return (world, entity, time) -> {
               LivingEntity livingEntity = (LivingEntity)context.getValue(attackTarget);
               if (livingEntity.isInRange(entity, (double)distance) && ((LivingTargetCache)context.getValue(visibleMobs)).contains(livingEntity)) {
                  lookTarget.remember((Object)(new EntityLookTarget(livingEntity, true)));
                  entity.getMoveControl().strafeTo(-forwardMovement, 0.0F);
                  entity.setYaw(MathHelper.clampAngle(entity.getYaw(), entity.headYaw, 0.0F));
                  return true;
               } else {
                  return false;
               }
            };
         });
      });
   }
}
