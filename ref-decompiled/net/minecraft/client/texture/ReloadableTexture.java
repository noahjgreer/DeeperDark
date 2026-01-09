package net.minecraft.client.texture;

import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.TextureFormat;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class ReloadableTexture extends AbstractTexture {
   private final Identifier textureId;

   public ReloadableTexture(Identifier textureId) {
      this.textureId = textureId;
   }

   public Identifier getId() {
      return this.textureId;
   }

   public void reload(TextureContents contents) {
      boolean bl = contents.clamp();
      boolean bl2 = contents.blur();
      NativeImage nativeImage = contents.image();

      try {
         this.load(nativeImage, bl2, bl);
      } catch (Throwable var8) {
         if (nativeImage != null) {
            try {
               nativeImage.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (nativeImage != null) {
         nativeImage.close();
      }

   }

   protected void load(NativeImage image, boolean blur, boolean clamp) {
      GpuDevice gpuDevice = RenderSystem.getDevice();
      this.close();
      Identifier var10002 = this.textureId;
      Objects.requireNonNull(var10002);
      this.glTexture = gpuDevice.createTexture((Supplier)(var10002::toString), 5, TextureFormat.RGBA8, image.getWidth(), image.getHeight(), 1, 1);
      this.glTextureView = gpuDevice.createTextureView(this.glTexture);
      this.setFilter(blur, false);
      this.setClamp(clamp);
      gpuDevice.createCommandEncoder().writeToTexture(this.glTexture, image);
   }

   public abstract TextureContents loadContents(ResourceManager resourceManager) throws IOException;
}
