package net.minecraft.client.realms.util;

import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.Backoff;
import net.minecraft.util.TimeSupplier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class PeriodicRunnerFactory {
   static final Logger LOGGER = LogUtils.getLogger();
   final Executor executor;
   final TimeUnit timeUnit;
   final TimeSupplier timeSupplier;

   public PeriodicRunnerFactory(Executor executor, TimeUnit timeUnit, TimeSupplier timeSupplier) {
      this.executor = executor;
      this.timeUnit = timeUnit;
      this.timeSupplier = timeSupplier;
   }

   public PeriodicRunner create(String name, Callable task, Duration cycle, Backoff backoff) {
      long l = this.timeUnit.convert(cycle);
      if (l == 0L) {
         String var10002 = String.valueOf(cycle);
         throw new IllegalArgumentException("Period of " + var10002 + " too short for selected resolution of " + String.valueOf(this.timeUnit));
      } else {
         return new PeriodicRunner(name, task, l, backoff);
      }
   }

   public RunnersManager create() {
      return new RunnersManager();
   }

   @Environment(EnvType.CLIENT)
   public class PeriodicRunner {
      private final String name;
      private final Callable task;
      private final long unitDuration;
      private final Backoff backoff;
      @Nullable
      private CompletableFuture resultFuture;
      @Nullable
      TimedResult lastResult;
      private long nextTime = -1L;

      PeriodicRunner(final String name, final Callable task, final long unitDuration, final Backoff backoff) {
         this.name = name;
         this.task = task;
         this.unitDuration = unitDuration;
         this.backoff = backoff;
      }

      void run(long currentTime) {
         if (this.resultFuture != null) {
            TimedErrableResult timedErrableResult = (TimedErrableResult)this.resultFuture.getNow((Object)null);
            if (timedErrableResult == null) {
               return;
            }

            this.resultFuture = null;
            long l = timedErrableResult.time;
            timedErrableResult.value().ifLeft((value) -> {
               this.lastResult = new TimedResult(value, l);
               this.nextTime = l + this.unitDuration * this.backoff.success();
            }).ifRight((exception) -> {
               long m = this.backoff.fail();
               PeriodicRunnerFactory.LOGGER.warn("Failed to process task {}, will repeat after {} cycles", new Object[]{this.name, m, exception});
               this.nextTime = l + this.unitDuration * m;
            });
         }

         if (this.nextTime <= currentTime) {
            this.resultFuture = CompletableFuture.supplyAsync(() -> {
               long l;
               try {
                  Object object = this.task.call();
                  l = PeriodicRunnerFactory.this.timeSupplier.get(PeriodicRunnerFactory.this.timeUnit);
                  return new TimedErrableResult(Either.left(object), l);
               } catch (Exception var4) {
                  l = PeriodicRunnerFactory.this.timeSupplier.get(PeriodicRunnerFactory.this.timeUnit);
                  return new TimedErrableResult(Either.right(var4), l);
               }
            }, PeriodicRunnerFactory.this.executor);
         }

      }

      public void reset() {
         this.resultFuture = null;
         this.lastResult = null;
         this.nextTime = -1L;
      }
   }

   @Environment(EnvType.CLIENT)
   public class RunnersManager {
      private final List runners = new ArrayList();

      public void add(PeriodicRunner runner, Consumer resultListener) {
         ResultListenableRunner resultListenableRunner = PeriodicRunnerFactory.this.new ResultListenableRunner(PeriodicRunnerFactory.this, runner, resultListener);
         this.runners.add(resultListenableRunner);
         resultListenableRunner.runListener();
      }

      public void forceRunListeners() {
         Iterator var1 = this.runners.iterator();

         while(var1.hasNext()) {
            ResultListenableRunner resultListenableRunner = (ResultListenableRunner)var1.next();
            resultListenableRunner.forceRunListener();
         }

      }

      public void runAll() {
         Iterator var1 = this.runners.iterator();

         while(var1.hasNext()) {
            ResultListenableRunner resultListenableRunner = (ResultListenableRunner)var1.next();
            resultListenableRunner.run(PeriodicRunnerFactory.this.timeSupplier.get(PeriodicRunnerFactory.this.timeUnit));
         }

      }

      public void resetAll() {
         Iterator var1 = this.runners.iterator();

         while(var1.hasNext()) {
            ResultListenableRunner resultListenableRunner = (ResultListenableRunner)var1.next();
            resultListenableRunner.reset();
         }

      }
   }

   @Environment(EnvType.CLIENT)
   class ResultListenableRunner {
      private final PeriodicRunner runner;
      private final Consumer resultListener;
      private long lastRunTime = -1L;

      ResultListenableRunner(final PeriodicRunnerFactory periodicRunnerFactory, final PeriodicRunner runner, final Consumer resultListener) {
         this.runner = runner;
         this.resultListener = resultListener;
      }

      void run(long currentTime) {
         this.runner.run(currentTime);
         this.runListener();
      }

      void runListener() {
         TimedResult timedResult = this.runner.lastResult;
         if (timedResult != null && this.lastRunTime < timedResult.time) {
            this.resultListener.accept(timedResult.value);
            this.lastRunTime = timedResult.time;
         }

      }

      void forceRunListener() {
         TimedResult timedResult = this.runner.lastResult;
         if (timedResult != null) {
            this.resultListener.accept(timedResult.value);
            this.lastRunTime = timedResult.time;
         }

      }

      void reset() {
         this.runner.reset();
         this.lastRunTime = -1L;
      }
   }

   @Environment(EnvType.CLIENT)
   private static record TimedResult(Object value, long time) {
      final Object value;
      final long time;

      TimedResult(Object object, long l) {
         this.value = object;
         this.time = l;
      }

      public Object value() {
         return this.value;
      }

      public long time() {
         return this.time;
      }
   }

   @Environment(EnvType.CLIENT)
   static record TimedErrableResult(Either value, long time) {
      final long time;

      TimedErrableResult(Either either, long l) {
         this.value = either;
         this.time = l;
      }

      public Either value() {
         return this.value;
      }

      public long time() {
         return this.time;
      }
   }
}
