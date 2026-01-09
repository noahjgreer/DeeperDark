package net.minecraft.client.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.border.WorldBorder;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

@Environment(EnvType.CLIENT)
public class WorldBorderRendering {
   public static final Identifier FORCEFIELD = Identifier.ofVanilla("textures/misc/forcefield.png");
   private boolean forceRefreshBuffers = true;
   private double lastUploadedBoundWest;
   private double lastUploadedBoundNorth;
   private double lastXMin;
   private double lastXMax;
   private double lastZMin;
   private double lastZMax;
   private final GpuBuffer vertexBuffer;
   private final RenderSystem.ShapeIndexBuffer indexBuffer;

   public WorldBorderRendering() {
      this.vertexBuffer = RenderSystem.getDevice().createBuffer(() -> {
         return "World border vertex buffer";
      }, 40, 16 * VertexFormats.POSITION_TEXTURE.getVertexSize());
      this.indexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS);
   }

   private void refreshDirectionBuffer(WorldBorder border, double viewDistanceBlocks, double z, double x, float farPlaneDistance, float vMin, float vMax) {
      BufferAllocator bufferAllocator = BufferAllocator.method_72201(VertexFormats.POSITION_TEXTURE.getVertexSize() * 4 * 4);

      try {
         double d = border.getBoundWest();
         double e = border.getBoundEast();
         double f = border.getBoundNorth();
         double g = border.getBoundSouth();
         double h = Math.max((double)MathHelper.floor(z - viewDistanceBlocks), f);
         double i = Math.min((double)MathHelper.ceil(z + viewDistanceBlocks), g);
         float j = (float)(MathHelper.floor(h) & 1) * 0.5F;
         float k = (float)(i - h) / 2.0F;
         double l = Math.max((double)MathHelper.floor(x - viewDistanceBlocks), d);
         double m = Math.min((double)MathHelper.ceil(x + viewDistanceBlocks), e);
         float n = (float)(MathHelper.floor(l) & 1) * 0.5F;
         float o = (float)(m - l) / 2.0F;
         BufferBuilder bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
         bufferBuilder.vertex(0.0F, -farPlaneDistance, (float)(g - h)).texture(n, vMin);
         bufferBuilder.vertex((float)(m - l), -farPlaneDistance, (float)(g - h)).texture(o + n, vMin);
         bufferBuilder.vertex((float)(m - l), farPlaneDistance, (float)(g - h)).texture(o + n, vMax);
         bufferBuilder.vertex(0.0F, farPlaneDistance, (float)(g - h)).texture(n, vMax);
         bufferBuilder.vertex(0.0F, -farPlaneDistance, 0.0F).texture(j, vMin);
         bufferBuilder.vertex(0.0F, -farPlaneDistance, (float)(i - h)).texture(k + j, vMin);
         bufferBuilder.vertex(0.0F, farPlaneDistance, (float)(i - h)).texture(k + j, vMax);
         bufferBuilder.vertex(0.0F, farPlaneDistance, 0.0F).texture(j, vMax);
         bufferBuilder.vertex((float)(m - l), -farPlaneDistance, 0.0F).texture(n, vMin);
         bufferBuilder.vertex(0.0F, -farPlaneDistance, 0.0F).texture(o + n, vMin);
         bufferBuilder.vertex(0.0F, farPlaneDistance, 0.0F).texture(o + n, vMax);
         bufferBuilder.vertex((float)(m - l), farPlaneDistance, 0.0F).texture(n, vMax);
         bufferBuilder.vertex((float)(e - l), -farPlaneDistance, (float)(i - h)).texture(j, vMin);
         bufferBuilder.vertex((float)(e - l), -farPlaneDistance, 0.0F).texture(k + j, vMin);
         bufferBuilder.vertex((float)(e - l), farPlaneDistance, 0.0F).texture(k + j, vMax);
         bufferBuilder.vertex((float)(e - l), farPlaneDistance, (float)(i - h)).texture(j, vMax);
         BuiltBuffer builtBuffer = bufferBuilder.end();

         try {
            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.vertexBuffer.slice(), builtBuffer.getBuffer());
         } catch (Throwable var38) {
            if (builtBuffer != null) {
               try {
                  builtBuffer.close();
               } catch (Throwable var37) {
                  var38.addSuppressed(var37);
               }
            }

            throw var38;
         }

         if (builtBuffer != null) {
            builtBuffer.close();
         }

         this.lastXMin = d;
         this.lastXMax = e;
         this.lastZMin = f;
         this.lastZMax = g;
         this.lastUploadedBoundWest = l;
         this.lastUploadedBoundNorth = h;
         this.forceRefreshBuffers = false;
      } catch (Throwable var39) {
         if (bufferAllocator != null) {
            try {
               bufferAllocator.close();
            } catch (Throwable var36) {
               var39.addSuppressed(var36);
            }
         }

         throw var39;
      }

      if (bufferAllocator != null) {
         bufferAllocator.close();
      }

   }

   public void render(WorldBorder border, Vec3d cameraPos, double viewDistanceBlocks, double farPlaneDistance) {
      double d = border.getBoundWest();
      double e = border.getBoundEast();
      double f = border.getBoundNorth();
      double g = border.getBoundSouth();
      if ((!(cameraPos.x < e - viewDistanceBlocks) || !(cameraPos.x > d + viewDistanceBlocks) || !(cameraPos.z < g - viewDistanceBlocks) || !(cameraPos.z > f + viewDistanceBlocks)) && !(cameraPos.x < d - viewDistanceBlocks) && !(cameraPos.x > e + viewDistanceBlocks) && !(cameraPos.z < f - viewDistanceBlocks) && !(cameraPos.z > g + viewDistanceBlocks)) {
         double h = 1.0 - border.getDistanceInsideBorder(cameraPos.x, cameraPos.z) / viewDistanceBlocks;
         h = Math.pow(h, 4.0);
         h = MathHelper.clamp(h, 0.0, 1.0);
         double i = cameraPos.x;
         double j = cameraPos.z;
         float k = (float)farPlaneDistance;
         int l = border.getStage().getColor();
         float m = (float)ColorHelper.getRed(l) / 255.0F;
         float n = (float)ColorHelper.getGreen(l) / 255.0F;
         float o = (float)ColorHelper.getBlue(l) / 255.0F;
         float p = (float)(Util.getMeasuringTimeMs() % 3000L) / 3000.0F;
         float q = (float)(-MathHelper.fractionalPart(cameraPos.y * 0.5));
         float r = q + k;
         if (this.shouldRefreshBuffers(border)) {
            this.refreshDirectionBuffer(border, viewDistanceBlocks, j, i, k, r, q);
         }

         TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
         AbstractTexture abstractTexture = textureManager.getTexture(FORCEFIELD);
         abstractTexture.setUseMipmaps(false);
         RenderPipeline renderPipeline = RenderPipelines.RENDERTYPE_WORLD_BORDER;
         Framebuffer framebuffer = MinecraftClient.getInstance().getFramebuffer();
         Framebuffer framebuffer2 = MinecraftClient.getInstance().worldRenderer.getWeatherFramebuffer();
         GpuTextureView gpuTextureView;
         GpuTextureView gpuTextureView2;
         if (framebuffer2 != null) {
            gpuTextureView = framebuffer2.getColorAttachmentView();
            gpuTextureView2 = framebuffer2.getDepthAttachmentView();
         } else {
            gpuTextureView = framebuffer.getColorAttachmentView();
            gpuTextureView2 = framebuffer.getDepthAttachmentView();
         }

         GpuBuffer gpuBuffer = this.indexBuffer.getIndexBuffer(6);
         GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().write(RenderSystem.getModelViewMatrix(), new Vector4f(m, n, o, (float)h), new Vector3f((float)(this.lastUploadedBoundWest - i), (float)(-cameraPos.y), (float)(this.lastUploadedBoundNorth - j)), (new Matrix4f()).translation(p, p, 0.0F), 0.0F);
         RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> {
            return "World border";
         }, gpuTextureView, OptionalInt.empty(), gpuTextureView2, OptionalDouble.empty());

         try {
            renderPass.setPipeline(renderPipeline);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", gpuBufferSlice);
            renderPass.setIndexBuffer(gpuBuffer, this.indexBuffer.getIndexType());
            renderPass.bindSampler("Sampler0", abstractTexture.getGlTextureView());
            renderPass.setVertexBuffer(0, this.vertexBuffer);
            ArrayList arrayList = new ArrayList();
            Iterator var40 = border.calculateDistancesFromCamera(i, j).iterator();

            while(var40.hasNext()) {
               WorldBorder.DistanceFromCamera distanceFromCamera = (WorldBorder.DistanceFromCamera)var40.next();
               if (distanceFromCamera.distance() < viewDistanceBlocks) {
                  int s = distanceFromCamera.direction().getHorizontalQuarterTurns();
                  arrayList.add(new RenderPass.RenderObject(0, this.vertexBuffer, gpuBuffer, this.indexBuffer.getIndexType(), 6 * s, 6));
               }
            }

            renderPass.drawMultipleIndexed(arrayList, (GpuBuffer)null, (VertexFormat.IndexType)null, Collections.emptyList(), this);
         } catch (Throwable var44) {
            if (renderPass != null) {
               try {
                  renderPass.close();
               } catch (Throwable var43) {
                  var44.addSuppressed(var43);
               }
            }

            throw var44;
         }

         if (renderPass != null) {
            renderPass.close();
         }

      }
   }

   public void markBuffersDirty() {
      this.forceRefreshBuffers = true;
   }

   private boolean shouldRefreshBuffers(WorldBorder border) {
      return this.forceRefreshBuffers || border.getBoundWest() != this.lastXMin || border.getBoundNorth() != this.lastZMin || border.getBoundEast() != this.lastXMax || border.getBoundSouth() != this.lastZMax;
   }
}
