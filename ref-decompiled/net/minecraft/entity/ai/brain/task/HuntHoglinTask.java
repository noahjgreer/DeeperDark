package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.PiglinBrain;

public class HuntHoglinTask {
   public static SingleTickTask create() {
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryValue(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN), context.queryMemoryAbsent(MemoryModuleType.ANGRY_AT), context.queryMemoryAbsent(MemoryModuleType.HUNTED_RECENTLY), context.queryMemoryOptional(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS)).apply(context, (nearestVisibleHuntableHoglin, angryAt, huntedRecently, nearestVisibleAdultPiglins) -> {
            return (world, entity, time) -> {
               if (!entity.isBaby() && !context.getOptionalValue(nearestVisibleAdultPiglins).map((piglin) -> {
                  return piglin.stream().anyMatch(HuntHoglinTask::hasHuntedRecently);
               }).isPresent()) {
                  HoglinEntity hoglinEntity = (HoglinEntity)context.getValue(nearestVisibleHuntableHoglin);
                  PiglinBrain.becomeAngryWith(world, entity, hoglinEntity);
                  PiglinBrain.rememberHunting(entity);
                  PiglinBrain.angerAtCloserTargets(world, entity, hoglinEntity);
                  context.getOptionalValue(nearestVisibleAdultPiglins).ifPresent((piglin) -> {
                     piglin.forEach(PiglinBrain::rememberHunting);
                  });
                  return true;
               } else {
                  return false;
               }
            };
         });
      });
   }

   private static boolean hasHuntedRecently(AbstractPiglinEntity piglin) {
      return piglin.getBrain().hasMemoryModule(MemoryModuleType.HUNTED_RECENTLY);
   }
}
