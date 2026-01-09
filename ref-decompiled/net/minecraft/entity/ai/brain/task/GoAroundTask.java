package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.util.math.GlobalPos;
import org.apache.commons.lang3.mutable.MutableLong;

public class GoAroundTask {
   private static final int UPDATE_INTERVAL = 180;
   private static final int HORIZONTAL_RANGE = 8;
   private static final int VERTICAL_RANGE = 6;

   public static SingleTickTask create(MemoryModuleType posModule, float walkSpeed, int maxDistance) {
      MutableLong mutableLong = new MutableLong(0L);
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryOptional(MemoryModuleType.WALK_TARGET), context.queryMemoryValue(posModule)).apply(context, (walkTarget, pos) -> {
            return (world, entity, time) -> {
               GlobalPos globalPos = (GlobalPos)context.getValue(pos);
               if (world.getRegistryKey() == globalPos.dimension() && globalPos.pos().isWithinDistance(entity.getPos(), (double)maxDistance)) {
                  if (time <= mutableLong.getValue()) {
                     return true;
                  } else {
                     Optional optional = Optional.ofNullable(FuzzyTargeting.find(entity, 8, 6));
                     walkTarget.remember(optional.map((targetPos) -> {
                        return new WalkTarget(targetPos, walkSpeed, 1);
                     }));
                     mutableLong.setValue(time + 180L);
                     return true;
                  }
               } else {
                  return false;
               }
            };
         });
      });
   }
}
