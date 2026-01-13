/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry.tag;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.Map;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;

public static final class TagGroupLoader.RegistryTags<T>
extends Record {
    private final RegistryKey<? extends Registry<T>> key;
    final Map<TagKey<T>, List<RegistryEntry<T>>> tags;

    public TagGroupLoader.RegistryTags(RegistryKey<? extends Registry<T>> key, Map<TagKey<T>, List<RegistryEntry<T>>> tags) {
        this.key = key;
        this.tags = tags;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{TagGroupLoader.RegistryTags.class, "key;tags", "key", "tags"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{TagGroupLoader.RegistryTags.class, "key;tags", "key", "tags"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{TagGroupLoader.RegistryTags.class, "key;tags", "key", "tags"}, this, object);
    }

    public RegistryKey<? extends Registry<T>> key() {
        return this.key;
    }

    public Map<TagKey<T>, List<RegistryEntry<T>>> tags() {
        return this.tags;
    }
}
