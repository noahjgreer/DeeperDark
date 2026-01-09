package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.world.GameRules;

public class ForgetAngryAtTargetTask {
   public static Task create() {
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryValue(MemoryModuleType.ANGRY_AT)).apply(context, (angryAt) -> {
            return (world, entity, time) -> {
               Optional.ofNullable(world.getEntity((UUID)context.getValue(angryAt))).map((target) -> {
                  LivingEntity var10000;
                  if (target instanceof LivingEntity livingEntity) {
                     var10000 = livingEntity;
                  } else {
                     var10000 = null;
                  }

                  return var10000;
               }).filter(LivingEntity::isDead).filter((target) -> {
                  return target.getType() != EntityType.PLAYER || world.getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS);
               }).ifPresent((target) -> {
                  angryAt.forget();
               });
               return true;
            };
         });
      });
   }
}
