/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import java.util.Optional;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.registry.tag.TagKey;

static abstract class RegistryBuilder.EntryListCreatingLookup<T>
implements RegistryEntryLookup<T> {
    protected final RegistryEntryOwner<T> entryOwner;

    protected RegistryBuilder.EntryListCreatingLookup(RegistryEntryOwner<T> entryOwner) {
        this.entryOwner = entryOwner;
    }

    @Override
    public Optional<RegistryEntryList.Named<T>> getOptional(TagKey<T> tag) {
        return Optional.of(RegistryEntryList.of(this.entryOwner, tag));
    }
}
