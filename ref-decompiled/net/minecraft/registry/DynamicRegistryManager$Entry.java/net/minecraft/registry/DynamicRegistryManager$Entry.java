/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Map;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public static final class DynamicRegistryManager.Entry<T>
extends Record {
    final RegistryKey<? extends Registry<T>> key;
    private final Registry<T> value;

    public DynamicRegistryManager.Entry(RegistryKey<? extends Registry<T>> key, Registry<T> value) {
        this.key = key;
        this.value = value;
    }

    private static <T, R extends Registry<? extends T>> DynamicRegistryManager.Entry<T> of(Map.Entry<? extends RegistryKey<? extends Registry<?>>, R> entry) {
        return DynamicRegistryManager.Entry.of(entry.getKey(), (Registry)entry.getValue());
    }

    private static <T> DynamicRegistryManager.Entry<T> of(RegistryKey<? extends Registry<?>> key, Registry<?> value) {
        return new DynamicRegistryManager.Entry(key, value);
    }

    private DynamicRegistryManager.Entry<T> freeze() {
        return new DynamicRegistryManager.Entry<T>(this.key, this.value.freeze());
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{DynamicRegistryManager.Entry.class, "key;value", "key", "value"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{DynamicRegistryManager.Entry.class, "key;value", "key", "value"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{DynamicRegistryManager.Entry.class, "key;value", "key", "value"}, this, object);
    }

    public RegistryKey<? extends Registry<T>> key() {
        return this.key;
    }

    public Registry<T> value() {
        return this.value;
    }
}
