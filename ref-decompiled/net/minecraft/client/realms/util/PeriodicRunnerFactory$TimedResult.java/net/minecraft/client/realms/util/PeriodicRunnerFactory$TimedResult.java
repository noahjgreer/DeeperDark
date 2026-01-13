/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.util;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
static final class PeriodicRunnerFactory.TimedResult<T>
extends Record {
    final T value;
    final long time;

    PeriodicRunnerFactory.TimedResult(T value, long time) {
        this.value = value;
        this.time = time;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{PeriodicRunnerFactory.TimedResult.class, "value;time", "value", "time"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PeriodicRunnerFactory.TimedResult.class, "value;time", "value", "time"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PeriodicRunnerFactory.TimedResult.class, "value;time", "value", "time"}, this, object);
    }

    public T value() {
        return this.value;
    }

    public long time() {
        return this.time;
    }
}
