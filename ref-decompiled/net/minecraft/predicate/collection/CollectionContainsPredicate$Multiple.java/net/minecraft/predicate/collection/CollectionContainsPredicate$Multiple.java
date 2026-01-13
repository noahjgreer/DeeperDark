/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.predicate.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.predicate.collection.CollectionContainsPredicate;

public record CollectionContainsPredicate.Multiple<T, P extends Predicate<T>>(List<P> tests) implements CollectionContainsPredicate<T, P>
{
    @Override
    public boolean test(Iterable<T> iterable) {
        ArrayList<P> list = new ArrayList<P>(this.tests);
        for (Object object : iterable) {
            list.removeIf(predicate -> predicate.test(object));
            if (!list.isEmpty()) continue;
            return true;
        }
        return false;
    }

    @Override
    public List<P> getPredicates() {
        return this.tests;
    }

    @Override
    public /* synthetic */ boolean test(Object collection) {
        return this.test((Iterable)collection);
    }
}
