/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

static class DynamicRegistryManager.1
implements DynamicRegistryManager.Immutable {
    final /* synthetic */ Registry field_36470;

    DynamicRegistryManager.1(Registry registry) {
        this.field_36470 = registry;
    }

    public <T> Optional<Registry<T>> getOptional(RegistryKey<? extends Registry<? extends T>> registryRef) {
        Registry registry = this.field_36470;
        return registry.getOptionalValue(registryRef);
    }

    @Override
    public Stream<DynamicRegistryManager.Entry<?>> streamAllRegistries() {
        return this.field_36470.getEntrySet().stream().map(DynamicRegistryManager.Entry::of);
    }

    @Override
    public DynamicRegistryManager.Immutable toImmutable() {
        return this;
    }
}
