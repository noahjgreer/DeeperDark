/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Lifecycle
 */
package net.minecraft.registry;

import com.mojang.serialization.Lifecycle;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

record RegistryBuilder.RegistryInfo<T>(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle, RegistryBuilder.BootstrapFunction<T> bootstrap) {
    void runBootstrap(RegistryBuilder.Registries registries) {
        this.bootstrap.run(registries.createRegisterable());
    }

    public RegistryBuilder.InitializedRegistry<T> init(RegistryBuilder.Registries registries) {
        HashMap map = new HashMap();
        Iterator<Map.Entry<RegistryKey<?>, RegistryBuilder.RegisteredValue<?>>> iterator = registries.registeredValues.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<RegistryKey<?>, RegistryBuilder.RegisteredValue<?>> entry = iterator.next();
            RegistryKey<?> registryKey = entry.getKey();
            if (!registryKey.isOf(this.key)) continue;
            RegistryKey<?> registryKey2 = registryKey;
            RegistryBuilder.RegisteredValue<?> registeredValue = entry.getValue();
            RegistryEntry.Reference<Object> reference = registries.lookup.keysToEntries.remove(registryKey);
            map.put(registryKey2, new RegistryBuilder.EntryAssociatedValue(registeredValue, Optional.ofNullable(reference)));
            iterator.remove();
        }
        return new RegistryBuilder.InitializedRegistry(this.key, this.lifecycle, map);
    }
}
