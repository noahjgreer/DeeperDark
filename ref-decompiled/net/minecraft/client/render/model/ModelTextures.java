/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.model.BakedModelManager
 *  net.minecraft.client.render.model.ModelTextures
 *  net.minecraft.client.render.model.ModelTextures$Textures
 *  net.minecraft.client.render.model.ModelTextures$Textures$Builder
 *  net.minecraft.client.util.SpriteIdentifier
 *  net.minecraft.util.Identifier
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ModelTextures {
    public static final ModelTextures EMPTY = new ModelTextures(Map.of());
    private static final char TEXTURE_REFERENCE_PREFIX = '#';
    private final Map<String, SpriteIdentifier> textures;

    ModelTextures(Map<String, SpriteIdentifier> textures) {
        this.textures = textures;
    }

    public @Nullable SpriteIdentifier get(String textureId) {
        if (ModelTextures.isTextureReference((String)textureId)) {
            textureId = textureId.substring(1);
        }
        return (SpriteIdentifier)this.textures.get(textureId);
    }

    private static boolean isTextureReference(String textureId) {
        return textureId.charAt(0) == '#';
    }

    public static Textures fromJson(JsonObject json) {
        Textures.Builder builder = new Textures.Builder();
        for (Map.Entry entry : json.entrySet()) {
            ModelTextures.add((String)((String)entry.getKey()), (String)((JsonElement)entry.getValue()).getAsString(), (Textures.Builder)builder);
        }
        return builder.build();
    }

    private static void add(String textureId, String spriteId, Textures.Builder builder) {
        if (ModelTextures.isTextureReference((String)spriteId)) {
            builder.addTextureReference(textureId, spriteId.substring(1));
        } else {
            Identifier identifier = Identifier.tryParse((String)spriteId);
            if (identifier == null) {
                throw new JsonParseException(spriteId + " is not valid resource location");
            }
            builder.addSprite(textureId, new SpriteIdentifier(BakedModelManager.BLOCK_OR_ITEM, identifier));
        }
    }
}

