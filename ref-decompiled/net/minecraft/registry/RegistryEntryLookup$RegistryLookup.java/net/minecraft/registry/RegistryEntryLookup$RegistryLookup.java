/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import java.util.Optional;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

public static interface RegistryEntryLookup.RegistryLookup {
    public <T> Optional<? extends RegistryEntryLookup<T>> getOptional(RegistryKey<? extends Registry<? extends T>> var1);

    default public <T> RegistryEntryLookup<T> getOrThrow(RegistryKey<? extends Registry<? extends T>> registryRef) {
        return this.getOptional(registryRef).orElseThrow(() -> new IllegalStateException("Registry " + String.valueOf(registryRef.getValue()) + " not found"));
    }

    default public <T> Optional<RegistryEntry.Reference<T>> getOptionalEntry(RegistryKey<T> registryRef) {
        return this.getOptional(registryRef.getRegistryRef()).flatMap(registryEntryLookup -> registryEntryLookup.getOptional(registryRef));
    }

    default public <T> RegistryEntry.Reference<T> getEntryOrThrow(RegistryKey<T> key) {
        return (RegistryEntry.Reference)this.getOptional(key.getRegistryRef()).flatMap(registry -> registry.getOptional(key)).orElseThrow(() -> new IllegalStateException("Missing element " + String.valueOf(key)));
    }
}
