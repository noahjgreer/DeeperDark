package net.minecraft.client.gl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.ClosableFactory;

@Environment(EnvType.CLIENT)
public record SimpleFramebufferFactory(int width, int height, boolean useDepth, int clearColor) implements ClosableFactory {
   public SimpleFramebufferFactory(int i, int j, boolean bl, int k) {
      this.width = i;
      this.height = j;
      this.useDepth = bl;
      this.clearColor = k;
   }

   public Framebuffer create() {
      return new SimpleFramebuffer((String)null, this.width, this.height, this.useDepth);
   }

   public void prepare(Framebuffer framebuffer) {
      if (this.useDepth) {
         RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(framebuffer.getColorAttachment(), this.clearColor, framebuffer.getDepthAttachment(), 1.0);
      } else {
         RenderSystem.getDevice().createCommandEncoder().clearColorTexture(framebuffer.getColorAttachment(), this.clearColor);
      }

   }

   public void close(Framebuffer framebuffer) {
      framebuffer.delete();
   }

   public boolean equals(ClosableFactory factory) {
      if (!(factory instanceof SimpleFramebufferFactory simpleFramebufferFactory)) {
         return false;
      } else {
         return this.width == simpleFramebufferFactory.width && this.height == simpleFramebufferFactory.height && this.useDepth == simpleFramebufferFactory.useDepth;
      }
   }

   public int width() {
      return this.width;
   }

   public int height() {
      return this.height;
   }

   public boolean useDepth() {
      return this.useDepth;
   }

   public int clearColor() {
      return this.clearColor;
   }

   // $FF: synthetic method
   public void close(final Object value) {
      this.close((Framebuffer)value);
   }

   // $FF: synthetic method
   public void prepare(final Object value) {
      this.prepare((Framebuffer)value);
   }

   // $FF: synthetic method
   public Object create() {
      return this.create();
   }
}
