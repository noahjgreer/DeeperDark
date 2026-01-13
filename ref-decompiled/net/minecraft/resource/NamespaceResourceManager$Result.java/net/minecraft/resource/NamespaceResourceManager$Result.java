/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource;

import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;

static final class NamespaceResourceManager.Result
extends Record {
    final ResourcePack pack;
    final InputSupplier<InputStream> supplier;
    final int packIndex;

    NamespaceResourceManager.Result(ResourcePack pack, InputSupplier<InputStream> supplier, int packIndex) {
        this.pack = pack;
        this.supplier = supplier;
        this.packIndex = packIndex;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{NamespaceResourceManager.Result.class, "packResources;resource;packIndex", "pack", "supplier", "packIndex"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{NamespaceResourceManager.Result.class, "packResources;resource;packIndex", "pack", "supplier", "packIndex"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{NamespaceResourceManager.Result.class, "packResources;resource;packIndex", "pack", "supplier", "packIndex"}, this, object);
    }

    public ResourcePack pack() {
        return this.pack;
    }

    public InputSupplier<InputStream> supplier() {
        return this.supplier;
    }

    public int packIndex() {
        return this.packIndex;
    }
}
