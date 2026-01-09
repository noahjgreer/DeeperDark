package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Map;

public class RandomTask extends CompositeTask {
   public RandomTask(List tasks) {
      this(ImmutableMap.of(), tasks);
   }

   public RandomTask(Map requiredMemoryState, List tasks) {
      super(requiredMemoryState, ImmutableSet.of(), CompositeTask.Order.SHUFFLED, CompositeTask.RunMode.RUN_ONE, tasks);
   }
}
