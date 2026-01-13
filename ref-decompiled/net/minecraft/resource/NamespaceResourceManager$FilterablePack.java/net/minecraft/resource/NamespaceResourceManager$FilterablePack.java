/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.resource;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Collection;
import java.util.function.Predicate;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

static final class NamespaceResourceManager.FilterablePack
extends Record {
    final String name;
    final @Nullable ResourcePack underlying;
    private final @Nullable Predicate<Identifier> filter;

    NamespaceResourceManager.FilterablePack(String name, @Nullable ResourcePack underlying, @Nullable Predicate<Identifier> filter) {
        this.name = name;
        this.underlying = underlying;
        this.filter = filter;
    }

    public void removeFiltered(Collection<Identifier> ids) {
        if (this.filter != null) {
            ids.removeIf(this.filter);
        }
    }

    public boolean isFiltered(Identifier id) {
        return this.filter != null && this.filter.test(id);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{NamespaceResourceManager.FilterablePack.class, "name;resources;filter", "name", "underlying", "filter"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{NamespaceResourceManager.FilterablePack.class, "name;resources;filter", "name", "underlying", "filter"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{NamespaceResourceManager.FilterablePack.class, "name;resources;filter", "name", "underlying", "filter"}, this, object);
    }

    public String name() {
        return this.name;
    }

    public @Nullable ResourcePack underlying() {
        return this.underlying;
    }

    public @Nullable Predicate<Identifier> filter() {
        return this.filter;
    }
}
