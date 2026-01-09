package net.minecraft.world.tick;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.minecraft.util.math.BlockPos;

public class SimpleTickScheduler implements SerializableTickScheduler, BasicTickScheduler {
   private final List scheduledTicks = Lists.newArrayList();
   private final Set scheduledTicksSet;

   public SimpleTickScheduler() {
      this.scheduledTicksSet = new ObjectOpenCustomHashSet(Tick.HASH_STRATEGY);
   }

   public void scheduleTick(OrderedTick orderedTick) {
      Tick tick = new Tick(orderedTick.type(), orderedTick.pos(), 0, orderedTick.priority());
      this.scheduleTick(tick);
   }

   private void scheduleTick(Tick tick) {
      if (this.scheduledTicksSet.add(tick)) {
         this.scheduledTicks.add(tick);
      }

   }

   public boolean isQueued(BlockPos pos, Object type) {
      return this.scheduledTicksSet.contains(Tick.create(type, pos));
   }

   public int getTickCount() {
      return this.scheduledTicks.size();
   }

   public List collectTicks(long time) {
      return this.scheduledTicks;
   }

   public List getTicks() {
      return List.copyOf(this.scheduledTicks);
   }

   public static SimpleTickScheduler tick(List ticks) {
      SimpleTickScheduler simpleTickScheduler = new SimpleTickScheduler();
      Objects.requireNonNull(simpleTickScheduler);
      ticks.forEach(simpleTickScheduler::scheduleTick);
      return simpleTickScheduler;
   }
}
