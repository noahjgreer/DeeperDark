/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data.tag;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.data.tag.TagProvider;
import net.minecraft.registry.RegistryWrapper;

static final class TagProvider.RegistryInfo<T>
extends Record {
    final RegistryWrapper.WrapperLookup contents;
    final TagProvider.TagLookup<T> parent;

    TagProvider.RegistryInfo(RegistryWrapper.WrapperLookup contents, TagProvider.TagLookup<T> parent) {
        this.contents = contents;
        this.parent = parent;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{TagProvider.RegistryInfo.class, "contents;parent", "contents", "parent"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{TagProvider.RegistryInfo.class, "contents;parent", "contents", "parent"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{TagProvider.RegistryInfo.class, "contents;parent", "contents", "parent"}, this, object);
    }

    public RegistryWrapper.WrapperLookup contents() {
        return this.contents;
    }

    public TagProvider.TagLookup<T> parent() {
        return this.parent;
    }
}
