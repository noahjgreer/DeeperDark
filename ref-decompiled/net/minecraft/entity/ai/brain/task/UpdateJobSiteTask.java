package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.village.VillagerProfession;

public class UpdateJobSiteTask {
   public static Task create() {
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryValue(MemoryModuleType.POTENTIAL_JOB_SITE), context.queryMemoryOptional(MemoryModuleType.JOB_SITE)).apply(context, (potentialJobSite, jobSite) -> {
            return (world, entity, time) -> {
               GlobalPos globalPos = (GlobalPos)context.getValue(potentialJobSite);
               if (!globalPos.pos().isWithinDistance(entity.getPos(), 2.0) && !entity.isNatural()) {
                  return false;
               } else {
                  potentialJobSite.forget();
                  jobSite.remember((Object)globalPos);
                  world.sendEntityStatus(entity, (byte)14);
                  if (!entity.getVillagerData().profession().matchesKey(VillagerProfession.NONE)) {
                     return true;
                  } else {
                     MinecraftServer minecraftServer = world.getServer();
                     Optional.ofNullable(minecraftServer.getWorld(globalPos.dimension())).flatMap((jobSiteWorld) -> {
                        return jobSiteWorld.getPointOfInterestStorage().getType(globalPos.pos());
                     }).flatMap((poiType) -> {
                        return Registries.VILLAGER_PROFESSION.streamEntries().filter((profession) -> {
                           return ((VillagerProfession)profession.value()).heldWorkstation().test(poiType);
                        }).findFirst();
                     }).ifPresent((profession) -> {
                        entity.setVillagerData(entity.getVillagerData().withProfession(profession));
                        entity.reinitializeBrain(world);
                     });
                     return true;
                  }
               }
            };
         });
      });
   }
}
