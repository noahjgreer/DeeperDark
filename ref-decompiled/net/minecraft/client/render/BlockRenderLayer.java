package net.minecraft.client.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;

@Environment(EnvType.CLIENT)
public enum BlockRenderLayer {
   SOLID(RenderPipelines.SOLID, 4194304, true, false),
   CUTOUT_MIPPED(RenderPipelines.CUTOUT_MIPPED, 4194304, true, false),
   CUTOUT(RenderPipelines.CUTOUT, 786432, false, false),
   TRANSLUCENT(RenderPipelines.TRANSLUCENT, 786432, true, true),
   TRIPWIRE(RenderPipelines.TRIPWIRE, 1536, true, true);

   private final RenderPipeline pipeline;
   private final int size;
   private final boolean mipmap;
   private final boolean translucent;
   private final String name;

   private BlockRenderLayer(final RenderPipeline pipeline, final int size, final boolean mipmap, final boolean translucent) {
      this.pipeline = pipeline;
      this.size = size;
      this.mipmap = mipmap;
      this.translucent = translucent;
      this.name = this.toString().toLowerCase(Locale.ROOT);
   }

   public RenderPipeline getPipeline() {
      return this.pipeline;
   }

   public int getBufferSize() {
      return this.size;
   }

   public String getName() {
      return this.name;
   }

   public boolean isTranslucent() {
      return this.translucent;
   }

   public GpuTextureView getTextureView() {
      TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
      AbstractTexture abstractTexture = textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
      abstractTexture.setUseMipmaps(this.mipmap);
      return abstractTexture.getGlTextureView();
   }

   public Framebuffer getFramebuffer() {
      MinecraftClient minecraftClient = MinecraftClient.getInstance();
      Framebuffer framebuffer;
      switch (this.ordinal()) {
         case 3:
            framebuffer = minecraftClient.worldRenderer.getTranslucentFramebuffer();
            return framebuffer != null ? framebuffer : minecraftClient.getFramebuffer();
         case 4:
            framebuffer = minecraftClient.worldRenderer.getWeatherFramebuffer();
            return framebuffer != null ? framebuffer : minecraftClient.getFramebuffer();
         default:
            return minecraftClient.getFramebuffer();
      }
   }

   // $FF: synthetic method
   private static BlockRenderLayer[] method_72026() {
      return new BlockRenderLayer[]{SOLID, CUTOUT_MIPPED, CUTOUT, TRANSLUCENT, TRIPWIRE};
   }
}
