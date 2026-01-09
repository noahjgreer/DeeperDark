package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.annotation.DeobfuscateClass;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
@DeobfuscateClass
public interface RenderPass extends AutoCloseable {
   void pushDebugGroup(Supplier labelGetter);

   void popDebugGroup();

   void setPipeline(RenderPipeline pipeline);

   void bindSampler(String name, @Nullable GpuTextureView texture);

   void setUniform(String name, GpuBuffer buffer);

   void setUniform(String name, GpuBufferSlice slice);

   void enableScissor(int x, int y, int width, int height);

   void disableScissor();

   void setVertexBuffer(int index, GpuBuffer buffer);

   void setIndexBuffer(GpuBuffer indexBuffer, VertexFormat.IndexType indexType);

   void drawIndexed(int baseVertex, int firstIndex, int count, int instanceCount);

   void drawMultipleIndexed(Collection objects, @Nullable GpuBuffer buffer, @Nullable VertexFormat.IndexType indexType, Collection validationSkippedUniforms, Object object);

   void draw(int offset, int count);

   void close();

   @Environment(EnvType.CLIENT)
   public interface UniformUploader {
      void upload(String name, GpuBufferSlice slice);
   }

   @Environment(EnvType.CLIENT)
   public static record RenderObject(int slot, GpuBuffer vertexBuffer, @Nullable GpuBuffer indexBuffer, @Nullable VertexFormat.IndexType indexType, int firstIndex, int indexCount, @Nullable BiConsumer uniformUploaderConsumer) {
      public RenderObject(int slot, GpuBuffer vertexBuffer, GpuBuffer indexBuffer, VertexFormat.IndexType indexType, int firstIndex, int indexCount) {
         this(slot, vertexBuffer, indexBuffer, indexType, firstIndex, indexCount, (BiConsumer)null);
      }

      public RenderObject(int i, GpuBuffer gpuBuffer, @Nullable GpuBuffer gpuBuffer2, @Nullable VertexFormat.IndexType indexType, int j, int k, @Nullable BiConsumer biConsumer) {
         this.slot = i;
         this.vertexBuffer = gpuBuffer;
         this.indexBuffer = gpuBuffer2;
         this.indexType = indexType;
         this.firstIndex = j;
         this.indexCount = k;
         this.uniformUploaderConsumer = biConsumer;
      }

      public int slot() {
         return this.slot;
      }

      public GpuBuffer vertexBuffer() {
         return this.vertexBuffer;
      }

      @Nullable
      public GpuBuffer indexBuffer() {
         return this.indexBuffer;
      }

      @Nullable
      public VertexFormat.IndexType indexType() {
         return this.indexType;
      }

      public int firstIndex() {
         return this.firstIndex;
      }

      public int indexCount() {
         return this.indexCount;
      }

      @Nullable
      public BiConsumer uniformUploaderConsumer() {
         return this.uniformUploaderConsumer;
      }
   }
}
