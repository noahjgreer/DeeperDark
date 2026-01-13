/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Lifecycle
 */
package net.minecraft.registry;

import com.mojang.serialization.Lifecycle;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntryOwner;

public record RegistryOps.RegistryInfo<T>(RegistryEntryOwner<T> owner, RegistryEntryLookup<T> entryLookup, Lifecycle elementsLifecycle) {
    public static <T> RegistryOps.RegistryInfo<T> fromWrapper(RegistryWrapper.Impl<T> wrapper) {
        return new RegistryOps.RegistryInfo<T>(wrapper, wrapper, wrapper.getLifecycle());
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{RegistryOps.RegistryInfo.class, "owner;getter;elementsLifecycle", "owner", "entryLookup", "elementsLifecycle"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RegistryOps.RegistryInfo.class, "owner;getter;elementsLifecycle", "owner", "entryLookup", "elementsLifecycle"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RegistryOps.RegistryInfo.class, "owner;getter;elementsLifecycle", "owner", "entryLookup", "elementsLifecycle"}, this, object);
    }
}
