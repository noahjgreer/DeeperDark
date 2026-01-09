package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;

public class RangedApproachTask {
   private static final int WEAPON_REACH_REDUCTION = 1;

   public static Task create(float speed) {
      return create((entity) -> {
         return speed;
      });
   }

   public static Task create(Function speed) {
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryOptional(MemoryModuleType.WALK_TARGET), context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET), context.queryMemoryValue(MemoryModuleType.ATTACK_TARGET), context.queryMemoryOptional(MemoryModuleType.VISIBLE_MOBS)).apply(context, (walkTarget, lookTarget, attackTarget, visibleMobs) -> {
            return (world, entity, time) -> {
               LivingEntity livingEntity = (LivingEntity)context.getValue(attackTarget);
               Optional optional = context.getOptionalValue(visibleMobs);
               if (optional.isPresent() && ((LivingTargetCache)optional.get()).contains(livingEntity) && TargetUtil.isTargetWithinAttackRange(entity, livingEntity, 1)) {
                  walkTarget.forget();
               } else {
                  lookTarget.remember((Object)(new EntityLookTarget(livingEntity, true)));
                  walkTarget.remember((Object)(new WalkTarget(new EntityLookTarget(livingEntity, false), (Float)speed.apply(entity), 0)));
               }

               return true;
            };
         });
      });
   }
}
