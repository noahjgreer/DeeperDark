package net.minecraft.world.tick;

import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongMaps;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.LongPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;

public class WorldTickScheduler implements QueryableTickScheduler {
   private static final Comparator COMPARATOR = (a, b) -> {
      return OrderedTick.BASIC_COMPARATOR.compare(a.peekNextTick(), b.peekNextTick());
   };
   private final LongPredicate tickingFutureReadyPredicate;
   private final Long2ObjectMap chunkTickSchedulers = new Long2ObjectOpenHashMap();
   private final Long2LongMap nextTriggerTickByChunkPos = (Long2LongMap)Util.make(new Long2LongOpenHashMap(), (map) -> {
      map.defaultReturnValue(Long.MAX_VALUE);
   });
   private final Queue tickableChunkTickSchedulers;
   private final Queue tickableTicks;
   private final List tickedTicks;
   private final Set copiedTickableTicksList;
   private final BiConsumer queuedTickConsumer;

   public WorldTickScheduler(LongPredicate tickingFutureReadyPredicate) {
      this.tickableChunkTickSchedulers = new PriorityQueue(COMPARATOR);
      this.tickableTicks = new ArrayDeque();
      this.tickedTicks = new ArrayList();
      this.copiedTickableTicksList = new ObjectOpenCustomHashSet(OrderedTick.HASH_STRATEGY);
      this.queuedTickConsumer = (chunkTickScheduler, tick) -> {
         if (tick.equals(chunkTickScheduler.peekNextTick())) {
            this.schedule(tick);
         }

      };
      this.tickingFutureReadyPredicate = tickingFutureReadyPredicate;
   }

   public void addChunkTickScheduler(ChunkPos pos, ChunkTickScheduler scheduler) {
      long l = pos.toLong();
      this.chunkTickSchedulers.put(l, scheduler);
      OrderedTick orderedTick = scheduler.peekNextTick();
      if (orderedTick != null) {
         this.nextTriggerTickByChunkPos.put(l, orderedTick.triggerTick());
      }

      scheduler.setTickConsumer(this.queuedTickConsumer);
   }

   public void removeChunkTickScheduler(ChunkPos pos) {
      long l = pos.toLong();
      ChunkTickScheduler chunkTickScheduler = (ChunkTickScheduler)this.chunkTickSchedulers.remove(l);
      this.nextTriggerTickByChunkPos.remove(l);
      if (chunkTickScheduler != null) {
         chunkTickScheduler.setTickConsumer((BiConsumer)null);
      }

   }

   public void scheduleTick(OrderedTick orderedTick) {
      long l = ChunkPos.toLong(orderedTick.pos());
      ChunkTickScheduler chunkTickScheduler = (ChunkTickScheduler)this.chunkTickSchedulers.get(l);
      if (chunkTickScheduler == null) {
         Util.logErrorOrPause("Trying to schedule tick in not loaded position " + String.valueOf(orderedTick.pos()));
      } else {
         chunkTickScheduler.scheduleTick(orderedTick);
      }
   }

   public void tick(long time, int maxTicks, BiConsumer ticker) {
      Profiler profiler = Profilers.get();
      profiler.push("collect");
      this.collectTickableTicks(time, maxTicks, profiler);
      profiler.swap("run");
      profiler.visit("ticksToRun", this.tickableTicks.size());
      this.tick(ticker);
      profiler.swap("cleanup");
      this.clear();
      profiler.pop();
   }

   private void collectTickableTicks(long time, int maxTicks, Profiler profiler) {
      this.collectTickableChunkTickSchedulers(time);
      profiler.visit("containersToTick", this.tickableChunkTickSchedulers.size());
      this.addTickableTicks(time, maxTicks);
      this.delayAllTicks();
   }

