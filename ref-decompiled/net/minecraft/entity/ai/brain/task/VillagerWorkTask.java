package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.GlobalPos;

public class VillagerWorkTask extends MultiTickTask {
   private static final int RUN_TIME = 300;
   private static final double MAX_DISTANCE = 1.73;
   private long lastCheckedTime;

   public VillagerWorkTask() {
      super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED));
   }

   protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
      if (serverWorld.getTime() - this.lastCheckedTime < 300L) {
         return false;
      } else if (serverWorld.random.nextInt(2) != 0) {
         return false;
      } else {
         this.lastCheckedTime = serverWorld.getTime();
         GlobalPos globalPos = (GlobalPos)villagerEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.JOB_SITE).get();
         return globalPos.dimension() == serverWorld.getRegistryKey() && globalPos.pos().isWithinDistance(villagerEntity.getPos(), 1.73);
      }
   }

   protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
      Brain brain = villagerEntity.getBrain();
      brain.remember(MemoryModuleType.LAST_WORKED_AT_POI, (Object)l);
      brain.getOptionalRegisteredMemory(MemoryModuleType.JOB_SITE).ifPresent((pos) -> {
         brain.remember(MemoryModuleType.LOOK_TARGET, (Object)(new BlockPosLookTarget(pos.pos())));
      });
      villagerEntity.playWorkSound();
      this.performAdditionalWork(serverWorld, villagerEntity);
      if (villagerEntity.shouldRestock()) {
         villagerEntity.restock();
      }

   }

   protected void performAdditionalWork(ServerWorld world, VillagerEntity entity) {
   }

   protected boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
      Optional optional = villagerEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.JOB_SITE);
      if (optional.isEmpty()) {
         return false;
      } else {
         GlobalPos globalPos = (GlobalPos)optional.get();
         return globalPos.dimension() == serverWorld.getRegistryKey() && globalPos.pos().isWithinDistance(villagerEntity.getPos(), 1.73);
      }
   }

   // $FF: synthetic method
   protected boolean shouldRun(final ServerWorld world, final LivingEntity entity) {
      return this.shouldRun(world, (VillagerEntity)entity);
   }

   // $FF: synthetic method
   protected boolean shouldKeepRunning(final ServerWorld world, final LivingEntity entity, final long time) {
      return this.shouldKeepRunning(world, (VillagerEntity)entity, time);
   }

   // $FF: synthetic method
   protected void run(final ServerWorld world, final LivingEntity entity, final long time) {
      this.run(world, (VillagerEntity)entity, time);
   }
}
