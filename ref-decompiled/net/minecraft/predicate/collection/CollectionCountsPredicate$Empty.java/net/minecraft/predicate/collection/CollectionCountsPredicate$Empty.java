/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.predicate.collection;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.predicate.collection.CollectionCountsPredicate;

public static class CollectionCountsPredicate.Empty<T, P extends Predicate<T>>
implements CollectionCountsPredicate<T, P> {
    @Override
    public boolean test(Iterable<T> iterable) {
        return true;
    }

    @Override
    public List<CollectionCountsPredicate.Entry<T, P>> getEntries() {
        return List.of();
    }

    @Override
    public /* synthetic */ boolean test(Object collection) {
        return this.test((Iterable)collection);
    }
}
