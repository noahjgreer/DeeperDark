/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public static final class AtlasManager.Metadata
extends Record {
    final Identifier textureId;
    final Identifier definitionId;
    final boolean createMipmaps;
    final Set<ResourceMetadataSerializer<?>> additionalMetadata;

    public AtlasManager.Metadata(Identifier textureId, Identifier definitionId, boolean createMipmaps) {
        this(textureId, definitionId, createMipmaps, Set.of());
    }

    public AtlasManager.Metadata(Identifier textureId, Identifier definitionId, boolean createMipmaps, Set<ResourceMetadataSerializer<?>> additionalMetadata) {
        this.textureId = textureId;
        this.definitionId = definitionId;
        this.createMipmaps = createMipmaps;
        this.additionalMetadata = additionalMetadata;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{AtlasManager.Metadata.class, "textureId;definitionLocation;createMipmaps;additionalMetadata", "textureId", "definitionId", "createMipmaps", "additionalMetadata"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{AtlasManager.Metadata.class, "textureId;definitionLocation;createMipmaps;additionalMetadata", "textureId", "definitionId", "createMipmaps", "additionalMetadata"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{AtlasManager.Metadata.class, "textureId;definitionLocation;createMipmaps;additionalMetadata", "textureId", "definitionId", "createMipmaps", "additionalMetadata"}, this, object);
    }

    public Identifier textureId() {
        return this.textureId;
    }

    public Identifier definitionId() {
        return this.definitionId;
    }

    public boolean createMipmaps() {
        return this.createMipmaps;
    }

    public Set<ResourceMetadataSerializer<?>> additionalMetadata() {
        return this.additionalMetadata;
    }
}
