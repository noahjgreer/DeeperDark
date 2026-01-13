/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package net.minecraft.predicate.component;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.component.ComponentType;
import net.minecraft.predicate.component.ComponentMapPredicate;
import net.minecraft.predicate.component.ComponentPredicate;
import net.minecraft.predicate.component.ComponentsPredicate;

public static class ComponentsPredicate.Builder {
    private ComponentMapPredicate exact = ComponentMapPredicate.EMPTY;
    private final ImmutableMap.Builder<ComponentPredicate.Type<?>, ComponentPredicate> partial = ImmutableMap.builder();

    private ComponentsPredicate.Builder() {
    }

    public static ComponentsPredicate.Builder create() {
        return new ComponentsPredicate.Builder();
    }

    public <T extends ComponentType<?>> ComponentsPredicate.Builder has(ComponentType<?> type) {
        ComponentPredicate.OfExistence ofExistence = ComponentPredicate.OfExistence.toPredicateType(type);
        this.partial.put((Object)ofExistence, (Object)ofExistence.getPredicate());
        return this;
    }

    public <T extends ComponentPredicate> ComponentsPredicate.Builder partial(ComponentPredicate.Type<T> type, T predicate) {
        this.partial.put(type, predicate);
        return this;
    }

    public ComponentsPredicate.Builder exact(ComponentMapPredicate exact) {
        this.exact = exact;
        return this;
    }

    public ComponentsPredicate build() {
        return new ComponentsPredicate(this.exact, (Map<ComponentPredicate.Type<?>, ComponentPredicate>)this.partial.buildOrThrow());
    }
}
