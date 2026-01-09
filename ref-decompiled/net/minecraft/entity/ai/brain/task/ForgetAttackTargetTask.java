package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public class ForgetAttackTargetTask {
   private static final int REMEMBER_TIME = 200;

   public static Task create(ForgetCallback callback) {
      return create((world, target) -> {
         return false;
      }, callback, true);
   }

   public static Task create(AlternativeCondition condition) {
      return create(condition, (world, entity, target) -> {
      }, true);
   }

   public static Task create() {
      return create((world, target) -> {
         return false;
      }, (world, entity, target) -> {
      }, true);
   }

   public static Task create(AlternativeCondition condition, ForgetCallback callback, boolean shouldForgetIfTargetUnreachable) {
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryValue(MemoryModuleType.ATTACK_TARGET), context.queryMemoryOptional(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)).apply(context, (attackTarget, cantReachWalkTargetSince) -> {
            return (world, entity, time) -> {
               LivingEntity livingEntity = (LivingEntity)context.getValue(attackTarget);
               if (entity.canTarget(livingEntity) && (!shouldForgetIfTargetUnreachable || !cannotReachTarget(entity, context.getOptionalValue(cantReachWalkTargetSince))) && livingEntity.isAlive() && livingEntity.getWorld() == entity.getWorld() && !condition.test(world, livingEntity)) {
                  return true;
               } else {
                  callback.accept(world, entity, livingEntity);
                  attackTarget.forget();
                  return true;
               }
            };
         });
      });
   }

   private static boolean cannotReachTarget(LivingEntity target, Optional lastReachTime) {
      return lastReachTime.isPresent() && target.getWorld().getTime() - (Long)lastReachTime.get() > 200L;
   }

   @FunctionalInterface
   public interface AlternativeCondition {
      boolean test(ServerWorld world, LivingEntity target);
   }

   @FunctionalInterface
   public interface ForgetCallback {
      void accept(ServerWorld world, Object entity, LivingEntity target);
   }
}
