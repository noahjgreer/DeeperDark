/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.registry.DynamicRegistryManager;

public static final class SaveLoading.LoadContext<D>
extends Record {
    final D extraData;
    final DynamicRegistryManager.Immutable dimensionsRegistryManager;

    public SaveLoading.LoadContext(D extraData, DynamicRegistryManager.Immutable dimensionsRegistryManager) {
        this.extraData = extraData;
        this.dimensionsRegistryManager = dimensionsRegistryManager;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{SaveLoading.LoadContext.class, "cookie;finalDimensions", "extraData", "dimensionsRegistryManager"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SaveLoading.LoadContext.class, "cookie;finalDimensions", "extraData", "dimensionsRegistryManager"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SaveLoading.LoadContext.class, "cookie;finalDimensions", "extraData", "dimensionsRegistryManager"}, this, object);
    }

    public D extraData() {
        return this.extraData;
    }

    public DynamicRegistryManager.Immutable dimensionsRegistryManager() {
        return this.dimensionsRegistryManager;
    }
}
