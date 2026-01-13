/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.predicate.collection;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.predicate.collection.CollectionCountsPredicate;

public record CollectionCountsPredicate.Single<T, P extends Predicate<T>>(CollectionCountsPredicate.Entry<T, P> entry) implements CollectionCountsPredicate<T, P>
{
    @Override
    public boolean test(Iterable<T> iterable) {
        return this.entry.test(iterable);
    }

    @Override
    public List<CollectionCountsPredicate.Entry<T, P>> getEntries() {
        return List.of(this.entry);
    }

    @Override
    public /* synthetic */ boolean test(Object collection) {
        return this.test((Iterable)collection);
    }
}
