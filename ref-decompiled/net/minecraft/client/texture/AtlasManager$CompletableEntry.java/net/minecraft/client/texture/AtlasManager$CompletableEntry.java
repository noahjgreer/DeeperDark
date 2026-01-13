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
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.AtlasManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteLoader;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
static final class AtlasManager.CompletableEntry
extends Record {
    final AtlasManager.Entry entry;
    final CompletableFuture<SpriteLoader.StitchResult> preparations;

    AtlasManager.CompletableEntry(AtlasManager.Entry entry, CompletableFuture<SpriteLoader.StitchResult> preparations) {
        this.entry = entry;
        this.preparations = preparations;
    }

    public void fillSpriteMap(Map<SpriteIdentifier, Sprite> sprites) {
        SpriteLoader.StitchResult stitchResult = this.preparations.join();
        this.entry.atlas.create(stitchResult);
        stitchResult.sprites().forEach((id, sprite) -> sprites.put(new SpriteIdentifier(this.entry.metadata.textureId, (Identifier)id), (Sprite)sprite));
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{AtlasManager.CompletableEntry.class, "entry;preparations", "entry", "preparations"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{AtlasManager.CompletableEntry.class, "entry;preparations", "entry", "preparations"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{AtlasManager.CompletableEntry.class, "entry;preparations", "entry", "preparations"}, this, object);
    }

    public AtlasManager.Entry entry() {
        return this.entry;
    }

    public CompletableFuture<SpriteLoader.StitchResult> preparations() {
        return this.preparations;
    }
}
