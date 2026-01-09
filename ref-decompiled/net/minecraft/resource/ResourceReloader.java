package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@FunctionalInterface
public interface ResourceReloader {
   CompletableFuture reload(Synchronizer synchronizer, ResourceManager manager, Executor prepareExecutor, Executor applyExecutor);

   default String getName() {
      return this.getClass().getSimpleName();
   }

   @FunctionalInterface
   public interface Synchronizer {
      CompletableFuture whenPrepared(Object preparedObject);
   }
}
