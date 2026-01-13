/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;

static class SimpleRegistry.TagLookup.1
implements SimpleRegistry.TagLookup<T> {
    SimpleRegistry.TagLookup.1() {
    }

    @Override
    public boolean isBound() {
        return false;
    }

    @Override
    public Optional<RegistryEntryList.Named<T>> getOptional(TagKey<T> key) {
        throw new IllegalStateException("Tags not bound, trying to access " + String.valueOf(key));
    }

    @Override
    public void forEach(BiConsumer<? super TagKey<T>, ? super RegistryEntryList.Named<T>> consumer) {
        throw new IllegalStateException("Tags not bound");
    }

    @Override
    public Stream<RegistryEntryList.Named<T>> stream() {
        throw new IllegalStateException("Tags not bound");
    }
}
