/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry.tag;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.resource.DependencyTracker;
import net.minecraft.util.Identifier;

static final class TagGroupLoader.TagDependencies
extends Record
implements DependencyTracker.Dependencies<Identifier> {
    final List<TagGroupLoader.TrackedEntry> entries;

    TagGroupLoader.TagDependencies(List<TagGroupLoader.TrackedEntry> entries) {
        this.entries = entries;
    }

    @Override
    public void forDependencies(Consumer<Identifier> callback) {
        this.entries.forEach(entry -> entry.entry.forEachRequiredTagId(callback));
    }

    @Override
    public void forOptionalDependencies(Consumer<Identifier> callback) {
        this.entries.forEach(entry -> entry.entry.forEachOptionalTagId(callback));
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{TagGroupLoader.TagDependencies.class, "entries", "entries"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{TagGroupLoader.TagDependencies.class, "entries", "entries"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{TagGroupLoader.TagDependencies.class, "entries", "entries"}, this, object);
    }

    public List<TagGroupLoader.TrackedEntry> entries() {
        return this.entries;
    }
}
