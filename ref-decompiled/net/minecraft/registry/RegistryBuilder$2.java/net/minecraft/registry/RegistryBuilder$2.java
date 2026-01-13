/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Lifecycle
 */
package net.minecraft.registry;

import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryOwner;

static class RegistryBuilder.2
extends RegistryBuilder.UntaggedLookup<T> {
    final /* synthetic */ RegistryKey field_47488;
    final /* synthetic */ Lifecycle field_47489;
    final /* synthetic */ Map keysToEntries;

    RegistryBuilder.2(RegistryEntryOwner registryEntryOwner, RegistryKey registryKey, Lifecycle lifecycle, Map map) {
        this.field_47488 = registryKey;
        this.field_47489 = lifecycle;
        this.keysToEntries = map;
        super(registryEntryOwner);
    }

    @Override
    public RegistryKey<? extends Registry<? extends T>> getKey() {
        return this.field_47488;
    }

    @Override
    public Lifecycle getLifecycle() {
        return this.field_47489;
    }

    @Override
    public Optional<RegistryEntry.Reference<T>> getOptional(RegistryKey<T> key) {
        return Optional.ofNullable((RegistryEntry.Reference)this.keysToEntries.get(key));
    }

    @Override
    public Stream<RegistryEntry.Reference<T>> streamEntries() {
        return this.keysToEntries.values().stream();
    }
}
