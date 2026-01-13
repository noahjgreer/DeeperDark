/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry.entry;

import java.util.List;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.registry.tag.TagKey;

static class RegistryEntryList.1
extends RegistryEntryList.Named<T> {
    RegistryEntryList.1(RegistryEntryOwner registryEntryOwner, TagKey tagKey) {
        super(registryEntryOwner, tagKey);
    }

    @Override
    protected List<RegistryEntry<T>> getEntries() {
        throw new UnsupportedOperationException("Tag " + String.valueOf(this.getTag()) + " can't be dereferenced during construction");
    }
}
