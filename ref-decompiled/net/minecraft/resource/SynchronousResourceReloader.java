package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.Unit;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;

public interface SynchronousResourceReloader extends ResourceReloader {
   default CompletableFuture reload(ResourceReloader.Synchronizer synchronizer, ResourceManager resourceManager, Executor executor, Executor executor2) {
      return synchronizer.whenPrepared(Unit.INSTANCE).thenRunAsync(() -> {
         Profiler profiler = Profilers.get();
         profiler.push("listener");
         this.reload(resourceManager);
         profiler.pop();
      }, executor2);
   }

   void reload(ResourceManager manager);
}
