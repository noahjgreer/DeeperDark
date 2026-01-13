/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;

public static interface Registry.PendingTagLoad<T> {
    public RegistryKey<? extends Registry<? extends T>> getKey();

    public RegistryWrapper.Impl<T> getLookup();

    public void apply();

    public int size();
}
