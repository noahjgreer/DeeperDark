/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.RegistryOps;

@FunctionalInterface
static interface RegistryLoader.RegistryLoadable {
    public void apply(RegistryLoader.Loader<?> var1, RegistryOps.RegistryInfoGetter var2);
}