   private void collectTickableChunkTickSchedulers(long time) {
      ObjectIterator objectIterator = Long2LongMaps.fastIterator(this.nextTriggerTickByChunkPos);

      while(objectIterator.hasNext()) {
         Long2LongMap.Entry entry = (Long2LongMap.Entry)objectIterator.next();
         long l = entry.getLongKey();
         long m = entry.getLongValue();
         if (m <= time) {
            ChunkTickScheduler chunkTickScheduler = (ChunkTickScheduler)this.chunkTickSchedulers.get(l);
            if (chunkTickScheduler == null) {
               objectIterator.remove();
            } else {
               OrderedTick orderedTick = chunkTickScheduler.peekNextTick();
               if (orderedTick == null) {
                  objectIterator.remove();
               } else if (orderedTick.triggerTick() > time) {
                  entry.setValue(orderedTick.triggerTick());
               } else if (this.tickingFutureReadyPredicate.test(l)) {
                  objectIterator.remove();
                  this.tickableChunkTickSchedulers.add(chunkTickScheduler);
               }
            }
         }
      }

   }

   private void addTickableTicks(long time, int maxTicks) {
      ChunkTickScheduler chunkTickScheduler;
      while(this.isTickableTicksCountUnder(maxTicks) && (chunkTickScheduler = (ChunkTickScheduler)this.tickableChunkTickSchedulers.poll()) != null) {
         OrderedTick orderedTick = chunkTickScheduler.pollNextTick();
         this.addTickableTick(orderedTick);
         this.addTickableTicks(this.tickableChunkTickSchedulers, chunkTickScheduler, time, maxTicks);
         OrderedTick orderedTick2 = chunkTickScheduler.peekNextTick();
         if (orderedTick2 != null) {
            if (orderedTick2.triggerTick() <= time && this.isTickableTicksCountUnder(maxTicks)) {
               this.tickableChunkTickSchedulers.add(chunkTickScheduler);
            } else {
               this.schedule(orderedTick2);
            }
         }
      }

   }

   private void delayAllTicks() {
      Iterator var1 = this.tickableChunkTickSchedulers.iterator();

      while(var1.hasNext()) {
         ChunkTickScheduler chunkTickScheduler = (ChunkTickScheduler)var1.next();
         this.schedule(chunkTickScheduler.peekNextTick());
      }

   }

   private void schedule(OrderedTick tick) {
      this.nextTriggerTickByChunkPos.put(ChunkPos.toLong(tick.pos()), tick.triggerTick());
   }

   private void addTickableTicks(Queue tickableChunkTickSchedulers, ChunkTickScheduler chunkTickScheduler, long tick, int maxTicks) {
      if (this.isTickableTicksCountUnder(maxTicks)) {
         ChunkTickScheduler chunkTickScheduler2 = (ChunkTickScheduler)tickableChunkTickSchedulers.peek();
         OrderedTick orderedTick = chunkTickScheduler2 != null ? chunkTickScheduler2.peekNextTick() : null;

         while(this.isTickableTicksCountUnder(maxTicks)) {
            OrderedTick orderedTick2 = chunkTickScheduler.peekNextTick();
            if (orderedTick2 == null || orderedTick2.triggerTick() > tick || orderedTick != null && OrderedTick.BASIC_COMPARATOR.compare(orderedTick2, orderedTick) > 0) {
               break;
            }

            chunkTickScheduler.pollNextTick();
            this.addTickableTick(orderedTick2);
         }

      }
   }

   private void addTickableTick(OrderedTick tick) {
      this.tickableTicks.add(tick);
   }

   private boolean isTickableTicksCountUnder(int maxTicks) {
      return this.tickableTicks.size() < maxTicks;
   }

   private void tick(BiConsumer ticker) {
      while(!this.tickableTicks.isEmpty()) {
         OrderedTick orderedTick = (OrderedTick)this.tickableTicks.poll();
         if (!this.copiedTickableTicksList.isEmpty()) {
            this.copiedTickableTicksList.remove(orderedTick);
         }

         this.tickedTicks.add(orderedTick);
         ticker.accept(orderedTick.pos(), orderedTick.type());
      }

   }

   private void clear() {
      this.tickableTicks.clear();
      this.tickableChunkTickSchedulers.clear();
      this.tickedTicks.clear();
      this.copiedTickableTicksList.clear();
   }

