/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Lifecycle
 */
package net.minecraft.registry;

import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;

public static interface RegistryWrapper.Impl.Delegating<T>
extends RegistryWrapper.Impl<T> {
    public RegistryWrapper.Impl<T> getBase();

    @Override
    default public RegistryKey<? extends Registry<? extends T>> getKey() {
        return this.getBase().getKey();
    }

    @Override
    default public Lifecycle getLifecycle() {
        return this.getBase().getLifecycle();
    }

    @Override
    default public Optional<RegistryEntry.Reference<T>> getOptional(RegistryKey<T> key) {
        return this.getBase().getOptional(key);
    }

    @Override
    default public Stream<RegistryEntry.Reference<T>> streamEntries() {
        return this.getBase().streamEntries();
    }

    @Override
    default public Optional<RegistryEntryList.Named<T>> getOptional(TagKey<T> tag) {
        return this.getBase().getOptional(tag);
    }

    @Override
    default public Stream<RegistryEntryList.Named<T>> getTags() {
        return this.getBase().getTags();
    }
}
