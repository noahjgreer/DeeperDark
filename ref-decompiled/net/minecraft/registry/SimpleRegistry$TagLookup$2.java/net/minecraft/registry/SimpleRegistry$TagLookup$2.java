/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;

static class SimpleRegistry.TagLookup.2
implements SimpleRegistry.TagLookup<T> {
    final /* synthetic */ Map field_53694;

    SimpleRegistry.TagLookup.2(Map map) {
        this.field_53694 = map;
    }

    @Override
    public boolean isBound() {
        return true;
    }

    @Override
    public Optional<RegistryEntryList.Named<T>> getOptional(TagKey<T> key) {
        return Optional.ofNullable((RegistryEntryList.Named)this.field_53694.get(key));
    }

    @Override
    public void forEach(BiConsumer<? super TagKey<T>, ? super RegistryEntryList.Named<T>> consumer) {
        this.field_53694.forEach(consumer);
    }

    @Override
    public Stream<RegistryEntryList.Named<T>> stream() {
        return this.field_53694.values().stream();
    }
}
