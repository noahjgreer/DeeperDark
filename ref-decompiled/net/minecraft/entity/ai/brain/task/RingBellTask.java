package net.minecraft.entity.ai.brain.task;

import net.minecraft.block.BellBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.GlobalPos;

public class RingBellTask {
   private static final float RUN_CHANCE = 0.95F;
   public static final int MAX_DISTANCE = 3;

   public static Task create() {
      return TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryValue(MemoryModuleType.MEETING_POINT)).apply(context, (meetingPoint) -> {
            return (world, entity, time) -> {
               if (world.random.nextFloat() <= 0.95F) {
                  return false;
               } else {
                  BlockPos blockPos = ((GlobalPos)context.getValue(meetingPoint)).pos();
                  if (blockPos.isWithinDistance(entity.getBlockPos(), 3.0)) {
                     BlockState blockState = world.getBlockState(blockPos);
                     if (blockState.isOf(Blocks.BELL)) {
                        BellBlock bellBlock = (BellBlock)blockState.getBlock();
                        bellBlock.ring(entity, world, blockPos, (Direction)null);
                     }
                  }

                  return true;
               }
            };
         });
      });
   }
}
