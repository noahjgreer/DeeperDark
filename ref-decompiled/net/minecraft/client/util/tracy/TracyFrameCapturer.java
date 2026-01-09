package net.minecraft.client.util.tracy;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.jtracy.TracyClient;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;

@Environment(EnvType.CLIENT)
public class TracyFrameCapturer implements AutoCloseable {
   private static final int MAX_WIDTH = 320;
   private static final int MAX_HEIGHT = 180;
   private static final int field_54254 = 4;
   private int framebufferWidth;
   private int framebufferHeight;
   private int width;
   private int height;
   private GpuTexture texture;
   private GpuTextureView textureView;
   private GpuBuffer buffer;
   private int offset;
   private boolean captured;
   private Status status;

   public TracyFrameCapturer() {
      this.status = TracyFrameCapturer.Status.WAITING_FOR_CAPTURE;
      this.width = 320;
      this.height = 180;
      GpuDevice gpuDevice = RenderSystem.getDevice();
      this.texture = gpuDevice.createTexture((String)"Tracy Frame Capture", 10, TextureFormat.RGBA8, this.width, this.height, 1, 1);
      this.textureView = gpuDevice.createTextureView(this.texture);
      this.buffer = gpuDevice.createBuffer(() -> {
         return "Tracy Frame Capture buffer";
      }, 9, this.width * this.height * 4);
   }

   private void resize(int framebufferWidth, int framebufferHeight) {
      float f = (float)framebufferWidth / (float)framebufferHeight;
      if (framebufferWidth > 320) {
         framebufferWidth = 320;
         framebufferHeight = (int)(320.0F / f);
      }

      if (framebufferHeight > 180) {
         framebufferWidth = (int)(180.0F * f);
         framebufferHeight = 180;
      }

      framebufferWidth = framebufferWidth / 4 * 4;
      framebufferHeight = framebufferHeight / 4 * 4;
      if (this.width != framebufferWidth || this.height != framebufferHeight) {
         this.width = framebufferWidth;
         this.height = framebufferHeight;
         GpuDevice gpuDevice = RenderSystem.getDevice();
         this.texture.close();
         this.texture = gpuDevice.createTexture((String)"Tracy Frame Capture", 10, TextureFormat.RGBA8, framebufferWidth, framebufferHeight, 1, 1);
         this.textureView.close();
         this.textureView = gpuDevice.createTextureView(this.texture);
         this.buffer.close();
         this.buffer = gpuDevice.createBuffer(() -> {
            return "Tracy Frame Capture buffer";
         }, 9, framebufferWidth * framebufferHeight * 4);
      }

   }

   public void capture(Framebuffer framebuffer) {
      if (this.status == TracyFrameCapturer.Status.WAITING_FOR_CAPTURE && !this.captured && framebuffer.getColorAttachment() != null) {
         this.captured = true;
         if (framebuffer.textureWidth != this.framebufferWidth || framebuffer.textureHeight != this.framebufferHeight) {
            this.framebufferWidth = framebuffer.textureWidth;
            this.framebufferHeight = framebuffer.textureHeight;
            this.resize(this.framebufferWidth, this.framebufferHeight);
         }

         this.status = TracyFrameCapturer.Status.WAITING_FOR_COPY;
         CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
         RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS);
         GpuBuffer gpuBuffer = shapeIndexBuffer.getIndexBuffer(6);
         RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> {
            return "Tracy blit";
         }, this.textureView, OptionalInt.empty());

         try {
            renderPass.setPipeline(RenderPipelines.TRACY_BLIT);
            renderPass.setVertexBuffer(0, RenderSystem.getQuadVertexBuffer());
            renderPass.setIndexBuffer(gpuBuffer, shapeIndexBuffer.getIndexType());
            renderPass.bindSampler("InSampler", framebuffer.getColorAttachmentView());
            renderPass.drawIndexed(0, 0, 6, 1);
         } catch (Throwable var9) {
            if (renderPass != null) {
               try {
                  renderPass.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (renderPass != null) {
            renderPass.close();
         }

         commandEncoder.copyTextureToBuffer(this.texture, this.buffer, 0, () -> {
            this.status = TracyFrameCapturer.Status.WAITING_FOR_UPLOAD;
         }, 0);
         this.offset = 0;
      }
   }

   public void upload() {
      if (this.status == TracyFrameCapturer.Status.WAITING_FOR_UPLOAD) {
         this.status = TracyFrameCapturer.Status.WAITING_FOR_CAPTURE;
         GpuBuffer.MappedView mappedView = RenderSystem.getDevice().createCommandEncoder().mapBuffer(this.buffer, true, false);

         try {
            TracyClient.frameImage(mappedView.data(), this.width, this.height, this.offset, true);
         } catch (Throwable var5) {
            if (mappedView != null) {
               try {
                  mappedView.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (mappedView != null) {
            mappedView.close();
         }

      }
   }

   public void markFrame() {
      ++this.offset;
      this.captured = false;
      TracyClient.markFrame();
   }

   public void close() {
      this.texture.close();
      this.textureView.close();
      this.buffer.close();
   }

   @Environment(EnvType.CLIENT)
   static enum Status {
      WAITING_FOR_CAPTURE,
      WAITING_FOR_COPY,
      WAITING_FOR_UPLOAD;

      // $FF: synthetic method
      private static Status[] method_68340() {
         return new Status[]{WAITING_FOR_CAPTURE, WAITING_FOR_COPY, WAITING_FOR_UPLOAD};
      }
   }
}
