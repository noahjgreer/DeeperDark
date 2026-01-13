/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import java.util.Optional;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryOwner;

static class RegistryBuilder.1
extends RegistryBuilder.EntryListCreatingLookup<T> {
    final /* synthetic */ RegistryWrapper.Impl field_40942;

    RegistryBuilder.1(RegistryEntryOwner registryEntryOwner, RegistryWrapper.Impl impl) {
        this.field_40942 = impl;
        super(registryEntryOwner);
    }

    @Override
    public Optional<RegistryEntry.Reference<T>> getOptional(RegistryKey<T> key) {
        return this.field_40942.getOptional(key);
    }
}
