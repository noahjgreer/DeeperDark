/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.util;

import com.mojang.datafixers.util.Either;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
static final class PeriodicRunnerFactory.TimedErrableResult<T>
extends Record {
    private final Either<T, Exception> value;
    final long time;

    PeriodicRunnerFactory.TimedErrableResult(Either<T, Exception> value, long time) {
        this.value = value;
        this.time = time;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{PeriodicRunnerFactory.TimedErrableResult.class, "value;time", "value", "time"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PeriodicRunnerFactory.TimedErrableResult.class, "value;time", "value", "time"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PeriodicRunnerFactory.TimedErrableResult.class, "value;time", "value", "time"}, this, object);
    }

    public Either<T, Exception> value() {
        return this.value;
    }

    public long time() {
        return this.time;
    }
}
