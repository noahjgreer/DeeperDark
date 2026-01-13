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
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;

static final class RegistryBuilder.InitializedRegistry<T>
extends Record {
    final RegistryKey<? extends Registry<? extends T>> key;
    private final Lifecycle lifecycle;
    private final Map<RegistryKey<T>, RegistryBuilder.EntryAssociatedValue<T>> values;

    RegistryBuilder.InitializedRegistry(RegistryKey<? extends Registry<? extends T>> key, Lifecycle lifecycle, Map<RegistryKey<T>, RegistryBuilder.EntryAssociatedValue<T>> values) {
        this.key = key;
        this.lifecycle = lifecycle;
        this.values = values;
    }

    public RegistryWrapper.Impl<T> toWrapper(RegistryBuilder.AnyOwner anyOwner) {
        Map map = this.values.entrySet().stream().collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, entry -> {
            RegistryBuilder.EntryAssociatedValue entryAssociatedValue = (RegistryBuilder.EntryAssociatedValue)entry.getValue();
            RegistryEntry.Reference reference = entryAssociatedValue.entry().orElseGet(() -> RegistryEntry.Reference.standAlone(anyOwner.downcast(), (RegistryKey)entry.getKey()));
            reference.setValue(entryAssociatedValue.value().value());
            return reference;
        }));
        return RegistryBuilder.createWrapper(this.key, this.lifecycle, anyOwner.downcast(), map);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{RegistryBuilder.InitializedRegistry.class, "key;lifecycle;values", "key", "lifecycle", "values"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RegistryBuilder.InitializedRegistry.class, "key;lifecycle;values", "key", "lifecycle", "values"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RegistryBuilder.InitializedRegistry.class, "key;lifecycle;values", "key", "lifecycle", "values"}, this, object);
    }

    public RegistryKey<? extends Registry<? extends T>> key() {
        return this.key;
    }

    public Lifecycle lifecycle() {
        return this.lifecycle;
    }

    public Map<RegistryKey<T>, RegistryBuilder.EntryAssociatedValue<T>> values() {
        return this.values;
    }
}
