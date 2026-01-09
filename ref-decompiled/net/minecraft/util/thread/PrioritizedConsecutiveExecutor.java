package net.minecraft.util.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class PrioritizedConsecutiveExecutor extends ConsecutiveExecutor {
   public PrioritizedConsecutiveExecutor(int priorityCount, Executor executor, String name) {
      super(new TaskQueue.Prioritized(priorityCount), executor, name);
      ExecutorSampling.INSTANCE.add(this);
   }

   public TaskQueue.PrioritizedTask createTask(Runnable runnable) {
      return new TaskQueue.PrioritizedTask(0, runnable);
   }

   public CompletableFuture executeAsync(int priority, Consumer future) {
      CompletableFuture completableFuture = new CompletableFuture();
      this.send(new TaskQueue.PrioritizedTask(priority, () -> {
         future.accept(completableFuture);
      }));
      return completableFuture;
   }

   // $FF: synthetic method
   public Runnable createTask(final Runnable runnable) {
      return this.createTask(runnable);
   }
}
