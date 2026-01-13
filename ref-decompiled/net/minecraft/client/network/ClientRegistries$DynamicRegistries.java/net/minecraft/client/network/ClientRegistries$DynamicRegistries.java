/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SerializableRegistries;

@Environment(value=EnvType.CLIENT)
static class ClientRegistries.DynamicRegistries {
    final Map<RegistryKey<? extends Registry<?>>, List<SerializableRegistries.SerializedRegistryEntry>> dynamicRegistries = new HashMap();

    ClientRegistries.DynamicRegistries() {
    }

    public void put(RegistryKey<? extends Registry<?>> registryRef, List<SerializableRegistries.SerializedRegistryEntry> entries) {
        this.dynamicRegistries.computeIfAbsent(registryRef, registries -> new ArrayList()).addAll(entries);
    }
}
