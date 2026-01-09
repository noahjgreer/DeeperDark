package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.MemoryQueryResult;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

public class OpenDoorsTask {
   private static final int RUN_TIME = 20;
   private static final double PATHING_DISTANCE = 3.0;
   private static final double REACH_DISTANCE = 2.0;

   public static Task create() {
      MutableObject mutableObject = new MutableObject((Object)null);
      MutableInt mutableInt = new MutableInt(0);
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryValue(MemoryModuleType.PATH), context.queryMemoryOptional(MemoryModuleType.DOORS_TO_CLOSE), context.queryMemoryOptional(MemoryModuleType.MOBS)).apply(context, (path, doorsToClose, mobs) -> {
            return (world, entity, time) -> {
               Path pathx = (Path)context.getValue(path);
               Optional optional = context.getOptionalValue(doorsToClose);
               if (!pathx.isStart() && !pathx.isFinished()) {
                  if (Objects.equals(mutableObject.getValue(), pathx.getCurrentNode())) {
                     mutableInt.setValue(20);
                  } else if (mutableInt.decrementAndGet() > 0) {
                     return false;
                  }

                  mutableObject.setValue(pathx.getCurrentNode());
                  PathNode pathNode = pathx.getLastNode();
                  PathNode pathNode2 = pathx.getCurrentNode();
                  BlockPos blockPos = pathNode.getBlockPos();
                  BlockState blockState = world.getBlockState(blockPos);
                  if (blockState.isIn(BlockTags.MOB_INTERACTABLE_DOORS, (state) -> {
                     return state.getBlock() instanceof DoorBlock;
                  })) {
                     DoorBlock doorBlock = (DoorBlock)blockState.getBlock();
                     if (!doorBlock.isOpen(blockState)) {
                        doorBlock.setOpen(entity, world, blockState, blockPos, true);
                     }

                     optional = storePos(doorsToClose, optional, world, blockPos);
                  }

                  BlockPos blockPos2 = pathNode2.getBlockPos();
                  BlockState blockState2 = world.getBlockState(blockPos2);
                  if (blockState2.isIn(BlockTags.MOB_INTERACTABLE_DOORS, (state) -> {
                     return state.getBlock() instanceof DoorBlock;
                  })) {
                     DoorBlock doorBlock2 = (DoorBlock)blockState2.getBlock();
                     if (!doorBlock2.isOpen(blockState2)) {
                        doorBlock2.setOpen(entity, world, blockState2, blockPos2, true);
                        optional = storePos(doorsToClose, optional, world, blockPos2);
                     }
                  }

                  optional.ifPresent((doors) -> {
                     pathToDoor(world, entity, pathNode, pathNode2, doors, context.getOptionalValue(mobs));
                  });
                  return true;
               } else {
                  return false;
               }
            };
         });
      });
   }

   public static void pathToDoor(ServerWorld world, LivingEntity entity, @Nullable PathNode lastNode, @Nullable PathNode currentNode, Set doors, Optional otherMobs) {
      Iterator iterator = doors.iterator();

      while(true) {
         GlobalPos globalPos;
         BlockPos blockPos;
         do {
            do {
               if (!iterator.hasNext()) {
                  return;
               }

               globalPos = (GlobalPos)iterator.next();
               blockPos = globalPos.pos();
            } while(lastNode != null && lastNode.getBlockPos().equals(blockPos));
         } while(currentNode != null && currentNode.getBlockPos().equals(blockPos));

         if (cannotReachDoor(world, entity, globalPos)) {
            iterator.remove();
         } else {
            BlockState blockState = world.getBlockState(blockPos);
            if (!blockState.isIn(BlockTags.MOB_INTERACTABLE_DOORS, (state) -> {
               return state.getBlock() instanceof DoorBlock;
            })) {
               iterator.remove();
            } else {
               DoorBlock doorBlock = (DoorBlock)blockState.getBlock();
               if (!doorBlock.isOpen(blockState)) {
                  iterator.remove();
               } else if (hasOtherMobReachedDoor(entity, blockPos, otherMobs)) {
                  iterator.remove();
               } else {
                  doorBlock.setOpen(entity, world, blockState, blockPos, false);
                  iterator.remove();
               }
            }
         }
      }
   }

   private static boolean hasOtherMobReachedDoor(LivingEntity entity, BlockPos pos, Optional otherMobs) {
      return otherMobs.isEmpty() ? false : ((List)otherMobs.get()).stream().filter((mob) -> {
         return mob.getType() == entity.getType();
      }).filter((mob) -> {
         return pos.isWithinDistance(mob.getPos(), 2.0);
      }).anyMatch((mob) -> {
         return hasReached(mob.getBrain(), pos);
      });
   }

   private static boolean hasReached(Brain brain, BlockPos pos) {
      if (!brain.hasMemoryModule(MemoryModuleType.PATH)) {
         return false;
      } else {
         Path path = (Path)brain.getOptionalRegisteredMemory(MemoryModuleType.PATH).get();
         if (path.isFinished()) {
            return false;
         } else {
            PathNode pathNode = path.getLastNode();
            if (pathNode == null) {
               return false;
            } else {
               PathNode pathNode2 = path.getCurrentNode();
               return pos.equals(pathNode.getBlockPos()) || pos.equals(pathNode2.getBlockPos());
            }
         }
      }
   }

   private static boolean cannotReachDoor(ServerWorld world, LivingEntity entity, GlobalPos doorPos) {
      return doorPos.dimension() != world.getRegistryKey() || !doorPos.pos().isWithinDistance(entity.getPos(), 3.0);
   }

   private static Optional storePos(MemoryQueryResult queryResult, Optional doors, ServerWorld world, BlockPos pos) {
      GlobalPos globalPos = GlobalPos.create(world.getRegistryKey(), pos);
      return Optional.of((Set)doors.map((doorSet) -> {
         doorSet.add(globalPos);
         return doorSet;
      }).orElseGet(() -> {
         Set set = Sets.newHashSet(new GlobalPos[]{globalPos});
         queryResult.remember((Object)set);
         return set;
      }));
   }
}
