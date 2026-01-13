/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;

public static class ReloadableRegistries.Lookup {
    private final RegistryWrapper.WrapperLookup registries;

    public ReloadableRegistries.Lookup(RegistryWrapper.WrapperLookup registries) {
        this.registries = registries;
    }

    public RegistryWrapper.WrapperLookup createRegistryLookup() {
        return this.registries;
    }

    public LootTable getLootTable(RegistryKey<LootTable> key) {
        return this.registries.getOptional(RegistryKeys.LOOT_TABLE).flatMap(registryEntryLookup -> registryEntryLookup.getOptional(key)).map(RegistryEntry::value).orElse(LootTable.EMPTY);
    }
}
