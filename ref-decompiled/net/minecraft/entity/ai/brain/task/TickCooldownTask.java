package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public class TickCooldownTask extends MultiTickTask {
   private final MemoryModuleType cooldownModule;

   public TickCooldownTask(MemoryModuleType cooldownModule) {
      super(ImmutableMap.of(cooldownModule, MemoryModuleState.VALUE_PRESENT));
      this.cooldownModule = cooldownModule;
   }

   private Optional getRemainingCooldownTicks(LivingEntity entity) {
      return entity.getBrain().getOptionalRegisteredMemory(this.cooldownModule);
   }

   protected boolean isTimeLimitExceeded(long time) {
      return false;
   }

   protected boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
      Optional optional = this.getRemainingCooldownTicks(entity);
      return optional.isPresent() && (Integer)optional.get() > 0;
   }

   protected void keepRunning(ServerWorld world, LivingEntity entity, long time) {
      Optional optional = this.getRemainingCooldownTicks(entity);
      entity.getBrain().remember(this.cooldownModule, (Object)((Integer)optional.get() - 1));
   }

   protected void finishRunning(ServerWorld world, LivingEntity entity, long time) {
      entity.getBrain().forget(this.cooldownModule);
   }
}
