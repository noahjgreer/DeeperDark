/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Lifecycle
 */
package net.minecraft.registry;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;

public static interface RegistryWrapper.WrapperLookup
extends RegistryEntryLookup.RegistryLookup {
    public Stream<RegistryKey<? extends Registry<?>>> streamAllRegistryKeys();

    default public Stream<RegistryWrapper.Impl<?>> stream() {
        return this.streamAllRegistryKeys().map(registryRef -> this.getOrThrow((RegistryKey)registryRef));
    }

    public <T> Optional<? extends RegistryWrapper.Impl<T>> getOptional(RegistryKey<? extends Registry<? extends T>> var1);

    default public <T> RegistryWrapper.Impl<T> getOrThrow(RegistryKey<? extends Registry<? extends T>> registryRef) {
        return this.getOptional(registryRef).orElseThrow(() -> new IllegalStateException("Registry " + String.valueOf(registryRef.getValue()) + " not found"));
    }

    default public <V> RegistryOps<V> getOps(DynamicOps<V> delegate) {
        return RegistryOps.of(delegate, this);
    }

    public static RegistryWrapper.WrapperLookup of(Stream<RegistryWrapper.Impl<?>> wrappers) {
        final Map<RegistryKey, RegistryWrapper.Impl> map = wrappers.collect(Collectors.toUnmodifiableMap(RegistryWrapper.Impl::getKey, wrapper -> wrapper));
        return new RegistryWrapper.WrapperLookup(){

            @Override
            public Stream<RegistryKey<? extends Registry<?>>> streamAllRegistryKeys() {
                return map.keySet().stream();
            }

            public <T> Optional<RegistryWrapper.Impl<T>> getOptional(RegistryKey<? extends Registry<? extends T>> registryRef) {
                return Optional.ofNullable((RegistryWrapper.Impl)map.get(registryRef));
            }
        };
    }

    default public Lifecycle getLifecycle() {
        return this.stream().map(RegistryWrapper.Impl::getLifecycle).reduce(Lifecycle.stable(), Lifecycle::add);
    }
}
