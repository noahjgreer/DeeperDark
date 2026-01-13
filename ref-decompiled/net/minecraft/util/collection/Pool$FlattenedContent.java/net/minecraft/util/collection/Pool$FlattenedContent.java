/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.collection;

import java.util.Arrays;
import java.util.List;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weighted;

static class Pool.FlattenedContent<E>
implements Pool.Content<E> {
    private final Object[] entries;

    Pool.FlattenedContent(List<Weighted<E>> entries, int totalWeight) {
        this.entries = new Object[totalWeight];
        int i = 0;
        for (Weighted<E> weighted : entries) {
            int j = weighted.weight();
            Arrays.fill(this.entries, i, i + j, weighted.value());
            i += j;
        }
    }

    @Override
    public E get(int i) {
        return (E)this.entries[i];
    }
}
