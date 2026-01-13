/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;

static interface SimpleRegistry.TagLookup<T> {
    public static <T> SimpleRegistry.TagLookup<T> ofUnbound() {
        return new SimpleRegistry.TagLookup<T>(){

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
        };
    }

    public static <T> SimpleRegistry.TagLookup<T> fromMap(final Map<TagKey<T>, RegistryEntryList.Named<T>> map) {
        return new SimpleRegistry.TagLookup<T>(){

            @Override
            public boolean isBound() {
                return true;
            }

            @Override
            public Optional<RegistryEntryList.Named<T>> getOptional(TagKey<T> key) {
                return Optional.ofNullable((RegistryEntryList.Named)map.get(key));
            }

            @Override
            public void forEach(BiConsumer<? super TagKey<T>, ? super RegistryEntryList.Named<T>> consumer) {
                map.forEach(consumer);
            }

            @Override
            public Stream<RegistryEntryList.Named<T>> stream() {
                return map.values().stream();
            }
        };
    }

    public boolean isBound();

    public Optional<RegistryEntryList.Named<T>> getOptional(TagKey<T> var1);

    public void forEach(BiConsumer<? super TagKey<T>, ? super RegistryEntryList.Named<T>> var1);

    public Stream<RegistryEntryList.Named<T>> stream();
}
