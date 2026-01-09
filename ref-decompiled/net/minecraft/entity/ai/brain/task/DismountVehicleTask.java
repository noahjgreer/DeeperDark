package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public class DismountVehicleTask extends MultiTickTask {
   public DismountVehicleTask() {
      super(ImmutableMap.of());
   }

   protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
      return entity.hasVehicle();
   }

   protected void run(ServerWorld world, LivingEntity entity, long time) {
      entity.detach();
   }
}
