/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.predicate.component;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.component.Component;
import net.minecraft.component.ComponentType;
import net.minecraft.predicate.component.ComponentMapPredicate;

public static class ComponentMapPredicate.Builder {
    private final List<Component<?>> components = new ArrayList();

    ComponentMapPredicate.Builder() {
    }

    public <T> ComponentMapPredicate.Builder add(Component<T> component) {
        return this.add(component.type(), component.value());
    }

    public <T> ComponentMapPredicate.Builder add(ComponentType<? super T> type, T value) {
        for (Component<?> component : this.components) {
            if (component.type() != type) continue;
            throw new IllegalArgumentException("Predicate already has component of type: '" + String.valueOf(type) + "'");
        }
        this.components.add(new Component<T>(type, value));
        return this;
    }

    public ComponentMapPredicate build() {
        return new ComponentMapPredicate(List.copyOf(this.components));
    }
}
