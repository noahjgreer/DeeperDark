package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.util.math.BlockPos;

public class LookAtDisturbanceTask {
   public static Task create() {
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET), context.queryMemoryOptional(MemoryModuleType.DISTURBANCE_LOCATION), context.queryMemoryOptional(MemoryModuleType.ROAR_TARGET), context.queryMemoryAbsent(MemoryModuleType.ATTACK_TARGET)).apply(context, (lookTarget, disturbanceLocation, roarTarget, attackTarget) -> {
            return (world, entity, time) -> {
               Optional optional = context.getOptionalValue(roarTarget).map(Entity::getBlockPos).or(() -> {
                  return context.getOptionalValue(disturbanceLocation);
               });
               if (optional.isEmpty()) {
                  return false;
               } else {
                  lookTarget.remember((Object)(new BlockPosLookTarget((BlockPos)optional.get())));
                  return true;
               }
            };
         });
      });
   }
}
