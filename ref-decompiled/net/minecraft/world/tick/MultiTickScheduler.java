package net.minecraft.world.tick;

import java.util.function.Function;
import net.minecraft.util.math.BlockPos;

public class MultiTickScheduler implements QueryableTickScheduler {
   private final Function mapper;

   public MultiTickScheduler(Function mapper) {
      this.mapper = mapper;
   }

   public boolean isQueued(BlockPos pos, Object type) {
      return ((BasicTickScheduler)this.mapper.apply(pos)).isQueued(pos, type);
   }

   public void scheduleTick(OrderedTick orderedTick) {
      ((BasicTickScheduler)this.mapper.apply(orderedTick.pos())).scheduleTick(orderedTick);
   }

   public boolean isTicking(BlockPos pos, Object type) {
      return false;
   }

   public int getTickCount() {
      return 0;
   }
}
