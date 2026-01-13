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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.AtlasManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.SpriteLoader;
import net.minecraft.resource.ResourceManager;

@Environment(value=EnvType.CLIENT)
static final class AtlasManager.Entry
extends Record
implements AutoCloseable {
    final SpriteAtlasTexture atlas;
    final AtlasManager.Metadata metadata;

    AtlasManager.Entry(SpriteAtlasTexture atlas, AtlasManager.Metadata metadata) {
        this.atlas = atlas;
        this.metadata = metadata;
    }

    @Override
    public void close() {
        this.atlas.clear();
    }

    CompletableFuture<SpriteLoader.StitchResult> load(ResourceManager manager, Executor executor, int mipLevel) {
        return SpriteLoader.fromAtlas(this.atlas).load(manager, this.metadata.definitionId, this.metadata.createMipmaps ? mipLevel : 0, executor, this.metadata.additionalMetadata);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{AtlasManager.Entry.class, "atlas;config", "atlas", "metadata"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{AtlasManager.Entry.class, "atlas;config", "atlas", "metadata"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{AtlasManager.Entry.class, "atlas;config", "atlas", "metadata"}, this, object);
    }

    public SpriteAtlasTexture atlas() {
        return this.atlas;
    }

    public AtlasManager.Metadata metadata() {
        return this.metadata;
    }
}
