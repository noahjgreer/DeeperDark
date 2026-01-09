package net.minecraft.client.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.EnumMap;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;

@Environment(EnvType.CLIENT)
public record SectionRenderState(EnumMap drawsPerLayer, int maxIndicesRequired, GpuBufferSlice[] dynamicTransforms) {
   public SectionRenderState(EnumMap enumMap, int i, GpuBufferSlice[] gpuBufferSlices) {
      this.drawsPerLayer = enumMap;
      this.maxIndicesRequired = i;
      this.dynamicTransforms = gpuBufferSlices;
   }

   public void renderSection(BlockRenderLayerGroup group) {
      RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS);
      GpuBuffer gpuBuffer = this.maxIndicesRequired == 0 ? null : shapeIndexBuffer.getIndexBuffer(this.maxIndicesRequired);
      VertexFormat.IndexType indexType = this.maxIndicesRequired == 0 ? null : shapeIndexBuffer.getIndexType();
      BlockRenderLayer[] blockRenderLayers = group.getLayers();
      MinecraftClient minecraftClient = MinecraftClient.getInstance();
      boolean bl = false;
      Framebuffer framebuffer = group.getFramebuffer();
      RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> {
         return "Section layers for " + group.getName();
      }, framebuffer.getColorAttachmentView(), OptionalInt.empty(), framebuffer.getDepthAttachmentView(), OptionalDouble.empty());

      try {
         RenderSystem.bindDefaultUniforms(renderPass);
         renderPass.bindSampler("Sampler2", minecraftClient.gameRenderer.getLightmapTextureManager().getGlTextureView());
         BlockRenderLayer[] var10 = blockRenderLayers;
         int var11 = blockRenderLayers.length;

         for(int var12 = 0; var12 < var11; ++var12) {
            BlockRenderLayer blockRenderLayer = var10[var12];
            List list = (List)this.drawsPerLayer.get(blockRenderLayer);
            if (!list.isEmpty()) {
               if (blockRenderLayer == BlockRenderLayer.TRANSLUCENT) {
                  list = list.reversed();
               }

               renderPass.setPipeline(bl ? RenderPipelines.WIREFRAME : blockRenderLayer.getPipeline());
               renderPass.bindSampler("Sampler0", blockRenderLayer.getTextureView());
               renderPass.drawMultipleIndexed(list, gpuBuffer, indexType, List.of("DynamicTransforms"), this.dynamicTransforms);
            }
         }
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

   public EnumMap drawsPerLayer() {
      return this.drawsPerLayer;
   }

   public int maxIndicesRequired() {
      return this.maxIndicesRequired;
   }

   public GpuBufferSlice[] dynamicTransforms() {
      return this.dynamicTransforms;
   }
}
