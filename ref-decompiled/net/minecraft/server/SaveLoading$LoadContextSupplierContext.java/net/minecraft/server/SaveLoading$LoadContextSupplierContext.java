/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.ResourceManager;

public record SaveLoading.LoadContextSupplierContext(ResourceManager resourceManager, DataConfiguration dataConfiguration, RegistryWrapper.WrapperLookup worldGenRegistryManager, DynamicRegistryManager.Immutable dimensionsRegistryManager) {
    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{SaveLoading.LoadContextSupplierContext.class, "resources;dataConfiguration;datapackWorldgen;datapackDimensions", "resourceManager", "dataConfiguration", "worldGenRegistryManager", "dimensionsRegistryManager"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SaveLoading.LoadContextSupplierContext.class, "resources;dataConfiguration;datapackWorldgen;datapackDimensions", "resourceManager", "dataConfiguration", "worldGenRegistryManager", "dimensionsRegistryManager"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SaveLoading.LoadContextSupplierContext.class, "resources;dataConfiguration;datapackWorldgen;datapackDimensions", "resourceManager", "dataConfiguration", "worldGenRegistryManager", "dimensionsRegistryManager"}, this, object);
    }
}
