/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureSet;

public static final class ItemGroup.DisplayContext
extends Record {
    final FeatureSet enabledFeatures;
    private final boolean hasPermissions;
    private final RegistryWrapper.WrapperLookup lookup;

    public ItemGroup.DisplayContext(FeatureSet enabledFeatures, boolean hasPermissions, RegistryWrapper.WrapperLookup lookup) {
        this.enabledFeatures = enabledFeatures;
        this.hasPermissions = hasPermissions;
        this.lookup = lookup;
    }

    public boolean doesNotMatch(FeatureSet enabledFeatures, boolean hasPermissions, RegistryWrapper.WrapperLookup registries) {
        return !this.enabledFeatures.equals(enabledFeatures) || this.hasPermissions != hasPermissions || this.lookup != registries;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ItemGroup.DisplayContext.class, "enabledFeatures;hasPermissions;holders", "enabledFeatures", "hasPermissions", "lookup"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ItemGroup.DisplayContext.class, "enabledFeatures;hasPermissions;holders", "enabledFeatures", "hasPermissions", "lookup"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ItemGroup.DisplayContext.class, "enabledFeatures;hasPermissions;holders", "enabledFeatures", "hasPermissions", "lookup"}, this, object);
    }

    public FeatureSet enabledFeatures() {
        return this.enabledFeatures;
    }

    public boolean hasPermissions() {
        return this.hasPermissions;
    }

    public RegistryWrapper.WrapperLookup lookup() {
        return this.lookup;
    }
}
