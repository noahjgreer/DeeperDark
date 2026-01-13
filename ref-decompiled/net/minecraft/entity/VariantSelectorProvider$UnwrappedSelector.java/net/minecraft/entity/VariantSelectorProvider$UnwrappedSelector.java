/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Comparator;
import net.minecraft.entity.VariantSelectorProvider;

public static final class VariantSelectorProvider.UnwrappedSelector<C, T>
extends Record {
    private final T entry;
    final int priority;
    final VariantSelectorProvider.SelectorCondition<C> condition;
    public static final Comparator<VariantSelectorProvider.UnwrappedSelector<?, ?>> PRIORITY_COMPARATOR = Comparator.comparingInt(VariantSelectorProvider.UnwrappedSelector::priority).reversed();

    public VariantSelectorProvider.UnwrappedSelector(T entry, int priority, VariantSelectorProvider.SelectorCondition<C> condition) {
        this.entry = entry;
        this.priority = priority;
        this.condition = condition;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{VariantSelectorProvider.UnwrappedSelector.class, "entry;priority;condition", "entry", "priority", "condition"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{VariantSelectorProvider.UnwrappedSelector.class, "entry;priority;condition", "entry", "priority", "condition"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{VariantSelectorProvider.UnwrappedSelector.class, "entry;priority;condition", "entry", "priority", "condition"}, this, object);
    }

    public T entry() {
        return this.entry;
    }

    public int priority() {
        return this.priority;
    }

    public VariantSelectorProvider.SelectorCondition<C> condition() {
        return this.condition;
    }
}
