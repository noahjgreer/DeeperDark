package net.minecraft.util.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public interface TaskExecutor extends AutoCloseable {
   String getName();

   void send(Runnable runnable);

   default void close() {
   }

   Runnable createTask(Runnable runnable);

   default CompletableFuture executeAsync(Consumer future) {
      CompletableFuture completableFuture = new CompletableFuture();
      this.send(this.createTask(() -> {
         future.accept(completableFuture);
      }));
      return completableFuture;
   }

   static TaskExecutor of(final String name, final Executor executor) {
      return new TaskExecutor() {
         public String getName() {
            return name;
         }

         public void send(Runnable runnable) {
            executor.execute(runnable);
         }

         public Runnable createTask(Runnable runnable) {
            return runnable;
         }

         public String toString() {
            return name;
         }
      };
   }
}
