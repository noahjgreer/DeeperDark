package net.minecraft.util.thread;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Queues;
import com.mojang.jtracy.TracyClient;
import com.mojang.jtracy.Zone;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.CheckReturnValue;
import net.minecraft.SharedConstants;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.profiler.SampleType;
import net.minecraft.util.profiler.Sampler;
import org.slf4j.Logger;

public abstract class ThreadExecutor implements SampleableExecutor, TaskExecutor, Executor {
   public static final long field_52421 = 100000L;
   private final String name;
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Queue tasks = Queues.newConcurrentLinkedQueue();
   private int executionsInProgress;

   protected ThreadExecutor(String name) {
      this.name = name;
      ExecutorSampling.INSTANCE.add(this);
   }

   protected abstract boolean canExecute(Runnable task);

   public boolean isOnThread() {
      return Thread.currentThread() == this.getThread();
   }

   protected abstract Thread getThread();

   protected boolean shouldExecuteAsync() {
      return !this.isOnThread();
   }

   public int getTaskCount() {
      return this.tasks.size();
   }

   public String getName() {
      return this.name;
   }

   public CompletableFuture submit(Supplier task) {
      return this.shouldExecuteAsync() ? CompletableFuture.supplyAsync(task, this) : CompletableFuture.completedFuture(task.get());
   }

   private CompletableFuture submitAsync(Runnable runnable) {
      return CompletableFuture.supplyAsync(() -> {
         runnable.run();
         return null;
      }, this);
   }

   @CheckReturnValue
   public CompletableFuture submit(Runnable task) {
      if (this.shouldExecuteAsync()) {
         return this.submitAsync(task);
      } else {
         task.run();
         return CompletableFuture.completedFuture((Object)null);
      }
   }

   public void submitAndJoin(Runnable runnable) {
      if (!this.isOnThread()) {
         this.submitAsync(runnable).join();
      } else {
         runnable.run();
      }

   }

   public void send(Runnable runnable) {
      this.tasks.add(runnable);
      LockSupport.unpark(this.getThread());
   }

   public void execute(Runnable runnable) {
      if (this.shouldExecuteAsync()) {
         this.send(this.createTask(runnable));
      } else {
         runnable.run();
      }

   }

   public void executeSync(Runnable runnable) {
      this.execute(runnable);
   }

   protected void cancelTasks() {
      this.tasks.clear();
   }

   protected void runTasks() {
      while(this.runTask()) {
      }

   }

   public boolean runTask() {
      Runnable runnable = (Runnable)this.tasks.peek();
      if (runnable == null) {
         return false;
      } else if (this.executionsInProgress == 0 && !this.canExecute(runnable)) {
         return false;
      } else {
         this.executeTask((Runnable)this.tasks.remove());
         return true;
      }
   }

   public void runTasks(BooleanSupplier stopCondition) {
      ++this.executionsInProgress;

      try {
         while(!stopCondition.getAsBoolean()) {
            if (!this.runTask()) {
               this.waitForTasks();
            }
         }
      } finally {
         --this.executionsInProgress;
      }

   }

   protected void waitForTasks() {
      Thread.yield();
      LockSupport.parkNanos("waiting for tasks", 100000L);
   }

   protected void executeTask(Runnable task) {
      try {
         Zone zone = TracyClient.beginZone("Task", SharedConstants.isDevelopment);

         try {
            task.run();
         } catch (Throwable var6) {
            if (zone != null) {
               try {
                  zone.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (zone != null) {
            zone.close();
         }
      } catch (Exception var7) {
         LOGGER.error(LogUtils.FATAL_MARKER, "Error executing task on {}", this.getName(), var7);
         if (isMemoryError(var7)) {
            throw var7;
         }
      }

   }

   public List createSamplers() {
      return ImmutableList.of(Sampler.create(this.name + "-pending-tasks", SampleType.EVENT_LOOPS, this::getTaskCount));
   }

   public static boolean isMemoryError(Throwable exception) {
      if (exception instanceof CrashException crashException) {
         return isMemoryError(crashException.getCause());
      } else {
         return exception instanceof OutOfMemoryError || exception instanceof StackOverflowError;
      }
   }
}
