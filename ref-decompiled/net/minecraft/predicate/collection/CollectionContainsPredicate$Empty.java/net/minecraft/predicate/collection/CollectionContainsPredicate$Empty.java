/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.predicate.collection;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.predicate.collection.CollectionContainsPredicate;

public static class CollectionContainsPredicate.Empty<T, P extends Predicate<T>>
implements CollectionContainsPredicate<T, P> {
    @Override
    public boolean test(Iterable<T> iterable) {
        return true;
    }

    @Override
    public List<P> getPredicates() {
        return List.of();
    }

    @Override
    public /* synthetic */ boolean test(Object collection) {
        return this.test((Iterable)collection);
    }
}
