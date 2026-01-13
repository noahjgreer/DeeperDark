/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import java.util.Optional;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.registry.tag.TagKey;

class ContextSwappableRegistryLookup.EntryLookupImpl
implements RegistryEntryLookup<Object>,
RegistryEntryOwner<Object> {
    ContextSwappableRegistryLookup.EntryLookupImpl() {
    }

    @Override
    public Optional<RegistryEntry.Reference<Object>> getOptional(RegistryKey<Object> key) {
        return Optional.of(this.getOrComputeEntry(key));
    }

    @Override
    public RegistryEntry.Reference<Object> getOrThrow(RegistryKey<Object> key) {
        return this.getOrComputeEntry(key);
    }

    private RegistryEntry.Reference<Object> getOrComputeEntry(RegistryKey<Object> key) {
        return ContextSwappableRegistryLookup.this.entries.computeIfAbsent(key, key2 -> RegistryEntry.Reference.standAlone(this, key2));
    }

    @Override
    public Optional<RegistryEntryList.Named<Object>> getOptional(TagKey<Object> tag) {
        return Optional.of(this.getOrComputeTag(tag));
    }

    @Override
    public RegistryEntryList.Named<Object> getOrThrow(TagKey<Object> tag) {
        return this.getOrComputeTag(tag);
    }

    private RegistryEntryList.Named<Object> getOrComputeTag(TagKey<Object> tag) {
        return ContextSwappableRegistryLookup.this.tags.computeIfAbsent(tag, tagKey -> RegistryEntryList.of(this, tagKey));
    }

    public <T> RegistryEntryLookup<T> asEntryLookup() {
        return this;
    }

    public <T> RegistryEntryOwner<T> asEntryOwner() {
        return this;
    }
}
