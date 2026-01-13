/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.predicate.collection;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.predicate.collection.CollectionContainsPredicate;

public record CollectionContainsPredicate.Single<T, P extends Predicate<T>>(P test) implements CollectionContainsPredicate<T, P>
{
    @Override
    public boolean test(Iterable<T> iterable) {
        for (T object : iterable) {
            if (!this.test.test(object)) continue;
            return true;
        }
        return false;
    }

    @Override
    public List<P> getPredicates() {
        return List.of(this.test);
    }

    @Override
    public /* synthetic */ boolean test(Object collection) {
        return this.test((Iterable)collection);
    }
}
