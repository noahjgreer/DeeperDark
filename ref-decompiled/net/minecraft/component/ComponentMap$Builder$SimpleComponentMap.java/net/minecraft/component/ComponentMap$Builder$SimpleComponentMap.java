/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterators
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectMaps
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.component;

import com.google.common.collect.Iterators;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.component.Component;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import org.jspecify.annotations.Nullable;

record ComponentMap.Builder.SimpleComponentMap(Reference2ObjectMap<ComponentType<?>, Object> map) implements ComponentMap
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
