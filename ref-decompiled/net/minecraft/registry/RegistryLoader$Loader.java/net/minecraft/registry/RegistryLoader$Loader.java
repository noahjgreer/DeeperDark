/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Map;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.RegistryOps;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceManager;

static final class RegistryLoader.Loader<T>
extends Record {
    final RegistryLoader.Entry<T> data;
    final MutableRegistry<T> registry;
    private final Map<RegistryKey<?>, Exception> loadingErrors;

    RegistryLoader.Loader(RegistryLoader.Entry<T> data, MutableRegistry<T> registry, Map<RegistryKey<?>, Exception> loadingErrors) {
        this.data = data;
        this.registry = registry;
        this.loadingErrors = loadingErrors;
    }

    public void loadFromResource(ResourceManager resourceManager, RegistryOps.RegistryInfoGetter infoGetter) {
        RegistryLoader.loadFromResource(resourceManager, infoGetter, this.registry, this.data.elementCodec, this.loadingErrors);
    }

    public void loadFromNetwork(Map<RegistryKey<? extends Registry<?>>, RegistryLoader.ElementsAndTags> data, ResourceFactory factory, RegistryOps.RegistryInfoGetter infoGetter) {
        RegistryLoader.loadFromNetwork(data, factory, infoGetter, this.registry, this.data.elementCodec, this.loadingErrors);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{RegistryLoader.Loader.class, "data;registry;loadingErrors", "data", "registry", "loadingErrors"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RegistryLoader.Loader.class, "data;registry;loadingErrors", "data", "registry", "loadingErrors"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RegistryLoader.Loader.class, "data;registry;loadingErrors", "data", "registry", "loadingErrors"}, this, object);
    }

    public RegistryLoader.Entry<T> data() {
        return this.data;
    }

    public MutableRegistry<T> registry() {
        return this.registry;
    }

    public Map<RegistryKey<?>, Exception> loadingErrors() {
        return this.loadingErrors;
    }
}
