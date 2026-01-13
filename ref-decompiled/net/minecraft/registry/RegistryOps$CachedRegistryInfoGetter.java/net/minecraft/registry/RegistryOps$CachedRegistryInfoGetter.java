/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;

static final class RegistryOps.CachedRegistryInfoGetter
implements RegistryOps.RegistryInfoGetter {
    private final RegistryWrapper.WrapperLookup registries;
    private final Map<RegistryKey<? extends Registry<?>>, Optional<? extends RegistryOps.RegistryInfo<?>>> cache = new ConcurrentHashMap();

    public RegistryOps.CachedRegistryInfoGetter(RegistryWrapper.WrapperLookup registries) {
        this.registries = registries;
    }

    public <E> Optional<RegistryOps.RegistryInfo<E>> getRegistryInfo(RegistryKey<? extends Registry<? extends E>> registryRef) {
        return this.cache.computeIfAbsent(registryRef, this::compute);
    }

    private Optional<RegistryOps.RegistryInfo<Object>> compute(RegistryKey<? extends Registry<?>> registryRef) {
        return this.registries.getOptional(registryRef).map(RegistryOps.RegistryInfo::fromWrapper);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RegistryOps.CachedRegistryInfoGetter)) return false;
        RegistryOps.CachedRegistryInfoGetter cachedRegistryInfoGetter = (RegistryOps.CachedRegistryInfoGetter)o;
        if (!this.registries.equals(cachedRegistryInfoGetter.registries)) return false;
        return true;
    }

    public int hashCode() {
        return this.registries.hashCode();
    }
}
