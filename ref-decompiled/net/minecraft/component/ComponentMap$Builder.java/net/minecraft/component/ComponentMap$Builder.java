/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterators
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectMaps
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap
 *  net.fabricmc.fabric.api.item.v1.FabricComponentMapBuilder
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.component;

import com.google.common.collect.Iterators;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.fabricmc.fabric.api.item.v1.FabricComponentMapBuilder;
import net.minecraft.component.Component;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import org.jspecify.annotations.Nullable;

public static class ComponentMap.Builder
implements FabricComponentMapBuilder {
    private final Reference2ObjectMap<ComponentType<?>, Object> components = new Reference2ObjectArrayMap();

    ComponentMap.Builder() {
    }

    public <T> ComponentMap.Builder add(ComponentType<T> type, @Nullable T value) {
        this.put(type, value);
        return this;
    }

    <T> void put(ComponentType<T> type, @Nullable Object value) {
        if (value != null) {
            this.components.put(type, value);
        } else {
            this.components.remove(type);
        }
    }

    public ComponentMap.Builder addAll(ComponentMap componentSet) {
        for (Component<?> component : componentSet) {
            this.components.put(component.type(), component.value());
        }
        return this;
    }

    public ComponentMap build() {
        return ComponentMap.Builder.build(this.components);
    }

    private static ComponentMap build(Map<ComponentType<?>, Object> components) {
        if (components.isEmpty()) {
            return EMPTY;
        }
        if (components.size() < 8) {
            return new SimpleComponentMap((Reference2ObjectMap<ComponentType<?>, Object>)new Reference2ObjectArrayMap(components));
        }
        return new SimpleComponentMap((Reference2ObjectMap<ComponentType<?>, Object>)new Reference2ObjectOpenHashMap(components));
    }

    record SimpleComponentMap(Reference2ObjectMap<ComponentType<?>, Object> map) implements ComponentMap
    {
        @Override
        public <T> @Nullable T get(ComponentType<? extends T> type) {
            return (T)this.map.get(type);
        }

        @Override
        public boolean contains(ComponentType<?> type) {
            return this.map.containsKey(type);
        }

        @Override
        public Set<ComponentType<?>> getTypes() {
            return this.map.keySet();
        }

        @Override
        public Iterator<Component<?>> iterator() {
            return Iterators.transform((Iterator)Reference2ObjectMaps.fastIterator(this.map), Component::of);
        }

        @Override
        public int size() {
            return this.map.size();
        }

        @Override
        public String toString() {
            return this.map.toString();
        }
    }
}
