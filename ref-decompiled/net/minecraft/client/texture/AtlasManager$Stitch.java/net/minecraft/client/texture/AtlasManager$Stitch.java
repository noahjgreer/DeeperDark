/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.AtlasManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteLoader;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public static class AtlasManager.Stitch {
    final List<AtlasManager.CompletableEntry> entries;
    private final Map<Identifier, CompletableFuture<SpriteLoader.StitchResult>> preparations;
    final CompletableFuture<?> readyForUpload;

    AtlasManager.Stitch(List<AtlasManager.CompletableEntry> entries, Map<Identifier, CompletableFuture<SpriteLoader.StitchResult>> preparations, CompletableFuture<?> readyForUpload) {
        this.entries = entries;
        this.preparations = preparations;
        this.readyForUpload = readyForUpload;
    }

    public Map<SpriteIdentifier, Sprite> createSpriteMap() {
        HashMap<SpriteIdentifier, Sprite> map = new HashMap<SpriteIdentifier, Sprite>();
        this.entries.forEach(entry -> entry.fillSpriteMap(map));
        return map;
    }

    public CompletableFuture<SpriteLoader.StitchResult> getPreparations(Identifier atlasTextureId) {
        return Objects.requireNonNull(this.preparations.get(atlasTextureId));
    }
}
