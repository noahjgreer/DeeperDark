package net.minecraft.entity.ai.brain.task;

import java.util.Collections;
import java.util.List;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;

public class GoIndoorsTask {
   public static Task create(float speed) {
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET)).apply(context, (walkTarget) -> {
            return (world, entity, time) -> {
               if (world.isSkyVisible(entity.getBlockPos())) {
                  return false;
               } else {
                  BlockPos blockPos = entity.getBlockPos();
                  List list = (List)BlockPos.stream(blockPos.add(-1, -1, -1), blockPos.add(1, 1, 1)).map(BlockPos::toImmutable).collect(Util.toArrayList());
                  Collections.shuffle(list);
                  list.stream().filter((pos) -> {
                     return !world.isSkyVisible(pos);
                  }).filter((pos) -> {
                     return world.isTopSolid(pos, entity);
                  }).filter((pos) -> {
                     return world.isSpaceEmpty(entity);
                  }).findFirst().ifPresent((pos) -> {
                     walkTarget.remember((Object)(new WalkTarget(pos, speed, 0)));
                  });
                  return true;
               }
            };
         });
      });
   }
}
