/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;
import net.minecraft.util.TimeSupplier;

public static interface TimeSupplier.Nanoseconds
extends TimeSupplier,
LongSupplier {
    @Override
    default public long get(TimeUnit timeUnit) {
        return timeUnit.convert(this.getAsLong(), TimeUnit.NANOSECONDS);
    }
}
