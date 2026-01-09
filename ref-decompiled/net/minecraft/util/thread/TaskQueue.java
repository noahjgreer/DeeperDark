package net.minecraft.util.thread;

import com.google.common.collect.Queues;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.Nullable;

public interface TaskQueue {
   @Nullable
   Runnable poll();

   boolean add(Runnable runnable);

   boolean isEmpty();

   int getSize();

   public static final class Prioritized implements TaskQueue {
      private final Queue[] queue;
      private final AtomicInteger queueSize = new AtomicInteger();

      public Prioritized(int priorityCount) {
         this.queue = new Queue[priorityCount];

         for(int i = 0; i < priorityCount; ++i) {
            this.queue[i] = Queues.newConcurrentLinkedQueue();
         }

      }

      @Nullable
      public Runnable poll() {
         Queue[] var1 = this.queue;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Queue queue = var1[var3];
            Runnable runnable = (Runnable)queue.poll();
            if (runnable != null) {
               this.queueSize.decrementAndGet();
               return runnable;
            }
         }

         return null;
      }

      public boolean add(PrioritizedTask prioritizedTask) {
         int i = prioritizedTask.priority;
         if (i < this.queue.length && i >= 0) {
            this.queue[i].add(prioritizedTask);
            this.queueSize.incrementAndGet();
            return true;
         } else {
            throw new IndexOutOfBoundsException(String.format(Locale.ROOT, "Priority %d not supported. Expected range [0-%d]", i, this.queue.length - 1));
         }
      }

      public boolean isEmpty() {
         return this.queueSize.get() == 0;
      }

      public int getSize() {
         return this.queueSize.get();
      }
   }

   public static record PrioritizedTask(int priority, Runnable runnable) implements Runnable {
      final int priority;

      public PrioritizedTask(int priority, Runnable runnable) {
         this.priority = priority;
         this.runnable = runnable;
      }

      public void run() {
         this.runnable.run();
      }

      public int priority() {
         return this.priority;
      }

      public Runnable runnable() {
         return this.runnable;
      }
   }

   public static final class Simple implements TaskQueue {
      private final Queue queue;

      public Simple(Queue queue) {
         this.queue = queue;
      }

      @Nullable
      public Runnable poll() {
         return (Runnable)this.queue.poll();
      }

      public boolean add(Runnable runnable) {
         return this.queue.add(runnable);
      }

      public boolean isEmpty() {
         return this.queue.isEmpty();
      }

      public int getSize() {
         return this.queue.size();
      }
   }
}
