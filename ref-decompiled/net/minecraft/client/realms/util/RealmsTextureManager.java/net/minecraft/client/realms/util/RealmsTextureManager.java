/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 *  org.lwjgl.system.MemoryUtil
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.util;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsTextureManager {
    private static final Map<String, RealmsTexture> TEXTURES = Maps.newHashMap();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Identifier ISLES = Identifier.ofVanilla("textures/gui/presets/isles.png");

    public static Identifier getTextureId(String id, @Nullable String image) {
        if (image == null) {
            return ISLES;
        }
        return RealmsTextureManager.getTextureIdInternal(id, image);
    }

    private static Identifier getTextureIdInternal(String id, String image) {
        RealmsTexture realmsTexture = TEXTURES.get(id);
        if (realmsTexture != null && realmsTexture.image().equals(image)) {
            return realmsTexture.textureId;
        }
        NativeImage nativeImage = RealmsTextureManager.loadImage(image);
        if (nativeImage == null) {
            Identifier identifier = MissingSprite.getMissingSpriteId();
            TEXTURES.put(id, new RealmsTexture(image, identifier));
            return identifier;
        }
        Identifier identifier = Identifier.of("realms", "dynamic/" + id);
        MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, new NativeImageBackedTexture(identifier::toString, nativeImage));
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
            NativeImage nativeImage = NativeImage.read(byteBuffer.put(bs).flip());
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

    @Environment(value=EnvType.CLIENT)
    public static final class RealmsTexture
    extends Record {
        private final String image;
        final Identifier textureId;

        public RealmsTexture(String image, Identifier textureId) {
            this.image = image;
            this.textureId = textureId;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{RealmsTexture.class, "image;textureId", "image", "textureId"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RealmsTexture.class, "image;textureId", "image", "textureId"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RealmsTexture.class, "image;textureId", "image", "textureId"}, this, object);
        }

        public String image() {
            return this.image;
        }

        public Identifier textureId() {
            return this.textureId;
        }
    }
}
