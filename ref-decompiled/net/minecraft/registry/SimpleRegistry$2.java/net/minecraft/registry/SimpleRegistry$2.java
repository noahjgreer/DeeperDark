/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.registry;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;

class SimpleRegistry.2
implements RegistryWrapper.Impl.Delegating<T> {
    final /* synthetic */ ImmutableMap field_54028;

    SimpleRegistry.2(ImmutableMap immutableMap) {
        this.field_54028 = immutableMap;
    }

    @Override
    public RegistryWrapper.Impl<T> getBase() {
        return SimpleRegistry.this;
    }

    @Override
    public Optional<RegistryEntryList.Named<T>> getOptional(TagKey<T> tag) {
        return Optional.ofNullable((RegistryEntryList.Named)this.field_54028.get(tag));
    }

    @Override
    public Stream<RegistryEntryList.Named<T>> getTags() {
        return this.field_54028.values().stream();
    }
}
