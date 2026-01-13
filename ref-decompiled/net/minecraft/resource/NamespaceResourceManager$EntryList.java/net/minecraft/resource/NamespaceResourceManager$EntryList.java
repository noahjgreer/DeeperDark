/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 */
package net.minecraft.resource;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;

static final class NamespaceResourceManager.EntryList
extends Record {
    final Identifier id;
    private final Identifier metadataId;
    final List<NamespaceResourceManager.FileSource> fileSources;
    final Map<ResourcePack, InputSupplier<InputStream>> metaSources;

    NamespaceResourceManager.EntryList(Identifier id) {
        this(id, NamespaceResourceManager.getMetadataPath(id), new ArrayList<NamespaceResourceManager.FileSource>(), (Map<ResourcePack, InputSupplier<InputStream>>)new Object2ObjectArrayMap());
    }

    private NamespaceResourceManager.EntryList(Identifier id, Identifier metadataId, List<NamespaceResourceManager.FileSource> fileSources, Map<ResourcePack, InputSupplier<InputStream>> metaSources) {
        this.id = id;
        this.metadataId = metadataId;
        this.fileSources = fileSources;
        this.metaSources = metaSources;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{NamespaceResourceManager.EntryList.class, "fileLocation;metadataLocation;fileSources;metaSources", "id", "metadataId", "fileSources", "metaSources"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{NamespaceResourceManager.EntryList.class, "fileLocation;metadataLocation;fileSources;metaSources", "id", "metadataId", "fileSources", "metaSources"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{NamespaceResourceManager.EntryList.class, "fileLocation;metadataLocation;fileSources;metaSources", "id", "metadataId", "fileSources", "metaSources"}, this, object);
    }

    public Identifier id() {
        return this.id;
    }

    public Identifier metadataId() {
        return this.metadataId;
    }

    public List<NamespaceResourceManager.FileSource> fileSources() {
        return this.fileSources;
    }

    public Map<ResourcePack, InputSupplier<InputStream>> metaSources() {
        return this.metaSources;
    }
}
