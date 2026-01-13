/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.util;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.util.PeriodicRunnerFactory;

@Environment(value=EnvType.CLIENT)
class PeriodicRunnerFactory.ResultListenableRunner<T> {
    private final PeriodicRunnerFactory.PeriodicRunner<T> runner;
    private final Consumer<T> resultListener;
    private long lastRunTime = -1L;

    PeriodicRunnerFactory.ResultListenableRunner(PeriodicRunnerFactory periodicRunnerFactory, PeriodicRunnerFactory.PeriodicRunner<T> runner, Consumer<T> resultListener) {
        this.runner = runner;
        this.resultListener = resultListener;
    }

    void run(long currentTime) {
        this.runner.run(currentTime);
        this.runListener();
    }

    void runListener() {
        PeriodicRunnerFactory.TimedResult timedResult = this.runner.lastResult;
        if (timedResult != null && this.lastRunTime < timedResult.time) {
            this.resultListener.accept(timedResult.value);
            this.lastRunTime = timedResult.time;
        }
    }

    void forceRunListener() {
        PeriodicRunnerFactory.TimedResult timedResult = this.runner.lastResult;
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
