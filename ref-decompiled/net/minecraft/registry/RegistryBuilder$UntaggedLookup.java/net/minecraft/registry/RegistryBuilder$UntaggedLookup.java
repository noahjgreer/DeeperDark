/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import java.util.stream.Stream;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.entry.RegistryEntryOwner;

static abstract class RegistryBuilder.UntaggedLookup<T>
extends RegistryBuilder.EntryListCreatingLookup<T>
implements RegistryWrapper.Impl<T> {
    protected RegistryBuilder.UntaggedLookup(RegistryEntryOwner<T> registryEntryOwner) {
        super(registryEntryOwner);
    }

    @Override
    public Stream<RegistryEntryList.Named<T>> getTags() {
        throw new UnsupportedOperationException("Tags are not available in datagen");
    }
}
