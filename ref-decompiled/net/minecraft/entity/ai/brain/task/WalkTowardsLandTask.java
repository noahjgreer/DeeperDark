package net.minecraft.entity.ai.brain.task;

import java.util.Iterator;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.mutable.MutableLong;

public class WalkTowardsLandTask {
   private static final int TASK_COOLDOWN = 60;

   public static Task create(int range, float speed) {
      MutableLong mutableLong = new MutableLong(0L);
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryAbsent(MemoryModuleType.ATTACK_TARGET), context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET), context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET)).apply(context, (attackTarget, walkTarget, lookTarget) -> {
            return (world, entity, time) -> {
               if (!world.getFluidState(entity.getBlockPos()).isIn(FluidTags.WATER)) {
                  return false;
               } else if (time < mutableLong.getValue()) {
                  mutableLong.setValue(time + 60L);
                  return true;
               } else {
                  BlockPos blockPos = entity.getBlockPos();
                  BlockPos.Mutable mutable = new BlockPos.Mutable();
                  ShapeContext shapeContext = ShapeContext.of(entity);
                  Iterator var12 = BlockPos.iterateOutwards(blockPos, range, range, range).iterator();

                  while(var12.hasNext()) {
                     BlockPos blockPos2 = (BlockPos)var12.next();
                     if (blockPos2.getX() != blockPos.getX() || blockPos2.getZ() != blockPos.getZ()) {
                        BlockState blockState = world.getBlockState(blockPos2);
                        BlockState blockState2 = world.getBlockState(mutable.set(blockPos2, (Direction)Direction.DOWN));
                        if (!blockState.isOf(Blocks.WATER) && world.getFluidState(blockPos2).isEmpty() && blockState.getCollisionShape(world, blockPos2, shapeContext).isEmpty() && blockState2.isSideSolidFullSquare(world, mutable, Direction.UP)) {
                           BlockPos blockPos3 = blockPos2.toImmutable();
                           lookTarget.remember((Object)(new BlockPosLookTarget(blockPos3)));
                           walkTarget.remember((Object)(new WalkTarget(new BlockPosLookTarget(blockPos3), speed, 1)));
                           break;
                        }
                     }
                  }

                  mutableLong.setValue(time + 60L);
                  return true;
               }
            };
         });
      });
   }
}
