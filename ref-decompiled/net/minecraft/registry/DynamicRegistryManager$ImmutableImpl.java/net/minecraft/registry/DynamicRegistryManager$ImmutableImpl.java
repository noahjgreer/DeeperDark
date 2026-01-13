/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.registry;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public static class DynamicRegistryManager.ImmutableImpl
implements DynamicRegistryManager {
    private final Map<? extends RegistryKey<? extends Registry<?>>, ? extends Registry<?>> registries;

    public DynamicRegistryManager.ImmutableImpl(List<? extends Registry<?>> registries) {
        this.registries = registries.stream().collect(Collectors.toUnmodifiableMap(Registry::getKey, registry -> registry));
    }

    public DynamicRegistryManager.ImmutableImpl(Map<? extends RegistryKey<? extends Registry<?>>, ? extends Registry<?>> registries) {
        this.registries = Map.copyOf(registries);
    }

    public DynamicRegistryManager.ImmutableImpl(Stream<DynamicRegistryManager.Entry<?>> entryStream) {
        this.registries = (Map)entryStream.collect(ImmutableMap.toImmutableMap(DynamicRegistryManager.Entry::key, DynamicRegistryManager.Entry::value));
    }

    @Override
    public <E> Optional<Registry<E>> getOptional(RegistryKey<? extends Registry<? extends E>> registryRef) {
        return Optional.ofNullable(this.registries.get(registryRef)).map(registry -> registry);
    }

    @Override
    public Stream<DynamicRegistryManager.Entry<?>> streamAllRegistries() {
        return this.registries.entrySet().stream().map(DynamicRegistryManager.Entry::of);
    }
}
