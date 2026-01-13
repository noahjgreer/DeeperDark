/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.util.SpriteIdentifier;

@Environment(value=EnvType.CLIENT)
public static class ModelTextures.Textures.Builder {
    private final Map<String, ModelTextures.Entry> entries = new HashMap<String, ModelTextures.Entry>();

    public ModelTextures.Textures.Builder addTextureReference(String textureId, String target) {
        this.entries.put(textureId, new ModelTextures.TextureReferenceEntry(target));
        return this;
    }

    public ModelTextures.Textures.Builder addSprite(String textureId, SpriteIdentifier spriteId) {
        this.entries.put(textureId, new ModelTextures.SpriteEntry(spriteId));
        return this;
    }

    public ModelTextures.Textures build() {
        if (this.entries.isEmpty()) {
            return EMPTY;
        }
        return new ModelTextures.Textures(Map.copyOf(this.entries));
    }
}
