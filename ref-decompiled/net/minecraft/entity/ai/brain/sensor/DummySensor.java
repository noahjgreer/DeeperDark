package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public class DummySensor extends Sensor {
   protected void sense(ServerWorld world, LivingEntity entity) {
   }

   public Set getOutputMemoryModules() {
      return ImmutableSet.of();
   }
}
