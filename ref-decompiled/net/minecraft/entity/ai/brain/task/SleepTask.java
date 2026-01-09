package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.Set;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;

public class SleepTask extends MultiTickTask {
   public static final int RUN_TIME = 100;
   private long startTime;

   public SleepTask() {
      super(ImmutableMap.of(MemoryModuleType.HOME, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.LAST_WOKEN, MemoryModuleState.REGISTERED));
   }

   protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
      if (entity.hasVehicle()) {
         return false;
      } else {
         Brain brain = entity.getBrain();
         GlobalPos globalPos = (GlobalPos)brain.getOptionalRegisteredMemory(MemoryModuleType.HOME).get();
         if (world.getRegistryKey() != globalPos.dimension()) {
            return false;
         } else {
            Optional optional = brain.getOptionalRegisteredMemory(MemoryModuleType.LAST_WOKEN);
            if (optional.isPresent()) {
               long l = world.getTime() - (Long)optional.get();
               if (l > 0L && l < 100L) {
                  return false;
               }
            }

            BlockState blockState = world.getBlockState(globalPos.pos());
            return globalPos.pos().isWithinDistance(entity.getPos(), 2.0) && blockState.isIn(BlockTags.BEDS) && !(Boolean)blockState.get(BedBlock.OCCUPIED);
         }
      }
   }

   protected boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
      Optional optional = entity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.HOME);
      if (optional.isEmpty()) {
         return false;
      } else {
         BlockPos blockPos = ((GlobalPos)optional.get()).pos();
         return entity.getBrain().hasActivity(Activity.REST) && entity.getY() > (double)blockPos.getY() + 0.4 && blockPos.isWithinDistance(entity.getPos(), 1.14);
      }
   }

   protected void run(ServerWorld world, LivingEntity entity, long time) {
      if (time > this.startTime) {
         Brain brain = entity.getBrain();
         if (brain.hasMemoryModule(MemoryModuleType.DOORS_TO_CLOSE)) {
            Set set = (Set)brain.getOptionalRegisteredMemory(MemoryModuleType.DOORS_TO_CLOSE).get();
            Optional optional;
            if (brain.hasMemoryModule(MemoryModuleType.MOBS)) {
               optional = brain.getOptionalRegisteredMemory(MemoryModuleType.MOBS);
            } else {
               optional = Optional.empty();
            }

            OpenDoorsTask.pathToDoor(world, entity, (PathNode)null, (PathNode)null, set, optional);
         }

         entity.sleep(((GlobalPos)entity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.HOME).get()).pos());
      }

   }

   protected boolean isTimeLimitExceeded(long time) {
      return false;
   }

   protected void finishRunning(ServerWorld world, LivingEntity entity, long time) {
      if (entity.isSleeping()) {
         entity.wakeUp();
         this.startTime = time + 40L;
      }

   }
}
