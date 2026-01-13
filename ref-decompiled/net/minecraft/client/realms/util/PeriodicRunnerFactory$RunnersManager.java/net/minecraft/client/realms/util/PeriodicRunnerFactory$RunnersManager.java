/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.util.PeriodicRunnerFactory;

@Environment(value=EnvType.CLIENT)
public class PeriodicRunnerFactory.RunnersManager {
    private final List<PeriodicRunnerFactory.ResultListenableRunner<?>> runners = new ArrayList();

    public <T> void add(PeriodicRunnerFactory.PeriodicRunner<T> runner, Consumer<T> resultListener) {
        PeriodicRunnerFactory.ResultListenableRunner<T> resultListenableRunner = new PeriodicRunnerFactory.ResultListenableRunner<T>(PeriodicRunnerFactory.this, runner, resultListener);
        this.runners.add(resultListenableRunner);
        resultListenableRunner.runListener();
    }

    public void forceRunListeners() {
        for (PeriodicRunnerFactory.ResultListenableRunner<?> resultListenableRunner : this.runners) {
            resultListenableRunner.forceRunListener();
        }
    }

    public void runAll() {
        for (PeriodicRunnerFactory.ResultListenableRunner<?> resultListenableRunner : this.runners) {
            resultListenableRunner.run(PeriodicRunnerFactory.this.timeSupplier.get(PeriodicRunnerFactory.this.timeUnit));
        }
    }

    public void resetAll() {
        for (PeriodicRunnerFactory.ResultListenableRunner<?> resultListenableRunner : this.runners) {
            resultListenableRunner.reset();
        }
    }
}
