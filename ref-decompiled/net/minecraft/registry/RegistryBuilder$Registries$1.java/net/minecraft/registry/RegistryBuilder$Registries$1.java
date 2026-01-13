/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Lifecycle
 */
package net.minecraft.registry;

import com.mojang.serialization.Lifecycle;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

class RegistryBuilder.Registries.1
implements Registerable<T> {
    RegistryBuilder.Registries.1() {
    }

    @Override
    public RegistryEntry.Reference<T> register(RegistryKey<T> key, T value, Lifecycle lifecycle) {
        RegistryBuilder.RegisteredValue registeredValue = Registries.this.registeredValues.put(key, new RegistryBuilder.RegisteredValue(value, lifecycle));
        if (registeredValue != null) {
            Registries.this.errors.add(new IllegalStateException("Duplicate registration for " + String.valueOf(key) + ", new=" + String.valueOf(value) + ", old=" + String.valueOf(registeredValue.value)));
        }
        return Registries.this.lookup.getOrCreate(key);
    }

    @Override
    public <S> RegistryEntryLookup<S> getRegistryLookup(RegistryKey<? extends Registry<? extends S>> registryRef) {
        return Registries.this.registries.getOrDefault(registryRef.getValue(), Registries.this.lookup);
    }
}
