/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import java.util.Optional;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;

class SimpleRegistry.1
implements RegistryEntryLookup<T> {
    SimpleRegistry.1() {
    }

    @Override
    public Optional<RegistryEntry.Reference<T>> getOptional(RegistryKey<T> key) {
        return Optional.of(this.getOrThrow(key));
    }

    @Override
    public RegistryEntry.Reference<T> getOrThrow(RegistryKey<T> key) {
        return SimpleRegistry.this.getOrCreateEntry(key);
    }

    @Override
    public Optional<RegistryEntryList.Named<T>> getOptional(TagKey<T> tag) {
        return Optional.of(this.getOrThrow(tag));
    }

    @Override
    public RegistryEntryList.Named<T> getOrThrow(TagKey<T> tag) {
        return SimpleRegistry.this.getTag(tag);
    }
}
