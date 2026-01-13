/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectMap
 */
package net.minecraft.component;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import java.util.Optional;
import net.minecraft.component.Component;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;

public static class ComponentChanges.Builder {
    private final Reference2ObjectMap<ComponentType<?>, Optional<?>> changes = new Reference2ObjectArrayMap();

    ComponentChanges.Builder() {
    }

    public <T> ComponentChanges.Builder add(ComponentType<T> type, T value) {
        this.changes.put(type, Optional.of(value));
        return this;
    }

    public <T> ComponentChanges.Builder remove(ComponentType<T> type) {
        this.changes.put(type, Optional.empty());
        return this;
    }

    public <T> ComponentChanges.Builder add(Component<T> component) {
        return this.add(component.type(), component.value());
    }

    public ComponentChanges build() {
        if (this.changes.isEmpty()) {
            return EMPTY;
        }
        return new ComponentChanges(this.changes);
    }
}
