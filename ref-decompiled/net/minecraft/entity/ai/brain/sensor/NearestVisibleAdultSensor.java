package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public class NearestVisibleAdultSensor extends Sensor {
   public Set getOutputMemoryModules() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.VISIBLE_MOBS);
   }

   protected void sense(ServerWorld world, LivingEntity entity) {
      entity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.VISIBLE_MOBS).ifPresent((targetCache) -> {
         this.find(entity, targetCache);
      });
   }

   protected void find(LivingEntity entity, LivingTargetCache targetCache) {
      Optional var10000 = targetCache.findFirst((target) -> {
         return target.getType() == entity.getType() && !target.isBaby();
      });
      Objects.requireNonNull(LivingEntity.class);
      Optional optional = var10000.map(LivingEntity.class::cast);
      entity.getBrain().remember(MemoryModuleType.NEAREST_VISIBLE_ADULT, optional);
   }
}
