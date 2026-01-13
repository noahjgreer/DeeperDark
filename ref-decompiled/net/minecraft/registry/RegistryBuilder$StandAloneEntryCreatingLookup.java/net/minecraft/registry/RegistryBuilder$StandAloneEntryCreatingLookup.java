/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryOwner;

static class RegistryBuilder.StandAloneEntryCreatingLookup
extends RegistryBuilder.EntryListCreatingLookup<Object> {
    final Map<RegistryKey<Object>, RegistryEntry.Reference<Object>> keysToEntries = new HashMap<RegistryKey<Object>, RegistryEntry.Reference<Object>>();

    public RegistryBuilder.StandAloneEntryCreatingLookup(RegistryEntryOwner<Object> registryEntryOwner) {
        super(registryEntryOwner);
    }

    @Override
    public Optional<RegistryEntry.Reference<Object>> getOptional(RegistryKey<Object> key) {
        return Optional.of(this.getOrCreate(key));
    }

    <T> RegistryEntry.Reference<T> getOrCreate(RegistryKey<T> key) {
        return this.keysToEntries.computeIfAbsent(key, key2 -> RegistryEntry.Reference.standAlone(this.entryOwner, key2));
    }
}
