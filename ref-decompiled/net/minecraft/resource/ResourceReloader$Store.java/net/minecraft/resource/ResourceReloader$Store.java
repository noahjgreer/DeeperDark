/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;

public static final class ResourceReloader.Store {
    private final ResourceManager resourceManager;
    private final Map<ResourceReloader.Key<?>, Object> store = new IdentityHashMap();

    public ResourceReloader.Store(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    public ResourceManager getResourceManager() {
        return this.resourceManager;
    }

    public <T> void put(ResourceReloader.Key<T> key, T value) {
        this.store.put(key, value);
    }

    public <T> T getOrThrow(ResourceReloader.Key<T> key) {
        return (T)Objects.requireNonNull(this.store.get(key));
    }
}
