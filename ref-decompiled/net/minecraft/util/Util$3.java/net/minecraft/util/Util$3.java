/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.util;

import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

static class Util.3
implements BiFunction<T, U, R> {
    private final Map<Pair<T, U>, R> cache = new ConcurrentHashMap();
    final /* synthetic */ BiFunction field_29656;

    Util.3(BiFunction biFunction) {
        this.field_29656 = biFunction;
    }

    @Override
    public R apply(T a, U b) {
        return this.cache.computeIfAbsent(Pair.of(a, b), pair -> this.field_29656.apply(pair.getFirst(), pair.getSecond()));
    }

    public String toString() {
        return "memoize/2[function=" + String.valueOf(this.field_29656) + ", size=" + this.cache.size() + "]";
    }
}
