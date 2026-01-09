package net.minecraft.resource;

import com.google.common.base.Stopwatch;
import com.mojang.logging.LogUtils;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import org.slf4j.Logger;

public class ProfiledResourceReload extends SimpleResourceReload {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Stopwatch reloadTimer = Stopwatch.createUnstarted();

   public static ResourceReload start(ResourceManager manager, List reloaders, Executor prepareExecutor, Executor applyExecutor, CompletableFuture initialStage) {
      ProfiledResourceReload profiledResourceReload = new ProfiledResourceReload(reloaders);
      profiledResourceReload.start(prepareExecutor, applyExecutor, manager, reloaders, (synchronizer, managerx, reloader, prepare, apply) -> {
         AtomicLong atomicLong = new AtomicLong();
         AtomicLong atomicLong2 = new AtomicLong();
         AtomicLong atomicLong3 = new AtomicLong();
         AtomicLong atomicLong4 = new AtomicLong();
         CompletableFuture completableFuture = reloader.reload(synchronizer, managerx, getProfiledExecutor(prepare, atomicLong, atomicLong2, reloader.getName()), getProfiledExecutor(apply, atomicLong3, atomicLong4, reloader.getName()));
         return completableFuture.thenApplyAsync((v) -> {
            LOGGER.debug("Finished reloading {}", reloader.getName());
            return new Summary(reloader.getName(), atomicLong, atomicLong2, atomicLong3, atomicLong4);
         }, applyExecutor);
      }, initialStage);
      return profiledResourceReload;
   }

   private ProfiledResourceReload(List waitingReloaders) {
      super(waitingReloaders);
      this.reloadTimer.start();
   }

   protected CompletableFuture startAsync(Executor prepareExecutor, Executor applyExecutor, ResourceManager manager, List reloaders, SimpleResourceReload.Factory factory, CompletableFuture initialStage) {
      return super.startAsync(prepareExecutor, applyExecutor, manager, reloaders, factory, initialStage).thenApplyAsync(this::finish, applyExecutor);
   }

   private static Executor getProfiledExecutor(Executor executor, AtomicLong output, AtomicLong counter, String name) {
      return (runnable) -> {
         executor.execute(() -> {
            Profiler profiler = Profilers.get();
            profiler.push(name);
            long l = Util.getMeasuringTimeNano();
            runnable.run();
            output.addAndGet(Util.getMeasuringTimeNano() - l);
            counter.incrementAndGet();
            profiler.pop();
         });
      };
   }

   private List finish(List summaries) {
      this.reloadTimer.stop();
      long l = 0L;
      LOGGER.info("Resource reload finished after {} ms", this.reloadTimer.elapsed(TimeUnit.MILLISECONDS));

      long o;
      for(Iterator var4 = summaries.iterator(); var4.hasNext(); l += o) {
         Summary summary = (Summary)var4.next();
         long m = TimeUnit.NANOSECONDS.toMillis(summary.prepareTimeMs.get());
         long n = summary.preparationCount.get();
         o = TimeUnit.NANOSECONDS.toMillis(summary.applyTimeMs.get());
         long p = summary.reloadCount.get();
         long q = m + o;
         long r = n + p;
         String string = summary.name;
         LOGGER.info("{} took approximately {} tasks/{} ms ({} tasks/{} ms preparing, {} tasks/{} ms applying)", new Object[]{string, r, q, n, m, p, o});
      }

      LOGGER.info("Total blocking time: {} ms", l);
      return summaries;
   }

   public static record Summary(String name, AtomicLong prepareTimeMs, AtomicLong preparationCount, AtomicLong applyTimeMs, AtomicLong reloadCount) {
      final String name;
      final AtomicLong prepareTimeMs;
      final AtomicLong preparationCount;
      final AtomicLong applyTimeMs;
      final AtomicLong reloadCount;

      public Summary(String name, AtomicLong atomicLong, AtomicLong atomicLong2, AtomicLong atomicLong3, AtomicLong atomicLong4) {
         this.name = name;
         this.prepareTimeMs = atomicLong;
         this.preparationCount = atomicLong2;
         this.applyTimeMs = atomicLong3;
         this.reloadCount = atomicLong4;
      }

      public String name() {
         return this.name;
      }

      public AtomicLong prepareTimeMs() {
         return this.prepareTimeMs;
      }

      public AtomicLong preparationCount() {
         return this.preparationCount;
      }

      public AtomicLong applyTimeMs() {
         return this.applyTimeMs;
      }

      public AtomicLong reloadCount() {
         return this.reloadCount;
      }
   }
}
