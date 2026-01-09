package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.MemoryQueryResult;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class PlayWithVillagerBabiesTask {
   private static final int HORIZONTAL_RANGE = 20;
   private static final int VERTICAL_RANGE = 8;
   private static final float WALK_SPEED = 0.6F;
   private static final float PLAYING_WALK_SPEED = 0.6F;
   private static final int MAX_BABY_INTERACTION_COUNT = 5;
   private static final int RUN_CHANCE = 10;

   public static Task create() {
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryValue(MemoryModuleType.VISIBLE_VILLAGER_BABIES), context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET), context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET), context.queryMemoryOptional(MemoryModuleType.INTERACTION_TARGET)).apply(context, (visibleVillagerBabies, walkTarget, lookTarget, interactionTarget) -> {
            return (world, entity, time) -> {
               if (world.getRandom().nextInt(10) != 0) {
                  return false;
               } else {
                  List list = (List)context.getValue(visibleVillagerBabies);
                  Optional optional = list.stream().filter((baby) -> {
                     return isInteractionTargetOf(entity, baby);
                  }).findAny();
                  if (!optional.isPresent()) {
                     Optional optional2 = getLeastPopularBabyInteractionTarget(list);
                     if (optional2.isPresent()) {
                        setPlayTarget(interactionTarget, lookTarget, walkTarget, (LivingEntity)optional2.get());
                        return true;
                     } else {
                        list.stream().findAny().ifPresent((baby) -> {
                           setPlayTarget(interactionTarget, lookTarget, walkTarget, baby);
                        });
                        return true;
                     }
                  } else {
                     for(int i = 0; i < 10; ++i) {
                        Vec3d vec3d = FuzzyTargeting.find(entity, 20, 8);
                        if (vec3d != null && world.isNearOccupiedPointOfInterest(BlockPos.ofFloored(vec3d))) {
                           walkTarget.remember((Object)(new WalkTarget(vec3d, 0.6F, 0)));
                           break;
                        }
                     }

                     return true;
                  }
               }
            };
         });
      });
   }

   private static void setPlayTarget(MemoryQueryResult interactionTarget, MemoryQueryResult lookTarget, MemoryQueryResult walkTarget, LivingEntity baby) {
      interactionTarget.remember((Object)baby);
      lookTarget.remember((Object)(new EntityLookTarget(baby, true)));
      walkTarget.remember((Object)(new WalkTarget(new EntityLookTarget(baby, false), 0.6F, 1)));
   }

   private static Optional getLeastPopularBabyInteractionTarget(List babies) {
      Map map = getBabyInteractionTargetCounts(babies);
      return map.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).filter((entry) -> {
         return (Integer)entry.getValue() > 0 && (Integer)entry.getValue() <= 5;
      }).map(Map.Entry::getKey).findFirst();
   }

   private static Map getBabyInteractionTargetCounts(List babies) {
      Map map = Maps.newHashMap();
      babies.stream().filter(PlayWithVillagerBabiesTask::hasInteractionTarget).forEach((baby) -> {
         map.compute(getInteractionTarget(baby), (target, count) -> {
            return count == null ? 1 : count + 1;
         });
      });
      return map;
   }

   private static LivingEntity getInteractionTarget(LivingEntity baby) {
      return (LivingEntity)baby.getBrain().getOptionalRegisteredMemory(MemoryModuleType.INTERACTION_TARGET).get();
   }

   private static boolean hasInteractionTarget(LivingEntity baby) {
      return baby.getBrain().getOptionalRegisteredMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
   }

   private static boolean isInteractionTargetOf(LivingEntity entity, LivingEntity baby) {
      return baby.getBrain().getOptionalRegisteredMemory(MemoryModuleType.INTERACTION_TARGET).filter((target) -> {
         return target == entity;
      }).isPresent();
   }
}
