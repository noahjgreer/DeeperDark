/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry.tag;

import java.util.Optional;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public static interface TagGroupLoader.EntrySupplier<T> {
    public Optional<? extends T> get(Identifier var1, boolean var2);

    public static <T> TagGroupLoader.EntrySupplier<? extends RegistryEntry<T>> forReload(Registry<T> registry) {
        return (id, required) -> registry.getEntry(id);
    }

    public static <T> TagGroupLoader.EntrySupplier<RegistryEntry<T>> forInitial(MutableRegistry<T> registry) {
        RegistryEntryLookup registryEntryLookup = registry.createMutableRegistryLookup();
        return (id, required) -> (required ? registryEntryLookup : registry).getOptional(RegistryKey.of(registry.getKey(), id));
    }
}
