/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.util.SpriteIdentifier;

@Environment(value=EnvType.CLIENT)
public static final class ModelTextures.Textures
extends Record {
    final Map<String, ModelTextures.Entry> values;
    public static final ModelTextures.Textures EMPTY = new ModelTextures.Textures(Map.of());

    public ModelTextures.Textures(Map<String, ModelTextures.Entry> values) {
        this.values = values;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ModelTextures.Textures.class, "values", "values"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ModelTextures.Textures.class, "values", "values"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ModelTextures.Textures.class, "values", "values"}, this, object);
    }

    public Map<String, ModelTextures.Entry> values() {
        return this.values;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Builder {
        private final Map<String, ModelTextures.Entry> entries = new HashMap<String, ModelTextures.Entry>();

        public Builder addTextureReference(String textureId, String target) {
            this.entries.put(textureId, new ModelTextures.TextureReferenceEntry(target));
            return this;
        }

        public Builder addSprite(String textureId, SpriteIdentifier spriteId) {
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
}
