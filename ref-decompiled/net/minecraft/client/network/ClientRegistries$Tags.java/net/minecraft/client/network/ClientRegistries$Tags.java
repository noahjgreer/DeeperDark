/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagPacketSerializer;

@Environment(value=EnvType.CLIENT)
static class ClientRegistries.Tags {
    private final Map<RegistryKey<? extends Registry<?>>, TagPacketSerializer.Serialized> tags = new HashMap();

    ClientRegistries.Tags() {
    }

    public void put(RegistryKey<? extends Registry<?>> registryRef, TagPacketSerializer.Serialized tags) {
        this.tags.put(registryRef, tags);
    }

    public void forEach(BiConsumer<? super RegistryKey<? extends Registry<?>>, ? super TagPacketSerializer.Serialized> consumer) {
        this.tags.forEach(consumer);
    }
}
