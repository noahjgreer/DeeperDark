/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;

static class RegistryWrapper.WrapperLookup.1
implements RegistryWrapper.WrapperLookup {
    final /* synthetic */ Map field_54027;

    RegistryWrapper.WrapperLookup.1(Map map) {
        this.field_54027 = map;
    }

    @Override
    public Stream<RegistryKey<? extends Registry<?>>> streamAllRegistryKeys() {
        return this.field_54027.keySet().stream();
    }

    public <T> Optional<RegistryWrapper.Impl<T>> getOptional(RegistryKey<? extends Registry<? extends T>> registryRef) {
        return Optional.ofNullable((RegistryWrapper.Impl)this.field_54027.get(registryRef));
    }
}
