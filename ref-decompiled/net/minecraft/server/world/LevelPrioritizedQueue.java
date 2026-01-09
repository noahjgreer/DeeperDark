package net.minecraft.server.world;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.util.math.ChunkPos;
import org.jetbrains.annotations.Nullable;

public class LevelPrioritizedQueue {
   public static final int LEVEL_COUNT;
   private final List values;
   private volatile int topPriority;
   private final String name;

   public LevelPrioritizedQueue(String name) {
      this.values = IntStream.range(0, LEVEL_COUNT).mapToObj((level) -> {
         return new Long2ObjectLinkedOpenHashMap();
      }).toList();
      this.topPriority = LEVEL_COUNT;
      this.name = name;
   }

   protected void updateLevel(int fromLevel, ChunkPos pos, int toLevel) {
      if (fromLevel < LEVEL_COUNT) {
         Long2ObjectLinkedOpenHashMap long2ObjectLinkedOpenHashMap = (Long2ObjectLinkedOpenHashMap)this.values.get(fromLevel);
         List list = (List)long2ObjectLinkedOpenHashMap.remove(pos.toLong());
         if (fromLevel == this.topPriority) {
            while(this.hasQueuedElement() && ((Long2ObjectLinkedOpenHashMap)this.values.get(this.topPriority)).isEmpty()) {
               ++this.topPriority;
            }
         }

         if (list != null && !list.isEmpty()) {
            ((List)((Long2ObjectLinkedOpenHashMap)this.values.get(toLevel)).computeIfAbsent(pos.toLong(), (chunkPos) -> {
               return Lists.newArrayList();
            })).addAll(list);
            this.topPriority = Math.min(this.topPriority, toLevel);
         }

      }
   }

   protected void add(Runnable task, long pos, int level) {
      ((List)((Long2ObjectLinkedOpenHashMap)this.values.get(level)).computeIfAbsent(pos, (chunkPos) -> {
         return Lists.newArrayList();
      })).add(task);
      this.topPriority = Math.min(this.topPriority, level);
   }

   protected void remove(long pos, boolean removeElement) {
      Iterator var4 = this.values.iterator();

      while(var4.hasNext()) {
         Long2ObjectLinkedOpenHashMap long2ObjectLinkedOpenHashMap = (Long2ObjectLinkedOpenHashMap)var4.next();
         List list = (List)long2ObjectLinkedOpenHashMap.get(pos);
         if (list != null) {
            if (removeElement) {
               list.clear();
            }

            if (list.isEmpty()) {
               long2ObjectLinkedOpenHashMap.remove(pos);
            }
         }
      }

      while(this.hasQueuedElement() && ((Long2ObjectLinkedOpenHashMap)this.values.get(this.topPriority)).isEmpty()) {
         ++this.topPriority;
      }

   }

   @Nullable
   public Entry poll() {
      if (!this.hasQueuedElement()) {
         return null;
      } else {
         int i = this.topPriority;
         Long2ObjectLinkedOpenHashMap long2ObjectLinkedOpenHashMap = (Long2ObjectLinkedOpenHashMap)this.values.get(i);
         long l = long2ObjectLinkedOpenHashMap.firstLongKey();

         List list;
         for(list = (List)long2ObjectLinkedOpenHashMap.removeFirst(); this.hasQueuedElement() && ((Long2ObjectLinkedOpenHashMap)this.values.get(this.topPriority)).isEmpty(); ++this.topPriority) {
         }

         return new Entry(l, list);
      }
   }

   public boolean hasQueuedElement() {
      return this.topPriority < LEVEL_COUNT;
   }

   public String toString() {
      return this.name + " " + this.topPriority + "...";
   }

   static {
      LEVEL_COUNT = ChunkLevels.INACCESSIBLE + 2;
   }

   public static record Entry(long chunkPos, List tasks) {
      public Entry(long l, List list) {
         this.chunkPos = l;
         this.tasks = list;
      }

      public long chunkPos() {
         return this.chunkPos;
      }

      public List tasks() {
         return this.tasks;
      }
   }
}
