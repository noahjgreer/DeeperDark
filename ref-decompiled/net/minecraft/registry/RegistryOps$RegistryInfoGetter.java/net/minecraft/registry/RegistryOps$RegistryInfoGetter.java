/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import java.util.Optional;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryOps;

public static interface RegistryOps.RegistryInfoGetter {
    public <T> Optional<RegistryOps.RegistryInfo<T>> getRegistryInfo(RegistryKey<? extends Registry<? extends T>> var1);
}
