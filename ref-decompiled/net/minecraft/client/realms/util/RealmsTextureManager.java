/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.realms.util.RealmsTextureManager
 *  net.minecraft.client.realms.util.RealmsTextureManager$RealmsTexture
 *  net.minecraft.client.texture.AbstractTexture
 *  net.minecraft.client.texture.MissingSprite
 *  net.minecraft.client.texture.NativeImage
 *  net.minecraft.client.texture.NativeImageBackedTexture
 *  net.minecraft.util.Identifier
 *  org.jspecify.annotations.Nullable
 *  org.lwjgl.system.MemoryUtil
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.util;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.realms.util.RealmsTextureManager;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class RealmsTextureManager {
    private static final Map<String, RealmsTexture> TEXTURES = Maps.newHashMap();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Identifier ISLES = Identifier.ofVanilla((String)"textures/gui/presets/isles.png");

    public static Identifier getTextureId(String id, @Nullable String image) {
        if (image == null) {
            return ISLES;
        }
        return RealmsTextureManager.getTextureIdInternal((String)id, (String)image);
    }

    private static Identifier getTextureIdInternal(String id, String image) {
        RealmsTexture realmsTexture = (RealmsTexture)TEXTURES.get(id);
        if (realmsTexture != null && realmsTexture.image().equals(image)) {
            return realmsTexture.textureId;
        }
        NativeImage nativeImage = RealmsTextureManager.loadImage((String)image);
        if (nativeImage == null) {
            Identifier identifier = MissingSprite.getMissingSpriteId();
            TEXTURES.put(id, new RealmsTexture(image, identifier));
            return identifier;
        }
        Identifier identifier = Identifier.of((String)"realms", (String)("dynamic/" + id));
        MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, (AbstractTexture)new NativeImageBackedTexture(() -> ((Identifier)identifier).toString(), nativeImage));
        TEXTURES.put(id, new RealmsTexture(image, identifier));
        return identifier;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static @Nullable NativeImage loadImage(String image) {
        byte[] bs = Base64.getDecoder().decode(image);
        ByteBuffer byteBuffer = MemoryUtil.memAlloc((int)bs.length);
        try {
            NativeImage nativeImage = NativeImage.read((ByteBuffer)byteBuffer.put(bs).flip());
            return nativeImage;
        }
        catch (IOException iOException) {
            LOGGER.warn("Failed to load world image: {}", (Object)image, (Object)iOException);
        }
        finally {
            MemoryUtil.memFree((Buffer)byteBuffer);
        }
        return null;
    }
}

