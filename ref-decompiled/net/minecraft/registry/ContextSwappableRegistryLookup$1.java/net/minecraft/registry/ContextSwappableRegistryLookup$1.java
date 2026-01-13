/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Lifecycle
 */
package net.minecraft.registry;

import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryOps;

class ContextSwappableRegistryLookup.1
implements RegistryOps.RegistryInfoGetter {
    ContextSwappableRegistryLookup.1() {
    }

    @Override
    public <T> Optional<RegistryOps.RegistryInfo<T>> getRegistryInfo(RegistryKey<? extends Registry<? extends T>> registryRef) {
        return ContextSwappableRegistryLookup.this.delegate.getOptional(registryRef).map(RegistryOps.RegistryInfo::fromWrapper).or(() -> Optional.of(new RegistryOps.RegistryInfo(ContextSwappableRegistryLookup.this.entryLookupImpl.asEntryOwner(), ContextSwappableRegistryLookup.this.entryLookupImpl.asEntryLookup(), Lifecycle.experimental())));
    }
}
