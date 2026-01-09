package net.minecraft.resource;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;

public abstract class SinglePreparationResourceReloader implements ResourceReloader {
   public final CompletableFuture reload(ResourceReloader.Synchronizer synchronizer, ResourceManager resourceManager, Executor executor, Executor executor2) {
      CompletableFuture var10000 = CompletableFuture.supplyAsync(() -> {
         return this.prepare(resourceManager, Profilers.get());
      }, executor);
      Objects.requireNonNull(synchronizer);
      return var10000.thenCompose(synchronizer::whenPrepared).thenAcceptAsync((prepared) -> {
         this.apply(prepared, resourceManager, Profilers.get());
      }, executor2);
   }

   protected abstract Object prepare(ResourceManager manager, Profiler profiler);

   protected abstract void apply(Object prepared, ResourceManager manager, Profiler profiler);
}
