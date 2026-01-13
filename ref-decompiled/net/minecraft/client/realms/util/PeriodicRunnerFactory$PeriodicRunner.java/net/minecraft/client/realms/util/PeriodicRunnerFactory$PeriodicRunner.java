/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms.util;

import com.mojang.datafixers.util.Either;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.util.PeriodicRunnerFactory;
import net.minecraft.client.util.Backoff;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PeriodicRunnerFactory.PeriodicRunner<T> {
    private final String name;
    private final Callable<T> task;
    private final long unitDuration;
    private final Backoff backoff;
    private @Nullable CompletableFuture<PeriodicRunnerFactory.TimedErrableResult<T>> resultFuture;
     @Nullable PeriodicRunnerFactory.TimedResult<T> lastResult;
    private long nextTime = -1L;

    PeriodicRunnerFactory.PeriodicRunner(String name, Callable<T> task, long unitDuration, Backoff backoff) {
        this.name = name;
        this.task = task;
        this.unitDuration = unitDuration;
        this.backoff = backoff;
    }

    void run(long currentTime) {
        if (this.resultFuture != null) {
            PeriodicRunnerFactory.TimedErrableResult timedErrableResult = this.resultFuture.getNow(null);
            if (timedErrableResult == null) {
                return;
            }
            this.resultFuture = null;
            long l = timedErrableResult.time;
            timedErrableResult.value().ifLeft(value -> {
                this.lastResult = new PeriodicRunnerFactory.TimedResult<Object>(value, l);
                this.nextTime = l + this.unitDuration * this.backoff.success();
            }).ifRight(exception -> {
                long m = this.backoff.fail();
                LOGGER.warn("Failed to process task {}, will repeat after {} cycles", new Object[]{this.name, m, exception});
                this.nextTime = l + this.unitDuration * m;
            });
        }
        if (this.nextTime <= currentTime) {
            this.resultFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    T object = this.task.call();
                    long l = PeriodicRunnerFactory.this.timeSupplier.get(PeriodicRunnerFactory.this.timeUnit);
                    return new PeriodicRunnerFactory.TimedErrableResult(Either.left(object), l);
                }
                catch (Exception exception) {
                    long l = PeriodicRunnerFactory.this.timeSupplier.get(PeriodicRunnerFactory.this.timeUnit);
                    return new PeriodicRunnerFactory.TimedErrableResult(Either.right((Object)exception), l);
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
