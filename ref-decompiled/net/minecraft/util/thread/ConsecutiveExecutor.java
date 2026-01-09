package net.minecraft.util.thread;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.SampleType;
import net.minecraft.util.profiler.Sampler;
import org.slf4j.Logger;

public abstract class ConsecutiveExecutor implements SampleableExecutor, TaskExecutor, Runnable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final AtomicReference status;
   private final TaskQueue queue;
   private final Executor executor;
   private final String name;

   public ConsecutiveExecutor(TaskQueue queue, Executor executor, String name) {
      this.status = new AtomicReference(ConsecutiveExecutor.Status.SLEEPING);
      this.executor = executor;
      this.queue = queue;
      this.name = name;
      ExecutorSampling.INSTANCE.add(this);
   }

   private boolean canRun() {
      return !this.isClosed() && !this.queue.isEmpty();
   }

   public void close() {
      this.status.set(ConsecutiveExecutor.Status.CLOSED);
   }

   private boolean runOnce() {
      if (!this.isRunning()) {
         return false;
      } else {
         Runnable runnable = this.queue.poll();
         if (runnable == null) {
            return false;
         } else {
            Util.runInNamedZone(runnable, this.name);
            return true;
         }
      }
   }

   public void run() {
      try {
         this.runOnce();
      } finally {
         this.sleep();
         this.scheduleSelf();
      }

   }

   public void runAll() {
      while(true) {
         try {
            if (this.runOnce()) {
               continue;
            }
         } finally {
            this.sleep();
            this.scheduleSelf();
         }

         return;
      }
   }

   public void send(Runnable runnable) {
      this.queue.add(runnable);
      this.scheduleSelf();
   }

   private void scheduleSelf() {
      if (this.canRun() && this.wakeUp()) {
         try {
            this.executor.execute(this);
         } catch (RejectedExecutionException var4) {
            try {
               this.executor.execute(this);
            } catch (RejectedExecutionException var3) {
               LOGGER.error("Could not schedule ConsecutiveExecutor", var3);
            }
         }
      }

   }

   public int queueSize() {
      return this.queue.getSize();
   }

   public boolean hasQueuedTasks() {
      return this.isRunning() && !this.queue.isEmpty();
   }

   public String toString() {
      String var10000 = this.name;
      return var10000 + " " + String.valueOf(this.status.get()) + " " + this.queue.isEmpty();
   }

   public String getName() {
      return this.name;
   }

   public List createSamplers() {
      return ImmutableList.of(Sampler.create(this.name + "-queue-size", SampleType.CONSECUTIVE_EXECUTORS, this::queueSize));
   }

   private boolean wakeUp() {
      return this.status.compareAndSet(ConsecutiveExecutor.Status.SLEEPING, ConsecutiveExecutor.Status.RUNNING);
   }

   private void sleep() {
      this.status.compareAndSet(ConsecutiveExecutor.Status.RUNNING, ConsecutiveExecutor.Status.SLEEPING);
   }

   private boolean isRunning() {
      return this.status.get() == ConsecutiveExecutor.Status.RUNNING;
   }

   private boolean isClosed() {
      return this.status.get() == ConsecutiveExecutor.Status.CLOSED;
   }

   static enum Status {
      SLEEPING,
      RUNNING,
      CLOSED;

      // $FF: synthetic method
      private static Status[] method_63598() {
         return new Status[]{SLEEPING, RUNNING, CLOSED};
      }
   }
}
