/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.crash;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;

static final class SuppressedExceptionsTracker.Entry
extends Record {
    final long timestampMs;
    final String location;
    final Class<? extends Throwable> cls;
    final String message;

    SuppressedExceptionsTracker.Entry(long timestampMs, String location, Class<? extends Throwable> cls, String message) {
        this.timestampMs = timestampMs;
        this.location = location;
        this.cls = cls;
        this.message = message;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{SuppressedExceptionsTracker.Entry.class, "timestampMs;location;cls;message", "timestampMs", "location", "cls", "message"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SuppressedExceptionsTracker.Entry.class, "timestampMs;location;cls;message", "timestampMs", "location", "cls", "message"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SuppressedExceptionsTracker.Entry.class, "timestampMs;location;cls;message", "timestampMs", "location", "cls", "message"}, this, object);
    }

    public long timestampMs() {
        return this.timestampMs;
    }

    public String location() {
        return this.location;
    }

    public Class<? extends Throwable> cls() {
        return this.cls;
    }

    public String message() {
        return this.message;
    }
}
