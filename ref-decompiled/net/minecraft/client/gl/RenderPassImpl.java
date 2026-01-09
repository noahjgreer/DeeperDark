package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class RenderPassImpl implements RenderPass {
   protected static final int field_57866 = 1;
   public static final boolean IS_DEVELOPMENT;
   private final GlCommandEncoder resourceManager;
   private final boolean hasDepth;
   private boolean closed;
   @Nullable
   protected CompiledShaderPipeline pipeline;
   protected final GpuBuffer[] vertexBuffers = new GpuBuffer[1];
   @Nullable
   protected GpuBuffer indexBuffer;
   protected VertexFormat.IndexType indexType;
   private final ScissorState scissorState;
   protected final HashMap simpleUniforms;
   protected final HashMap samplerUniforms;
   protected final Set setSimpleUniforms;
   protected int debugGroupPushCount;

   public RenderPassImpl(GlCommandEncoder resourceManager, boolean hasDepth) {
      this.indexType = VertexFormat.IndexType.INT;
      this.scissorState = new ScissorState();
      this.simpleUniforms = new HashMap();
      this.samplerUniforms = new HashMap();
      this.setSimpleUniforms = new HashSet();
      this.resourceManager = resourceManager;
      this.hasDepth = hasDepth;
   }

   public boolean hasDepth() {
      return this.hasDepth;
   }

   public void pushDebugGroup(Supplier supplier) {
      if (this.closed) {
         throw new IllegalStateException("Can't use a closed render pass");
      } else {
         ++this.debugGroupPushCount;
         this.resourceManager.getBackend().getDebugLabelManager().pushDebugGroup(supplier);
      }
   }

   public void popDebugGroup() {
      if (this.closed) {
         throw new IllegalStateException("Can't use a closed render pass");
      } else if (this.debugGroupPushCount == 0) {
         throw new IllegalStateException("Can't pop more debug groups than was pushed!");
      } else {
         --this.debugGroupPushCount;
         this.resourceManager.getBackend().getDebugLabelManager().popDebugGroup();
      }
   }

   public void setPipeline(RenderPipeline renderPipeline) {
      if (this.pipeline == null || this.pipeline.info() != renderPipeline) {
         this.setSimpleUniforms.addAll(this.simpleUniforms.keySet());
         this.setSimpleUniforms.addAll(this.samplerUniforms.keySet());
      }

      this.pipeline = this.resourceManager.getBackend().compilePipelineCached(renderPipeline);
   }

   public void bindSampler(String string, @Nullable GpuTextureView gpuTextureView) {
      if (gpuTextureView == null) {
         this.samplerUniforms.remove(string);
      } else {
         this.samplerUniforms.put(string, gpuTextureView);
      }

      this.setSimpleUniforms.add(string);
   }

   public void setUniform(String string, GpuBuffer gpuBuffer) {
      this.simpleUniforms.put(string, gpuBuffer.slice());
      this.setSimpleUniforms.add(string);
   }

   public void setUniform(String string, GpuBufferSlice gpuBufferSlice) {
      int i = this.resourceManager.getBackend().getUniformOffsetAlignment();
      if (gpuBufferSlice.offset() % i > 0) {
         throw new IllegalArgumentException("Uniform buffer offset must be aligned to " + i);
      } else {
         this.simpleUniforms.put(string, gpuBufferSlice);
         this.setSimpleUniforms.add(string);
      }
   }

   public void enableScissor(int i, int j, int k, int l) {
      this.scissorState.enable(i, j, k, l);
   }

   public void disableScissor() {
      this.scissorState.disable();
   }

   public boolean isScissorEnabled() {
      return this.scissorState.method_72091();
   }

   public int getScissorX() {
      return this.scissorState.method_72092();
   }

   public int getScissorY() {
      return this.scissorState.method_72093();
   }

   public int getScissorWidth() {
      return this.scissorState.method_72094();
   }

   public int getScissorHeight() {
      return this.scissorState.method_72095();
   }

   public void setVertexBuffer(int i, GpuBuffer gpuBuffer) {
      if (i >= 0 && i < 1) {
         this.vertexBuffers[i] = gpuBuffer;
      } else {
         throw new IllegalArgumentException("Vertex buffer slot is out of range: " + i);
      }
   }

   public void setIndexBuffer(@Nullable GpuBuffer gpuBuffer, VertexFormat.IndexType indexType) {
      this.indexBuffer = gpuBuffer;
      this.indexType = indexType;
   }

   public void drawIndexed(int i, int j, int k, int l) {
      if (this.closed) {
         throw new IllegalStateException("Can't use a closed render pass");
      } else {
         this.resourceManager.drawBoundObjectWithRenderPass(this, i, j, k, this.indexType, l);
      }
   }

   public void drawMultipleIndexed(Collection collection, @Nullable GpuBuffer gpuBuffer, @Nullable VertexFormat.IndexType indexType, Collection collection2, Object object) {
      if (this.closed) {
         throw new IllegalStateException("Can't use a closed render pass");
      } else {
         this.resourceManager.drawObjectsWithRenderPass(this, collection, gpuBuffer, indexType, collection2, object);
      }
   }

   public void draw(int i, int j) {
      if (this.closed) {
         throw new IllegalStateException("Can't use a closed render pass");
      } else {
         this.resourceManager.drawBoundObjectWithRenderPass(this, i, 0, j, (VertexFormat.IndexType)null, 1);
      }
   }

   public void close() {
      if (!this.closed) {
         if (this.debugGroupPushCount > 0) {
            throw new IllegalStateException("Render pass had debug groups left open!");
         }

         this.closed = true;
         this.resourceManager.closePass();
      }

   }

   static {
      IS_DEVELOPMENT = SharedConstants.isDevelopment;
   }
}
