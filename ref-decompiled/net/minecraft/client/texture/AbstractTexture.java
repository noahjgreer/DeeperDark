package net.minecraft.client.texture;

import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public abstract class AbstractTexture implements AutoCloseable {
   @Nullable
   protected GpuTexture glTexture;
   @Nullable
   protected GpuTextureView glTextureView;

   public void setClamp(boolean clamp) {
      if (this.glTexture == null) {
         throw new IllegalStateException("Texture does not exist, can't change its clamp before something initializes it");
      } else {
         this.glTexture.setAddressMode(clamp ? AddressMode.CLAMP_TO_EDGE : AddressMode.REPEAT);
      }
   }

   public void setFilter(boolean bilinear, boolean mipmap) {
      if (this.glTexture == null) {
         throw new IllegalStateException("Texture does not exist, can't get change its filter before something initializes it");
      } else {
         this.glTexture.setTextureFilter(bilinear ? FilterMode.LINEAR : FilterMode.NEAREST, mipmap);
      }
   }

   public void setUseMipmaps(boolean useMipmaps) {
      if (this.glTexture == null) {
         throw new IllegalStateException("Texture does not exist, can't get change its filter before something initializes it");
      } else {
         this.glTexture.setUseMipmaps(useMipmaps);
      }
   }

   public void close() {
      if (this.glTexture != null) {
         this.glTexture.close();
         this.glTexture = null;
      }

      if (this.glTextureView != null) {
         this.glTextureView.close();
         this.glTextureView = null;
      }

   }

   public GpuTexture getGlTexture() {
      if (this.glTexture == null) {
         throw new IllegalStateException("Texture does not exist, can't get it before something initializes it");
      } else {
         return this.glTexture;
      }
   }

   public GpuTextureView getGlTextureView() {
      if (this.glTextureView == null) {
         throw new IllegalStateException("Texture view does not exist, can't get it before something initializes it");
      } else {
         return this.glTextureView;
      }
   }
}
