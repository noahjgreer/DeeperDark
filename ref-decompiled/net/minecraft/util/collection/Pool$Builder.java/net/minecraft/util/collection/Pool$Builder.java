/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package net.minecraft.util.collection;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weighted;

public static class Pool.Builder<E> {
    private final ImmutableList.Builder<Weighted<E>> entries = ImmutableList.builder();

    public Pool.Builder<E> add(E object) {
        return this.add(object, 1);
    }

    public Pool.Builder<E> add(E object, int weight) {
        this.entries.add(new Weighted<E>(object, weight));
        return this;
    }

    public Pool<E> build() {
        return new Pool(this.entries.build());
    }
}
