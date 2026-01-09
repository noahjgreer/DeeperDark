package net.minecraft.client.gui;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.ProjectionMatrix3;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.CubemapTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;

@Environment(EnvType.CLIENT)
public class CubeMapRenderer implements AutoCloseable {
   private static final int FACES_COUNT = 6;
   private final GpuBuffer buffer;
   private final ProjectionMatrix3 projectionMatrix;
   private final Identifier id;

   public CubeMapRenderer(Identifier id) {
      this.id = id;
      this.projectionMatrix = new ProjectionMatrix3("cubemap", 0.05F, 10.0F);
      this.buffer = upload();
   }

   public void draw(MinecraftClient client, float x, float y) {
      RenderSystem.setProjectionMatrix(this.projectionMatrix.set(client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight(), 85.0F), ProjectionType.PERSPECTIVE);
      RenderPipeline renderPipeline = RenderPipelines.POSITION_TEX_PANORAMA;
      Framebuffer framebuffer = MinecraftClient.getInstance().getFramebuffer();
      GpuTextureView gpuTextureView = framebuffer.getColorAttachmentView();
      GpuTextureView gpuTextureView2 = framebuffer.getDepthAttachmentView();
      RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS);
      GpuBuffer gpuBuffer = shapeIndexBuffer.getIndexBuffer(36);
      Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
      matrix4fStack.pushMatrix();
      matrix4fStack.rotationX(3.1415927F);
      matrix4fStack.rotateX(x * 0.017453292F);
      matrix4fStack.rotateY(y * 0.017453292F);
      GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().write(new Matrix4f(matrix4fStack), new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), new Vector3f(), new Matrix4f(), 0.0F);
      matrix4fStack.popMatrix();
      RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> {
         return "Cubemap";
      }, gpuTextureView, OptionalInt.empty(), gpuTextureView2, OptionalDouble.empty());

      try {
         renderPass.setPipeline(renderPipeline);
         RenderSystem.bindDefaultUniforms(renderPass);
         renderPass.setVertexBuffer(0, this.buffer);
         renderPass.setIndexBuffer(gpuBuffer, shapeIndexBuffer.getIndexType());
         renderPass.setUniform("DynamicTransforms", gpuBufferSlice);
         renderPass.bindSampler("Sampler0", client.getTextureManager().getTexture(this.id).getGlTextureView());
         renderPass.drawIndexed(0, 0, 36, 1);
      } catch (Throwable var16) {
         if (renderPass != null) {
            try {
               renderPass.close();
            } catch (Throwable var15) {
               var16.addSuppressed(var15);
            }
         }

         throw var16;
      }

      if (renderPass != null) {
         renderPass.close();
      }

   }

   private static GpuBuffer upload() {
      BufferAllocator bufferAllocator = BufferAllocator.method_72201(VertexFormats.POSITION.getVertexSize() * 4 * 6);

      GpuBuffer var3;
      try {
         BufferBuilder bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
         bufferBuilder.vertex(-1.0F, -1.0F, 1.0F);
         bufferBuilder.vertex(-1.0F, 1.0F, 1.0F);
         bufferBuilder.vertex(1.0F, 1.0F, 1.0F);
         bufferBuilder.vertex(1.0F, -1.0F, 1.0F);
         bufferBuilder.vertex(1.0F, -1.0F, 1.0F);
         bufferBuilder.vertex(1.0F, 1.0F, 1.0F);
         bufferBuilder.vertex(1.0F, 1.0F, -1.0F);
         bufferBuilder.vertex(1.0F, -1.0F, -1.0F);
         bufferBuilder.vertex(1.0F, -1.0F, -1.0F);
         bufferBuilder.vertex(1.0F, 1.0F, -1.0F);
         bufferBuilder.vertex(-1.0F, 1.0F, -1.0F);
         bufferBuilder.vertex(-1.0F, -1.0F, -1.0F);
         bufferBuilder.vertex(-1.0F, -1.0F, -1.0F);
         bufferBuilder.vertex(-1.0F, 1.0F, -1.0F);
         bufferBuilder.vertex(-1.0F, 1.0F, 1.0F);
         bufferBuilder.vertex(-1.0F, -1.0F, 1.0F);
         bufferBuilder.vertex(-1.0F, -1.0F, -1.0F);
         bufferBuilder.vertex(-1.0F, -1.0F, 1.0F);
         bufferBuilder.vertex(1.0F, -1.0F, 1.0F);
         bufferBuilder.vertex(1.0F, -1.0F, -1.0F);
         bufferBuilder.vertex(-1.0F, 1.0F, 1.0F);
         bufferBuilder.vertex(-1.0F, 1.0F, -1.0F);
         bufferBuilder.vertex(1.0F, 1.0F, -1.0F);
         bufferBuilder.vertex(1.0F, 1.0F, 1.0F);
         BuiltBuffer builtBuffer = bufferBuilder.end();

         try {
            var3 = RenderSystem.getDevice().createBuffer(() -> {
               return "Cube map vertex buffer";
            }, 32, builtBuffer.getBuffer());
         } catch (Throwable var7) {
            if (builtBuffer != null) {
               try {
                  builtBuffer.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (builtBuffer != null) {
            builtBuffer.close();
         }
      } catch (Throwable var8) {
         if (bufferAllocator != null) {
            try {
               bufferAllocator.close();
            } catch (Throwable var5) {
               var8.addSuppressed(var5);
            }
         }

         throw var8;
      }

      if (bufferAllocator != null) {
         bufferAllocator.close();
      }

      return var3;
   }

   public void registerTextures(TextureManager textureManager) {
      textureManager.registerTexture(this.id, (AbstractTexture)(new CubemapTexture(this.id)));
   }

   public void close() {
      this.buffer.close();
      this.projectionMatrix.close();
   }
}
