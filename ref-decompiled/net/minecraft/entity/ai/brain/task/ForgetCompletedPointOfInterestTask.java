package net.minecraft.entity.ai.brain.task;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.GlobalPos;

public class ForgetCompletedPointOfInterestTask {
   private static final int MAX_RANGE = 16;

   public static Task create(Predicate poiTypePredicate, MemoryModuleType poiPosModule) {
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryValue(poiPosModule)).apply(context, (poiPos) -> {
            return (world, entity, time) -> {
               GlobalPos globalPos = (GlobalPos)context.getValue(poiPos);
               BlockPos blockPos = globalPos.pos();
               if (world.getRegistryKey() == globalPos.dimension() && blockPos.isWithinDistance(entity.getPos(), 16.0)) {
                  ServerWorld serverWorld = world.getServer().getWorld(globalPos.dimension());
                  if (serverWorld != null && serverWorld.getPointOfInterestStorage().test(blockPos, poiTypePredicate)) {
                     if (isBedOccupiedByOthers(serverWorld, blockPos, entity)) {
                        poiPos.forget();
                        if (!isSleepingVillagerAt(serverWorld, blockPos)) {
                           world.getPointOfInterestStorage().releaseTicket(blockPos);
                           DebugInfoSender.sendPointOfInterest(world, blockPos);
                        }
                     }
                  } else {
                     poiPos.forget();
                  }

                  return true;
               } else {
                  return false;
               }
            };
         });
      });
   }

   private static boolean isBedOccupiedByOthers(ServerWorld world, BlockPos pos, LivingEntity entity) {
      BlockState blockState = world.getBlockState(pos);
      return blockState.isIn(BlockTags.BEDS) && (Boolean)blockState.get(BedBlock.OCCUPIED) && !entity.isSleeping();
   }

   private static boolean isSleepingVillagerAt(ServerWorld world, BlockPos pos) {
      List list = world.getEntitiesByClass(VillagerEntity.class, new Box(pos), LivingEntity::isSleeping);
      return !list.isEmpty();
   }
}
