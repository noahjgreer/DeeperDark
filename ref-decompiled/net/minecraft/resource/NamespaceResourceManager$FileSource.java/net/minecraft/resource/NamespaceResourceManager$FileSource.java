/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource;

import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;

static final class NamespaceResourceManager.FileSource
extends Record {
    final ResourcePack sourcePack;
    final InputSupplier<InputStream> supplier;

    NamespaceResourceManager.FileSource(ResourcePack sourcePack, InputSupplier<InputStream> supplier) {
        this.sourcePack = sourcePack;
        this.supplier = supplier;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{NamespaceResourceManager.FileSource.class, "source;resource", "sourcePack", "supplier"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{NamespaceResourceManager.FileSource.class, "source;resource", "sourcePack", "supplier"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{NamespaceResourceManager.FileSource.class, "source;resource", "sourcePack", "supplier"}, this, object);
    }

    public ResourcePack sourcePack() {
        return this.sourcePack;
    }

    public InputSupplier<InputStream> supplier() {
        return this.supplier;
    }
}
