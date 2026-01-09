package net.minecraft.client.gl;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.textures.TextureFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public sealed interface GlUniform extends AutoCloseable {
   default void close() {
   }

   @Environment(EnvType.CLIENT)
   public static record Sampler(int location, int samplerIndex) implements GlUniform {
      public Sampler(int i, int j) {
         this.location = i;
         this.samplerIndex = j;
      }

      public int location() {
         return this.location;
      }

      public int samplerIndex() {
         return this.samplerIndex;
      }
   }

   @Environment(EnvType.CLIENT)
   public static record TexelBuffer(int location, int samplerIndex, TextureFormat format, int texture) implements GlUniform {
      public TexelBuffer(int location, int samplerIndex, TextureFormat format) {
         this(location, samplerIndex, format, GlStateManager._genTexture());
      }

      public TexelBuffer(int i, int j, TextureFormat textureFormat, int k) {
         this.location = i;
         this.samplerIndex = j;
         this.format = textureFormat;
         this.texture = k;
      }

      public void close() {
         GlStateManager._deleteTexture(this.texture);
      }

      public int location() {
         return this.location;
      }

      public int samplerIndex() {
         return this.samplerIndex;
      }

      public TextureFormat format() {
         return this.format;
      }

      public int texture() {
         return this.texture;
      }
   }

   @Environment(EnvType.CLIENT)
   public static record UniformBuffer(int blockBinding) implements GlUniform {
      public UniformBuffer(int i) {
         this.blockBinding = i;
      }

      public int blockBinding() {
         return this.blockBinding;
      }
   }
}
