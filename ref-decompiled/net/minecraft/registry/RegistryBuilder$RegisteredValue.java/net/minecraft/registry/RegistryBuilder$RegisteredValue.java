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

static final class RegistryBuilder.RegisteredValue<T>
extends Record {
    final T value;
    private final Lifecycle lifecycle;

    RegistryBuilder.RegisteredValue(T value, Lifecycle lifecycle) {
        this.value = value;
        this.lifecycle = lifecycle;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{RegistryBuilder.RegisteredValue.class, "value;lifecycle", "value", "lifecycle"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RegistryBuilder.RegisteredValue.class, "value;lifecycle", "value", "lifecycle"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RegistryBuilder.RegisteredValue.class, "value;lifecycle", "value", "lifecycle"}, this, object);
    }

    public T value() {
        return this.value;
    }

    public Lifecycle lifecycle() {
        return this.lifecycle;
    }
}
