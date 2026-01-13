/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import java.util.Map;
import java.util.Optional;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryOps;

static class RegistryLoader.1
implements RegistryOps.RegistryInfoGetter {
    final /* synthetic */ Map field_40851;

    RegistryLoader.1(Map map) {
        this.field_40851 = map;
    }

    @Override
    public <T> Optional<RegistryOps.RegistryInfo<T>> getRegistryInfo(RegistryKey<? extends Registry<? extends T>> registryRef) {
        return Optional.ofNullable((RegistryOps.RegistryInfo)this.field_40851.get(registryRef));
    }
}
