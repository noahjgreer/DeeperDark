package net.minecraft.world.tick;

import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ChunkTickScheduler implements SerializableTickScheduler, BasicTickScheduler {
   private final Queue tickQueue;
   @Nullable
   private List ticks;
   private final Set queuedTicks;
   @Nullable
   private BiConsumer tickConsumer;

   public ChunkTickScheduler() {
      this.tickQueue = new PriorityQueue(OrderedTick.TRIGGER_TICK_COMPARATOR);
      this.queuedTicks = new ObjectOpenCustomHashSet(OrderedTick.HASH_STRATEGY);
   }

   public ChunkTickScheduler(List ticks) {
      this.tickQueue = new PriorityQueue(OrderedTick.TRIGGER_TICK_COMPARATOR);
      this.queuedTicks = new ObjectOpenCustomHashSet(OrderedTick.HASH_STRATEGY);
      this.ticks = ticks;
      Iterator var2 = ticks.iterator();

      while(var2.hasNext()) {
         Tick tick = (Tick)var2.next();
         this.queuedTicks.add(OrderedTick.create(tick.type(), tick.pos()));
      }

   }

   public void setTickConsumer(@Nullable BiConsumer tickConsumer) {
      this.tickConsumer = tickConsumer;
   }

   @Nullable
   public OrderedTick peekNextTick() {
      return (OrderedTick)this.tickQueue.peek();
   }

   @Nullable
   public OrderedTick pollNextTick() {
      OrderedTick orderedTick = (OrderedTick)this.tickQueue.poll();
      if (orderedTick != null) {
         this.queuedTicks.remove(orderedTick);
      }

      return orderedTick;
   }

   public void scheduleTick(OrderedTick orderedTick) {
      if (this.queuedTicks.add(orderedTick)) {
         this.queueTick(orderedTick);
      }

   }

   private void queueTick(OrderedTick orderedTick) {
      this.tickQueue.add(orderedTick);
      if (this.tickConsumer != null) {
         this.tickConsumer.accept(this, orderedTick);
      }

   }

   public boolean isQueued(BlockPos pos, Object type) {
      return this.queuedTicks.contains(OrderedTick.create(type, pos));
   }

   public void removeTicksIf(Predicate predicate) {
      Iterator iterator = this.tickQueue.iterator();

      while(iterator.hasNext()) {
         OrderedTick orderedTick = (OrderedTick)iterator.next();
         if (predicate.test(orderedTick)) {
            iterator.remove();
            this.queuedTicks.remove(orderedTick);
         }
      }

   }

   public Stream getQueueAsStream() {
      return this.tickQueue.stream();
   }

   public int getTickCount() {
      return this.tickQueue.size() + (this.ticks != null ? this.ticks.size() : 0);
   }

   public List collectTicks(long time) {
      List list = new ArrayList(this.tickQueue.size());
      if (this.ticks != null) {
         list.addAll(this.ticks);
      }

      Iterator var4 = this.tickQueue.iterator();

      while(var4.hasNext()) {
         OrderedTick orderedTick = (OrderedTick)var4.next();
         list.add(orderedTick.toTick(time));
      }

      return list;
   }

   public void disable(long time) {
      if (this.ticks != null) {
         int i = -this.ticks.size();
         Iterator var4 = this.ticks.iterator();

         while(var4.hasNext()) {
            Tick tick = (Tick)var4.next();
            this.queueTick(tick.createOrderedTick(time, (long)(i++)));
         }
      }

      this.ticks = null;
   }
}
