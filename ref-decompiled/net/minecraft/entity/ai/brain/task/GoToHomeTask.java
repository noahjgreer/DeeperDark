package net.minecraft.entity.ai.brain.task;

import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;

public class GoToHomeTask {
   private static final int POI_EXPIRY = 40;
   private static final int MAX_TRIES = 5;
   private static final int RUN_TIME = 20;
   private static final int MAX_DISTANCE = 4;

   public static Task create(float speed) {
      Long2LongMap long2LongMap = new Long2LongOpenHashMap();
      MutableLong mutableLong = new MutableLong(0L);
      return TaskTriggerer.task((taskContext) -> {
         return taskContext.group(taskContext.queryMemoryAbsent(MemoryModuleType.WALK_TARGET), taskContext.queryMemoryAbsent(MemoryModuleType.HOME)).apply(taskContext, (walkTarget, home) -> {
            return (world, entity, time) -> {
               if (world.getTime() - mutableLong.getValue() < 20L) {
                  return false;
               } else {
                  PointOfInterestStorage pointOfInterestStorage = world.getPointOfInterestStorage();
                  Optional optional = pointOfInterestStorage.getNearestPosition((poiType) -> {
                     return poiType.matchesKey(PointOfInterestTypes.HOME);
                  }, entity.getBlockPos(), 48, PointOfInterestStorage.OccupationStatus.ANY);
                  if (!optional.isEmpty() && !(((BlockPos)optional.get()).getSquaredDistance(entity.getBlockPos()) <= 4.0)) {
                     MutableInt mutableInt = new MutableInt(0);
                     mutableLong.setValue(world.getTime() + (long)world.getRandom().nextInt(20));
                     Predicate predicate = (pos) -> {
                        long l = pos.asLong();
                        if (long2LongMap.containsKey(l)) {
                           return false;
                        } else if (mutableInt.incrementAndGet() >= 5) {
                           return false;
                        } else {
                           long2LongMap.put(l, mutableLong.getValue() + 40L);
                           return true;
                        }
                     };
                     Set set = (Set)pointOfInterestStorage.getTypesAndPositions((poiType) -> {
                        return poiType.matchesKey(PointOfInterestTypes.HOME);
                     }, predicate, entity.getBlockPos(), 48, PointOfInterestStorage.OccupationStatus.ANY).collect(Collectors.toSet());
                     Path path = FindPointOfInterestTask.findPathToPoi(entity, set);
                     if (path != null && path.reachesTarget()) {
                        BlockPos blockPos = path.getTarget();
                        Optional optional2 = pointOfInterestStorage.getType(blockPos);
                        if (optional2.isPresent()) {
                           walkTarget.remember((Object)(new WalkTarget(blockPos, speed, 1)));
                           DebugInfoSender.sendPointOfInterest(world, blockPos);
                        }
                     } else if (mutableInt.getValue() < 5) {
                        long2LongMap.long2LongEntrySet().removeIf((entry) -> {
                           return entry.getLongValue() < mutableLong.getValue();
                        });
                     }

                     return true;
                  } else {
                     return false;
                  }
               }
            };
         });
      });
   }
}
