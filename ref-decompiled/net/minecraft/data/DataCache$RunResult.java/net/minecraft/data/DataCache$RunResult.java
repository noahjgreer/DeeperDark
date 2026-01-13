/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.data.DataCache;

public record DataCache.RunResult(String providerName, DataCache.CachedData cache, int cacheMissCount) {
    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{DataCache.RunResult.class, "providerId;cache;writes", "providerName", "cache", "cacheMissCount"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{DataCache.RunResult.class, "providerId;cache;writes", "providerName", "cache", "cacheMissCount"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{DataCache.RunResult.class, "providerId;cache;writes", "providerName", "cache", "cacheMissCount"}, this, object);
    }
}
