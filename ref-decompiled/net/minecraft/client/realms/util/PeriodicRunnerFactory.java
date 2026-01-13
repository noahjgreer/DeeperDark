/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.util.PeriodicRunnerFactory
 *  net.minecraft.client.realms.util.PeriodicRunnerFactory$PeriodicRunner
 *  net.minecraft.client.realms.util.PeriodicRunnerFactory$RunnersManager
 *  net.minecraft.client.util.Backoff
 *  net.minecraft.util.TimeSupplier
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.util;

import com.mojang.logging.LogUtils;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.util.PeriodicRunnerFactory;
import net.minecraft.client.util.Backoff;
import net.minecraft.util.TimeSupplier;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
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

    public <T> PeriodicRunner<T> create(String name, Callable<T> task, Duration cycle, Backoff backoff) {
        long l = this.timeUnit.convert(cycle);
        if (l == 0L) {
            throw new IllegalArgumentException("Period of " + String.valueOf(cycle) + " too short for selected resolution of " + String.valueOf((Object)this.timeUnit));
        }
        return new PeriodicRunner(this, name, task, l, backoff);
    }

    public RunnersManager create() {
        return new RunnersManager(this);
    }
}

