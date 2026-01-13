/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Lifecycle
 */
package net.minecraft.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.SimpleRegistry;

public static final class RegistryLoader.Entry<T>
extends Record {
    private final RegistryKey<? extends Registry<T>> key;
    final Codec<T> elementCodec;
    final boolean requiredNonEmpty;

    RegistryLoader.Entry(RegistryKey<? extends Registry<T>> key, Codec<T> codec) {
        this(key, codec, false);
    }

    public RegistryLoader.Entry(RegistryKey<? extends Registry<T>> key, Codec<T> elementCodec, boolean requiredNonEmpty) {
        this.key = key;
        this.elementCodec = elementCodec;
        this.requiredNonEmpty = requiredNonEmpty;
    }

    RegistryLoader.Loader<T> getLoader(Lifecycle lifecycle, Map<RegistryKey<?>, Exception> errors) {
        SimpleRegistry mutableRegistry = new SimpleRegistry(this.key, lifecycle);
        return new RegistryLoader.Loader(this, mutableRegistry, errors);
    }

    public void addToCloner(BiConsumer<RegistryKey<? extends Registry<T>>, Codec<T>> callback) {
        callback.accept(this.key, this.elementCodec);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{RegistryLoader.Entry.class, "key;elementCodec;requiredNonEmpty", "key", "elementCodec", "requiredNonEmpty"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RegistryLoader.Entry.class, "key;elementCodec;requiredNonEmpty", "key", "elementCodec", "requiredNonEmpty"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RegistryLoader.Entry.class, "key;elementCodec;requiredNonEmpty", "key", "elementCodec", "requiredNonEmpty"}, this, object);
    }

    public RegistryKey<? extends Registry<T>> key() {
        return this.key;
    }

    public Codec<T> elementCodec() {
        return this.elementCodec;
    }

    public boolean requiredNonEmpty() {
        return this.requiredNonEmpty;
    }
}
