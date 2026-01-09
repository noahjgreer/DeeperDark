package net.minecraft.entity.ai.brain.task;

import java.util.List;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.village.VillagerProfession;

public class WorkStationCompetitionTask {
   public static Task create() {
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryValue(MemoryModuleType.JOB_SITE), context.queryMemoryValue(MemoryModuleType.MOBS)).apply(context, (jobSite, mobs) -> {
            return (world, entity, time) -> {
               GlobalPos globalPos = (GlobalPos)context.getValue(jobSite);
               world.getPointOfInterestStorage().getType(globalPos.pos()).ifPresent((poiType) -> {
                  ((List)context.getValue(mobs)).stream().filter((mob) -> {
                     return mob instanceof VillagerEntity && mob != entity;
                  }).map((villager) -> {
                     return (VillagerEntity)villager;
                  }).filter(LivingEntity::isAlive).filter((villager) -> {
                     return isUsingWorkStationAt(globalPos, poiType, villager);
                  }).reduce(entity, WorkStationCompetitionTask::keepJobSiteForMoreExperiencedVillager);
               });
               return true;
            };
         });
      });
   }

   private static VillagerEntity keepJobSiteForMoreExperiencedVillager(VillagerEntity first, VillagerEntity second) {
      VillagerEntity villagerEntity;
      VillagerEntity villagerEntity2;
      if (first.getExperience() > second.getExperience()) {
         villagerEntity = first;
         villagerEntity2 = second;
      } else {
         villagerEntity = second;
         villagerEntity2 = first;
      }

      villagerEntity2.getBrain().forget(MemoryModuleType.JOB_SITE);
      return villagerEntity;
   }

   private static boolean isUsingWorkStationAt(GlobalPos pos, RegistryEntry poiType, VillagerEntity villager) {
      Optional optional = villager.getBrain().getOptionalRegisteredMemory(MemoryModuleType.JOB_SITE);
      return optional.isPresent() && pos.equals(optional.get()) && isCompletedWorkStation(poiType, villager.getVillagerData().profession());
   }

   private static boolean isCompletedWorkStation(RegistryEntry poiType, RegistryEntry profession) {
      return ((VillagerProfession)profession.value()).heldWorkstation().test(poiType);
   }
}
