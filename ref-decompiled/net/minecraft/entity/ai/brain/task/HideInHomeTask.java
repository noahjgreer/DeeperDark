package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;

public class HideInHomeTask {
   public static SingleTickTask create(int maxDistance, float walkSpeed, int preferredDistance) {
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET), context.queryMemoryOptional(MemoryModuleType.HOME), context.queryMemoryOptional(MemoryModuleType.HIDING_PLACE), context.queryMemoryOptional(MemoryModuleType.PATH), context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET), context.queryMemoryOptional(MemoryModuleType.BREED_TARGET), context.queryMemoryOptional(MemoryModuleType.INTERACTION_TARGET)).apply(context, (walkTarget, home, hidingPlace, path, lookTarget, breedTarget, interactionTarget) -> {
            return (world, entity, time) -> {
               world.getPointOfInterestStorage().getPosition((poiType) -> {
                  return poiType.matchesKey(PointOfInterestTypes.HOME);
               }, (pos) -> {
                  return true;
               }, entity.getBlockPos(), preferredDistance + 1, PointOfInterestStorage.OccupationStatus.ANY).filter((pos) -> {
                  return pos.isWithinDistance(entity.getPos(), (double)preferredDistance);
               }).or(() -> {
                  return world.getPointOfInterestStorage().getPosition((poiType) -> {
                     return poiType.matchesKey(PointOfInterestTypes.HOME);
                  }, (pos) -> {
                     return true;
                  }, PointOfInterestStorage.OccupationStatus.ANY, entity.getBlockPos(), maxDistance, entity.getRandom());
               }).or(() -> {
                  return context.getOptionalValue(home).map(GlobalPos::pos);
               }).ifPresent((pos) -> {
                  path.forget();
                  lookTarget.forget();
                  breedTarget.forget();
                  interactionTarget.forget();
                  hidingPlace.remember((Object)GlobalPos.create(world.getRegistryKey(), pos));
                  if (!pos.isWithinDistance(entity.getPos(), (double)preferredDistance)) {
                     walkTarget.remember((Object)(new WalkTarget(pos, walkSpeed, preferredDistance)));
                  }

               });
               return true;
            };
         });
      });
   }
}
