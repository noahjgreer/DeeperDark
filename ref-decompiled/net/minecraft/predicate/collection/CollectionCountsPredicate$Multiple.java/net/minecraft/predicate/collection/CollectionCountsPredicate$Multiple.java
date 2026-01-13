/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.predicate.collection;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.predicate.collection.CollectionCountsPredicate;

public record CollectionCountsPredicate.Multiple<T, P extends Predicate<T>>(List<CollectionCountsPredicate.Entry<T, P>> entries) implements CollectionCountsPredicate<T, P>
{
    @Override
    public boolean test(Iterable<T> iterable) {
        for (CollectionCountsPredicate.Entry<T, P> entry : this.entries) {
            if (entry.test(iterable)) continue;
            return false;
        }
        return true;
    }

    @Override
    public List<CollectionCountsPredicate.Entry<T, P>> getEntries() {
        return this.entries;
    }

    @Override
    public /* synthetic */ boolean test(Object collection) {
        return this.test((Iterable)collection);
    }
}
