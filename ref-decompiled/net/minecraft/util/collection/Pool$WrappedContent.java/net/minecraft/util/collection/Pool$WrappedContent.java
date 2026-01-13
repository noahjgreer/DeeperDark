/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.collection;

import java.util.List;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weighted;

static class Pool.WrappedContent<E>
implements Pool.Content<E> {
    private final Weighted<?>[] entries;

    Pool.WrappedContent(List<Weighted<E>> entries) {
        this.entries = (Weighted[])entries.toArray(Weighted[]::new);
    }

    @Override
    public E get(int i) {
        for (Weighted<?> weighted : this.entries) {
            if ((i -= weighted.weight()) >= 0) continue;
            return (E)weighted.value();
        }
        throw new IllegalStateException(i + " exceeded total weight");
    }
}