   public boolean isQueued(BlockPos pos, Object type) {
      ChunkTickScheduler chunkTickScheduler = (ChunkTickScheduler)this.chunkTickSchedulers.get(ChunkPos.toLong(pos));
      return chunkTickScheduler != null && chunkTickScheduler.isQueued(pos, type);
   }

   public boolean isTicking(BlockPos pos, Object type) {
      this.copyTickableTicksList();
      return this.copiedTickableTicksList.contains(OrderedTick.create(type, pos));
   }

   private void copyTickableTicksList() {
      if (this.copiedTickableTicksList.isEmpty() && !this.tickableTicks.isEmpty()) {
         this.copiedTickableTicksList.addAll(this.tickableTicks);
      }

   }

   private void visitChunks(BlockBox box, ChunkVisitor visitor) {
      int i = ChunkSectionPos.getSectionCoord((double)box.getMinX());
      int j = ChunkSectionPos.getSectionCoord((double)box.getMinZ());
      int k = ChunkSectionPos.getSectionCoord((double)box.getMaxX());
      int l = ChunkSectionPos.getSectionCoord((double)box.getMaxZ());

      for(int m = i; m <= k; ++m) {
         for(int n = j; n <= l; ++n) {
            long o = ChunkPos.toLong(m, n);
            ChunkTickScheduler chunkTickScheduler = (ChunkTickScheduler)this.chunkTickSchedulers.get(o);
            if (chunkTickScheduler != null) {
               visitor.accept(o, chunkTickScheduler);
            }
         }
      }

   }

   public void clearNextTicks(BlockBox box) {
      Predicate predicate = (tick) -> {
         return box.contains(tick.pos());
      };
      this.visitChunks(box, (chunkPos, chunkTickScheduler) -> {
         OrderedTick orderedTick = chunkTickScheduler.peekNextTick();
         chunkTickScheduler.removeTicksIf(predicate);
         OrderedTick orderedTick2 = chunkTickScheduler.peekNextTick();
         if (orderedTick2 != orderedTick) {
            if (orderedTick2 != null) {
               this.schedule(orderedTick2);
            } else {
               this.nextTriggerTickByChunkPos.remove(chunkPos);
            }
         }

      });
      this.tickedTicks.removeIf(predicate);
      this.tickableTicks.removeIf(predicate);
   }

   public void scheduleTicks(BlockBox box, Vec3i offset) {
      this.scheduleTicks(this, box, offset);
   }

   public void scheduleTicks(WorldTickScheduler scheduler, BlockBox box, Vec3i offset) {
      List list = new ArrayList();
      Predicate predicate = (tick) -> {
         return box.contains(tick.pos());
      };
      Stream var10000 = scheduler.tickedTicks.stream().filter(predicate);
      Objects.requireNonNull(list);
      var10000.forEach(list::add);
      var10000 = scheduler.tickableTicks.stream().filter(predicate);
      Objects.requireNonNull(list);
      var10000.forEach(list::add);
      scheduler.visitChunks(box, (chunkPos, chunkTickScheduler) -> {
         Stream var10000 = chunkTickScheduler.getQueueAsStream().filter(predicate);
         Objects.requireNonNull(list);
         var10000.forEach(list::add);
      });
      LongSummaryStatistics longSummaryStatistics = list.stream().mapToLong(OrderedTick::subTickOrder).summaryStatistics();
      long l = longSummaryStatistics.getMin();
      long m = longSummaryStatistics.getMax();
      list.forEach((tick) -> {
         this.scheduleTick(new OrderedTick(tick.type(), tick.pos().add(offset), tick.triggerTick(), tick.priority(), tick.subTickOrder() - l + m + 1L));
      });
   }

   public int getTickCount() {
      return this.chunkTickSchedulers.values().stream().mapToInt(TickScheduler::getTickCount).sum();
   }

   @FunctionalInterface
   interface ChunkVisitor {
      void accept(long chunkPos, ChunkTickScheduler chunkTickScheduler);
   }
}
