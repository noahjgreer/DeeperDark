package net.minecraft.resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

public class SimpleResourceReload implements ResourceReload {
   private static final int FIRST_PREPARE_APPLY_WEIGHT = 2;
   private static final int SECOND_PREPARE_APPLY_WEIGHT = 2;
   private static final int RELOADER_WEIGHT = 1;
   final CompletableFuture prepareStageFuture = new CompletableFuture();
   @Nullable
   private CompletableFuture applyStageFuture;
   final Set waitingReloaders;
   private final int reloaderCount;
   private final AtomicInteger toPrepareCount = new AtomicInteger();
   private final AtomicInteger preparedCount = new AtomicInteger();
   private final AtomicInteger toApplyCount = new AtomicInteger();
   private final AtomicInteger appliedCount = new AtomicInteger();

   public static ResourceReload create(ResourceManager manager, List reloaders, Executor prepareExecutor, Executor applyExecutor, CompletableFuture initialStage) {
      SimpleResourceReload simpleResourceReload = new SimpleResourceReload(reloaders);
      simpleResourceReload.start(prepareExecutor, applyExecutor, manager, reloaders, SimpleResourceReload.Factory.SIMPLE, initialStage);
      return simpleResourceReload;
   }

   protected SimpleResourceReload(List waitingReloaders) {
      this.reloaderCount = waitingReloaders.size();
      this.waitingReloaders = new HashSet(waitingReloaders);
   }

   protected void start(Executor prepareExecutor, Executor applyExecutor, ResourceManager manager, List reloaders, Factory factory, CompletableFuture initialStage) {
      this.applyStageFuture = this.startAsync(prepareExecutor, applyExecutor, manager, reloaders, factory, initialStage);
   }

   protected CompletableFuture startAsync(Executor prepareExecutor, Executor applyExecutor, ResourceManager manager, List reloaders, Factory factory, CompletableFuture initialStage) {
      Executor executor = (runnable) -> {
         this.toPrepareCount.incrementAndGet();
         prepareExecutor.execute(() -> {
            runnable.run();
            this.preparedCount.incrementAndGet();
         });
      };
      Executor executor2 = (runnable) -> {
         this.toApplyCount.incrementAndGet();
         applyExecutor.execute(() -> {
            runnable.run();
            this.appliedCount.incrementAndGet();
         });
      };
      this.toPrepareCount.incrementAndGet();
      AtomicInteger var10001 = this.preparedCount;
      Objects.requireNonNull(var10001);
      initialStage.thenRun(var10001::incrementAndGet);
      CompletableFuture completableFuture = initialStage;
      List list = new ArrayList();

      CompletableFuture completableFuture2;
      for(Iterator var11 = reloaders.iterator(); var11.hasNext(); completableFuture = completableFuture2) {
         ResourceReloader resourceReloader = (ResourceReloader)var11.next();
         ResourceReloader.Synchronizer synchronizer = this.createSynchronizer(resourceReloader, completableFuture, applyExecutor);
         completableFuture2 = factory.create(synchronizer, manager, resourceReloader, executor, executor2);
         list.add(completableFuture2);
      }

      return Util.combine(list);
   }

   private ResourceReloader.Synchronizer createSynchronizer(final ResourceReloader reloader, final CompletableFuture future, final Executor applyExecutor) {
      return new ResourceReloader.Synchronizer() {
         public CompletableFuture whenPrepared(Object preparedObject) {
            applyExecutor.execute(() -> {
               SimpleResourceReload.this.waitingReloaders.remove(reloader);
               if (SimpleResourceReload.this.waitingReloaders.isEmpty()) {
                  SimpleResourceReload.this.prepareStageFuture.complete(Unit.INSTANCE);
               }

            });
            return SimpleResourceReload.this.prepareStageFuture.thenCombine(future, (unit, object2) -> {
               return preparedObject;
            });
         }
      };
   }

   public CompletableFuture whenComplete() {
      return (CompletableFuture)Objects.requireNonNull(this.applyStageFuture, "not started");
   }

   public float getProgress() {
      int i = this.reloaderCount - this.waitingReloaders.size();
      float f = (float)toWeighted(this.preparedCount.get(), this.appliedCount.get(), i);
      float g = (float)toWeighted(this.toPrepareCount.get(), this.toApplyCount.get(), this.reloaderCount);
      return f / g;
   }

   private static int toWeighted(int prepare, int apply, int total) {
      return prepare * 2 + apply * 2 + total * 1;
   }

   public static ResourceReload start(ResourceManager manager, List reloaders, Executor prepareExecutor, Executor applyExecutor, CompletableFuture initialStage, boolean profiled) {
      return profiled ? ProfiledResourceReload.start(manager, reloaders, prepareExecutor, applyExecutor, initialStage) : create(manager, reloaders, prepareExecutor, applyExecutor, initialStage);
   }

   @FunctionalInterface
   protected interface Factory {
      Factory SIMPLE = (synchronizer, manager, reloader, prepareExecutor, applyExecutor) -> {
         return reloader.reload(synchronizer, manager, prepareExecutor, applyExecutor);
      };

      CompletableFuture create(ResourceReloader.Synchronizer synchronizer, ResourceManager manager, ResourceReloader reloader, Executor prepareExecutor, Executor applyExecutor);
   }
}
