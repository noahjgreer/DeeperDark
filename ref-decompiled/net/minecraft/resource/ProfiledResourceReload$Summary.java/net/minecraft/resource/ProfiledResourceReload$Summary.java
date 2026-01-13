/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.concurrent.atomic.AtomicLong;

public static final class ProfiledResourceReload.Summary
extends Record {
    final String name;
    final AtomicLong prepareTimeMs;
    final AtomicLong preparationCount;
    final AtomicLong applyTimeMs;
    final AtomicLong reloadCount;

    public ProfiledResourceReload.Summary(String name, AtomicLong prepareTimeMs, AtomicLong preparationCount, AtomicLong applyTimeMs, AtomicLong reloadCount) {
        this.name = name;
        this.prepareTimeMs = prepareTimeMs;
        this.preparationCount = preparationCount;
        this.applyTimeMs = applyTimeMs;
        this.reloadCount = reloadCount;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ProfiledResourceReload.Summary.class, "name;preparationNanos;preparationCount;reloadNanos;reloadCount", "name", "prepareTimeMs", "preparationCount", "applyTimeMs", "reloadCount"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ProfiledResourceReload.Summary.class, "name;preparationNanos;preparationCount;reloadNanos;reloadCount", "name", "prepareTimeMs", "preparationCount", "applyTimeMs", "reloadCount"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ProfiledResourceReload.Summary.class, "name;preparationNanos;preparationCount;reloadNanos;reloadCount", "name", "prepareTimeMs", "preparationCount", "applyTimeMs", "reloadCount"}, this, object);
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
