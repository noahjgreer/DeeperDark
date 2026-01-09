package net.minecraft.client.realms.util;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsTextureManager {
   private static final Map TEXTURES = Maps.newHashMap();
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Identifier ISLES = Identifier.ofVanilla("textures/gui/presets/isles.png");

   public static Identifier getTextureId(String id, @Nullable String image) {
      return image == null ? ISLES : getTextureIdInternal(id, image);
   }

   private static Identifier getTextureIdInternal(String id, String image) {
      RealmsTexture realmsTexture = (RealmsTexture)TEXTURES.get(id);
      if (realmsTexture != null && realmsTexture.image().equals(image)) {
         return realmsTexture.textureId;
      } else {
         NativeImage nativeImage = loadImage(image);
         Identifier identifier;
         if (nativeImage == null) {
            identifier = MissingSprite.getMissingSpriteId();
            TEXTURES.put(id, new RealmsTexture(image, identifier));
            return identifier;
         } else {
            identifier = Identifier.of("realms", "dynamic/" + id);
            TextureManager var10000 = MinecraftClient.getInstance().getTextureManager();
            Objects.requireNonNull(identifier);
            var10000.registerTexture(identifier, (AbstractTexture)(new NativeImageBackedTexture(identifier::toString, nativeImage)));
            TEXTURES.put(id, new RealmsTexture(image, identifier));
            return identifier;
         }
      }
   }

   @Nullable
   private static NativeImage loadImage(String image) {
      byte[] bs = Base64.getDecoder().decode(image);
      ByteBuffer byteBuffer = MemoryUtil.memAlloc(bs.length);

      try {
         NativeImage var3 = NativeImage.read(byteBuffer.put(bs).flip());
         return var3;
      } catch (IOException var7) {
         LOGGER.warn("Failed to load world image: {}", image, var7);
      } finally {
         MemoryUtil.memFree(byteBuffer);
      }

      return null;
   }

   @Environment(EnvType.CLIENT)
   public static record RealmsTexture(String image, Identifier textureId) {
      final Identifier textureId;

      public RealmsTexture(String image, Identifier identifier) {
         this.image = image;
         this.textureId = identifier;
      }

      public String image() {
         return this.image;
      }

      public Identifier textureId() {
         return this.textureId;
      }
   }
}
