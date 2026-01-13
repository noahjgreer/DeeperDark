/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.registry;

import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryCloner;
import net.minecraft.registry.RegistryKey;
import org.jspecify.annotations.Nullable;

public static class RegistryCloner.CloneableRegistries {
    private final Map<RegistryKey<? extends Registry<?>>, RegistryCloner<?>> registries = new HashMap();

    public <T> RegistryCloner.CloneableRegistries add(RegistryKey<? extends Registry<? extends T>> registryRef, Codec<T> elementCodec) {
        this.registries.put(registryRef, new RegistryCloner<T>(elementCodec));
        return this;
    }

    public <T> @Nullable RegistryCloner<T> get(RegistryKey<? extends Registry<? extends T>> registryRef) {
        return this.registries.get(registryRef);
    }
}
