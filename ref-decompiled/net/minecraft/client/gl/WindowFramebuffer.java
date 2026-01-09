package net.minecraft.client.gl;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.TextureAllocationException;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class WindowFramebuffer extends Framebuffer {
   public static final int DEFAULT_WIDTH = 854;
   public static final int DEFAULT_HEIGHT = 480;
   static final Size DEFAULT = new Size(854, 480);

   public WindowFramebuffer(int width, int height) {
      super("Main", true);
      this.init(width, height);
   }

   private void init(int width, int height) {
      Size size = this.findSuitableSize(width, height);
      if (this.colorAttachment != null && this.depthAttachment != null) {
         this.colorAttachment.setTextureFilter(FilterMode.NEAREST, false);
         this.colorAttachment.setAddressMode(AddressMode.CLAMP_TO_EDGE);
         this.colorAttachment.setTextureFilter(FilterMode.NEAREST, false);
         this.colorAttachment.setAddressMode(AddressMode.CLAMP_TO_EDGE);
         this.viewportWidth = size.width;
         this.viewportHeight = size.height;
         this.textureWidth = size.width;
         this.textureHeight = size.height;
      } else {
         throw new IllegalStateException("Missing color and/or depth textures");
      }
   }

   private Size findSuitableSize(int width, int height) {
      RenderSystem.assertOnRenderThread();
      Iterator var3 = WindowFramebuffer.Size.findCompatible(width, height).iterator();

      Size size;
      do {
         if (!var3.hasNext()) {
            String var10002 = this.colorAttachment == null ? "missing color" : "have color";
            throw new RuntimeException("Unrecoverable GL_OUT_OF_MEMORY (" + var10002 + ", " + (this.depthAttachment == null ? "missing depth" : "have depth") + ")");
         }

         size = (Size)var3.next();
         if (this.colorAttachment != null) {
            this.colorAttachment.close();
            this.colorAttachment = null;
         }

         if (this.colorAttachmentView != null) {
            this.colorAttachmentView.close();
            this.colorAttachmentView = null;
         }

         if (this.depthAttachment != null) {
            this.depthAttachment.close();
            this.depthAttachment = null;
         }

         if (this.depthAttachmentView != null) {
            this.depthAttachmentView.close();
            this.depthAttachmentView = null;
         }

         this.colorAttachment = this.createColorAttachment(size);
         this.depthAttachment = this.createDepthAttachment(size);
      } while(this.colorAttachment == null || this.depthAttachment == null);

      this.colorAttachmentView = RenderSystem.getDevice().createTextureView(this.colorAttachment);
      this.depthAttachmentView = RenderSystem.getDevice().createTextureView(this.depthAttachment);
      return size;
   }

   @Nullable
   private GpuTexture createColorAttachment(Size size) {
      try {
         return RenderSystem.getDevice().createTexture((Supplier)(() -> {
            return this.name + " / Color";
         }), 15, TextureFormat.RGBA8, size.width, size.height, 1, 1);
      } catch (TextureAllocationException var3) {
         return null;
      }
   }

   @Nullable
   private GpuTexture createDepthAttachment(Size size) {
      try {
         return RenderSystem.getDevice().createTexture((Supplier)(() -> {
            return this.name + " / Depth";
         }), 15, TextureFormat.DEPTH32, size.width, size.height, 1, 1);
      } catch (TextureAllocationException var3) {
         return null;
      }
   }

   @Environment(EnvType.CLIENT)
   static class Size {
      public final int width;
      public final int height;

      Size(int width, int height) {
         this.width = width;
         this.height = height;
      }

      static List findCompatible(int width, int height) {
         RenderSystem.assertOnRenderThread();
         int i = RenderSystem.getDevice().getMaxTextureSize();
         return width > 0 && width <= i && height > 0 && height <= i ? ImmutableList.of(new Size(width, height), WindowFramebuffer.DEFAULT) : ImmutableList.of(WindowFramebuffer.DEFAULT);
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            Size size = (Size)o;
            return this.width == size.width && this.height == size.height;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.width, this.height});
      }

      public String toString() {
         return this.width + "x" + this.height;
      }
   }
}
