package net.minecraft.util.thread;

import com.mojang.logging.LogUtils;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import org.slf4j.Logger;

@FunctionalInterface
public interface FutureQueue {
   Logger LOGGER = LogUtils.getLogger();

   static FutureQueue immediate(final Executor executor) {
      return new FutureQueue() {
         public void append(CompletableFuture completableFuture, Consumer consumer) {
            completableFuture.thenAcceptAsync(consumer, executor).exceptionally((throwable) -> {
               LOGGER.error("Task failed", throwable);
               return null;
            });
         }
      };
   }

   default void append(Runnable callback) {
      this.append(CompletableFuture.completedFuture((Object)null), (current) -> {
         callback.run();
      });
   }

   void append(CompletableFuture future, Consumer callback);
}
