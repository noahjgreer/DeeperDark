package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.server.world.ServerWorld;

public class PlayDeadTask extends MultiTickTask {
   public PlayDeadTask() {
      super(ImmutableMap.of(MemoryModuleType.PLAY_DEAD_TICKS, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleState.VALUE_PRESENT), 200);
   }

   protected boolean shouldRun(ServerWorld serverWorld, AxolotlEntity axolotlEntity) {
      return axolotlEntity.isTouchingWater();
   }

   protected boolean shouldKeepRunning(ServerWorld serverWorld, AxolotlEntity axolotlEntity, long l) {
      return axolotlEntity.isTouchingWater() && axolotlEntity.getBrain().hasMemoryModule(MemoryModuleType.PLAY_DEAD_TICKS);
   }

   protected void run(ServerWorld serverWorld, AxolotlEntity axolotlEntity, long l) {
      Brain brain = axolotlEntity.getBrain();
      brain.forget(MemoryModuleType.WALK_TARGET);
      brain.forget(MemoryModuleType.LOOK_TARGET);
      axolotlEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 0));
   }

   // $FF: synthetic method
   protected void run(final ServerWorld world, final LivingEntity entity, final long time) {
      this.run(world, (AxolotlEntity)entity, time);
   }
}
