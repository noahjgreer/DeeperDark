package net.minecraft.entity.ai.brain.task;

import java.util.Iterator;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.mutable.MutableLong;

public class WalkTowardsWaterTask {
   public static Task create(int range, float speed) {
      MutableLong mutableLong = new MutableLong(0L);
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryAbsent(MemoryModuleType.ATTACK_TARGET), context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET), context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET)).apply(context, (attackTarget, walkTarget, lookTarget) -> {
            return (world, entity, time) -> {
               if (world.getFluidState(entity.getBlockPos()).isIn(FluidTags.WATER)) {
                  return false;
               } else if (time < mutableLong.getValue()) {
                  mutableLong.setValue(time + 40L);
                  return true;
               } else {
                  ShapeContext shapeContext = ShapeContext.of(entity);
                  BlockPos blockPos = entity.getBlockPos();
                  BlockPos.Mutable mutable = new BlockPos.Mutable();
                  Iterator var12 = BlockPos.iterateOutwards(blockPos, range, range, range).iterator();

                  label45:
                  while(var12.hasNext()) {
                     BlockPos blockPos2 = (BlockPos)var12.next();
                     if ((blockPos2.getX() != blockPos.getX() || blockPos2.getZ() != blockPos.getZ()) && world.getBlockState(blockPos2).getCollisionShape(world, blockPos2, shapeContext).isEmpty() && !world.getBlockState(mutable.set(blockPos2, (Direction)Direction.DOWN)).getCollisionShape(world, blockPos2, shapeContext).isEmpty()) {
                        Iterator var14 = Direction.Type.HORIZONTAL.iterator();

                        while(var14.hasNext()) {
                           Direction direction = (Direction)var14.next();
                           mutable.set(blockPos2, (Direction)direction);
                           if (world.getBlockState(mutable).isAir() && world.getBlockState(mutable.move(Direction.DOWN)).isOf(Blocks.WATER)) {
                              lookTarget.remember((Object)(new BlockPosLookTarget(blockPos2)));
                              walkTarget.remember((Object)(new WalkTarget(new BlockPosLookTarget(blockPos2), speed, 0)));
                              break label45;
                           }
                        }
                     }
                  }

                  mutableLong.setValue(time + 40L);
                  return true;
               }
            };
         });
      });
   }
}
