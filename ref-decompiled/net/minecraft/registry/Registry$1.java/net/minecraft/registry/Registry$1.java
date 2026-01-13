/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.registry;

import java.util.Iterator;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.collection.IndexedIterable;
import org.jspecify.annotations.Nullable;

class Registry.1
implements IndexedIterable<RegistryEntry<T>> {
    Registry.1() {
    }

    @Override
    public int getRawId(RegistryEntry<T> registryEntry) {
        return Registry.this.getRawId(registryEntry.value());
    }

    @Override
    public @Nullable RegistryEntry<T> get(int i) {
        return Registry.this.getEntry(i).orElse(null);
    }

    @Override
    public int size() {
        return Registry.this.size();
    }

    @Override
    public Iterator<RegistryEntry<T>> iterator() {
        return Registry.this.streamEntries().map(entry -> entry).iterator();
    }

    @Override
    public /* synthetic */ @Nullable Object get(int index) {
        return this.get(index);
    }
}
