package net.minecraft.world.tick;

import net.minecraft.util.math.BlockPos;

public class EmptyTickSchedulers {
   private static final BasicTickScheduler EMPTY_BASIC_TICK_SCHEDULER = new BasicTickScheduler() {
      public void scheduleTick(OrderedTick orderedTick) {
      }

      public boolean isQueued(BlockPos pos, Object type) {
         return false;
      }

      public int getTickCount() {
         return 0;
      }
   };
   private static final QueryableTickScheduler EMPTY_QUERYABLE_TICK_SCHEDULER = new QueryableTickScheduler() {
      public void scheduleTick(OrderedTick orderedTick) {
      }

      public boolean isQueued(BlockPos pos, Object type) {
         return false;
      }

      public boolean isTicking(BlockPos pos, Object type) {
         return false;
      }

      public int getTickCount() {
         return 0;
      }
   };

   public static BasicTickScheduler getReadOnlyTickScheduler() {
      return EMPTY_BASIC_TICK_SCHEDULER;
   }

   public static QueryableTickScheduler getClientTickScheduler() {
      return EMPTY_QUERYABLE_TICK_SCHEDULER;
   }
}
