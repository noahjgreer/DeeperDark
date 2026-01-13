/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server;

import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.server.DataPackContents;

@FunctionalInterface
public static interface SaveLoading.SaveApplierFactory<D, R> {
    public R create(LifecycledResourceManager var1, DataPackContents var2, CombinedDynamicRegistries<ServerDynamicRegistryType> var3, D var4);
}
