/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.texture;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteDimensions;
import net.minecraft.resource.Resource;
import net.minecraft.resource.metadata.ResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public interface SpriteOpener {
    public static final Logger LOGGER = LogUtils.getLogger();

    public static SpriteOpener create(Set<ResourceMetadataSerializer<?>> additionalMetadata) {
        return (id, resource) -> {
            SpriteDimensions spriteDimensions;
            NativeImage nativeImage;
            List<ResourceMetadataSerializer.Value<?>> list;
            Optional<TextureResourceMetadata> optional2;
            Optional<AnimationResourceMetadata> optional;
            try {
                ResourceMetadata resourceMetadata = resource.getMetadata();
                optional = resourceMetadata.decode(AnimationResourceMetadata.SERIALIZER);
                optional2 = resourceMetadata.decode(TextureResourceMetadata.SERIALIZER);
                list = resourceMetadata.decode(additionalMetadata);
            }
            catch (Exception exception) {
                LOGGER.error("Unable to parse metadata from {}", (Object)id, (Object)exception);
                return null;
            }
            try (InputStream inputStream = resource.getInputStream();){
                nativeImage = NativeImage.read(inputStream);
            }
            catch (IOException iOException) {
                LOGGER.error("Using missing texture, unable to load {}", (Object)id, (Object)iOException);
                return null;
            }
            if (optional.isPresent()) {
                spriteDimensions = optional.get().getSize(nativeImage.getWidth(), nativeImage.getHeight());
                if (!MathHelper.isMultipleOf(nativeImage.getWidth(), spriteDimensions.width()) || !MathHelper.isMultipleOf(nativeImage.getHeight(), spriteDimensions.height())) {
                    LOGGER.error("Image {} size {},{} is not multiple of frame size {},{}", new Object[]{id, nativeImage.getWidth(), nativeImage.getHeight(), spriteDimensions.width(), spriteDimensions.height()});
                    nativeImage.close();
                    return null;
                }
            } else {
                spriteDimensions = new SpriteDimensions(nativeImage.getWidth(), nativeImage.getHeight());
            }
            return new SpriteContents(id, spriteDimensions, nativeImage, optional, list, optional2);
        };
    }

    public @Nullable SpriteContents loadSprite(Identifier var1, Resource var2);
}
