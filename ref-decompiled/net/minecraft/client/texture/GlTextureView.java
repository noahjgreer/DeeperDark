package net.minecraft.client.texture;

import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GlTextureView extends GpuTextureView {
   private boolean closed;

   protected GlTextureView(GlTexture texture, int baseMipLevel, int mipLevels) {
      super(texture, baseMipLevel, mipLevels);
      texture.incrementRefCount();
   }

   public boolean isClosed() {
      return this.closed;
   }

   public void close() {
      if (!this.closed) {
         this.closed = true;
         this.texture().decrementRefCount();
      }

   }

   public GlTexture texture() {
      return (GlTexture)super.texture();
   }

   // $FF: synthetic method
   public GpuTexture texture() {
      return this.texture();
   }
}
