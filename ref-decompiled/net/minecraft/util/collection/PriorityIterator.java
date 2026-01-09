package net.minecraft.util.collection;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Queues;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Deque;
import org.jetbrains.annotations.Nullable;

public final class PriorityIterator extends AbstractIterator {
   private static final int LOWEST_PRIORITY = Integer.MIN_VALUE;
   @Nullable
   private Deque maxPriorityQueue = null;
   private int maxPriority = Integer.MIN_VALUE;
   private final Int2ObjectMap queuesByPriority = new Int2ObjectOpenHashMap();

   public void enqueue(Object value, int priority) {
      if (priority == this.maxPriority && this.maxPriorityQueue != null) {
         this.maxPriorityQueue.addLast(value);
      } else {
         Deque deque = (Deque)this.queuesByPriority.computeIfAbsent(priority, (p) -> {
            return Queues.newArrayDeque();
         });
         deque.addLast(value);
         if (priority >= this.maxPriority) {
            this.maxPriorityQueue = deque;
            this.maxPriority = priority;
         }

      }
   }

   @Nullable
   protected Object computeNext() {
      if (this.maxPriorityQueue == null) {
         return this.endOfData();
      } else {
         Object object = this.maxPriorityQueue.removeFirst();
         if (object == null) {
            return this.endOfData();
         } else {
            if (this.maxPriorityQueue.isEmpty()) {
               this.refreshMaxPriority();
            }

            return object;
         }
      }
   }

   private void refreshMaxPriority() {
      int i = Integer.MIN_VALUE;
      Deque deque = null;
      ObjectIterator var3 = Int2ObjectMaps.fastIterable(this.queuesByPriority).iterator();

      while(var3.hasNext()) {
         Int2ObjectMap.Entry entry = (Int2ObjectMap.Entry)var3.next();
         Deque deque2 = (Deque)entry.getValue();
         int j = entry.getIntKey();
         if (j > i && !deque2.isEmpty()) {
            i = j;
            deque = deque2;
            if (j == this.maxPriority - 1) {
               break;
            }
         }
      }

      this.maxPriority = i;
      this.maxPriorityQueue = deque;
   }
}
