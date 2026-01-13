/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.serialization.Lifecycle
 */
package net.minecraft.registry;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Lifecycle;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

static final class RegistryBuilder.Registries
extends Record {
    final RegistryBuilder.AnyOwner owner;
    final RegistryBuilder.StandAloneEntryCreatingLookup lookup;
    final Map<Identifier, RegistryEntryLookup<?>> registries;
    final Map<RegistryKey<?>, RegistryBuilder.RegisteredValue<?>> registeredValues;
    final List<RuntimeException> errors;

    private RegistryBuilder.Registries(RegistryBuilder.AnyOwner owner, RegistryBuilder.StandAloneEntryCreatingLookup lookup, Map<Identifier, RegistryEntryLookup<?>> registries, Map<RegistryKey<?>, RegistryBuilder.RegisteredValue<?>> registeredValues, List<RuntimeException> errors) {
        this.owner = owner;
        this.lookup = lookup;
        this.registries = registries;
        this.registeredValues = registeredValues;
        this.errors = errors;
    }

    public static RegistryBuilder.Registries of(DynamicRegistryManager dynamicRegistryManager, Stream<RegistryKey<? extends Registry<?>>> registryRefs) {
        RegistryBuilder.AnyOwner anyOwner = new RegistryBuilder.AnyOwner();
        ArrayList<RuntimeException> list = new ArrayList<RuntimeException>();
        RegistryBuilder.StandAloneEntryCreatingLookup standAloneEntryCreatingLookup = new RegistryBuilder.StandAloneEntryCreatingLookup(anyOwner);
        ImmutableMap.Builder builder = ImmutableMap.builder();
        dynamicRegistryManager.streamAllRegistries().forEach(entry -> builder.put((Object)entry.key().getValue(), RegistryBuilder.toLookup(entry.value())));
        registryRefs.forEach(registryRef -> builder.put((Object)registryRef.getValue(), (Object)standAloneEntryCreatingLookup));
        return new RegistryBuilder.Registries(anyOwner, standAloneEntryCreatingLookup, (Map<Identifier, RegistryEntryLookup<?>>)builder.build(), new HashMap(), (List<RuntimeException>)list);
    }

    public <T> Registerable<T> createRegisterable() {
        return new Registerable<T>(){

            @Override
            public RegistryEntry.Reference<T> register(RegistryKey<T> key, T value, Lifecycle lifecycle) {
                RegistryBuilder.RegisteredValue registeredValue = registeredValues.put(key, new RegistryBuilder.RegisteredValue(value, lifecycle));
                if (registeredValue != null) {
                    errors.add(new IllegalStateException("Duplicate registration for " + String.valueOf(key) + ", new=" + String.valueOf(value) + ", old=" + String.valueOf(registeredValue.value)));
                }
                return lookup.getOrCreate(key);
            }

            @Override
            public <S> RegistryEntryLookup<S> getRegistryLookup(RegistryKey<? extends Registry<? extends S>> registryRef) {
                return registries.getOrDefault(registryRef.getValue(), lookup);
            }
        };
    }

    public void checkOrphanedValues() {
        this.registeredValues.forEach((key, value) -> this.errors.add(new IllegalStateException("Orpaned value " + String.valueOf(value.value) + " for key " + String.valueOf(key))));
    }

    public void checkUnreferencedKeys() {
        for (RegistryKey<Object> registryKey : this.lookup.keysToEntries.keySet()) {
            this.errors.add(new IllegalStateException("Unreferenced key: " + String.valueOf(registryKey)));
        }
    }

    public void throwErrors() {
        if (!this.errors.isEmpty()) {
            IllegalStateException illegalStateException = new IllegalStateException("Errors during registry creation");
            for (RuntimeException runtimeException : this.errors) {
                illegalStateException.addSuppressed(runtimeException);
            }
            throw illegalStateException;
        }
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{RegistryBuilder.Registries.class, "owner;lookup;registries;registeredValues;errors", "owner", "lookup", "registries", "registeredValues", "errors"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RegistryBuilder.Registries.class, "owner;lookup;registries;registeredValues;errors", "owner", "lookup", "registries", "registeredValues", "errors"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RegistryBuilder.Registries.class, "owner;lookup;registries;registeredValues;errors", "owner", "lookup", "registries", "registeredValues", "errors"}, this, object);
    }

    public RegistryBuilder.AnyOwner owner() {
        return this.owner;
    }

    public RegistryBuilder.StandAloneEntryCreatingLookup lookup() {
        return this.lookup;
    }

    public Map<Identifier, RegistryEntryLookup<?>> registries() {
        return this.registries;
    }

    public Map<RegistryKey<?>, RegistryBuilder.RegisteredValue<?>> registeredValues() {
        return this.registeredValues;
    }

    public List<RuntimeException> errors() {
        return this.errors;
    }
}
