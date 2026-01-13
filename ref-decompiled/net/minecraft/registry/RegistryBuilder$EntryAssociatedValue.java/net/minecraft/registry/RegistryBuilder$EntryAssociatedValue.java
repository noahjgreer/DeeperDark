/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.entry.RegistryEntry;

record RegistryBuilder.EntryAssociatedValue<T>(RegistryBuilder.RegisteredValue<T> value, Optional<RegistryEntry.Reference<T>> entry) {
    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{RegistryBuilder.EntryAssociatedValue.class, "value;holder", "value", "entry"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RegistryBuilder.EntryAssociatedValue.class, "value;holder", "value", "entry"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RegistryBuilder.EntryAssociatedValue.class, "value;holder", "value", "entry"}, this, object);
    }
}
