/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

static class Util.2
implements Function<T, R> {
    private final Map<T, R> cache = new ConcurrentHashMap();
    final /* synthetic */ Function field_29654;

    Util.2(Function function) {
        this.field_29654 = function;
    }

    @Override
    public R apply(T object) {
        return this.cache.computeIfAbsent(object, this.field_29654);
    }

    public String toString() {
        return "memoize/1[function=" + String.valueOf(this.field_29654) + ", size=" + this.cache.size() + "]";
    }
}
