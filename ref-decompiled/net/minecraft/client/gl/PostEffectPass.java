package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.FramePass;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.Handle;
import net.minecraft.util.Identifier;
import org.lwjgl.system.MemoryStack;

@Environment(EnvType.CLIENT)
public class PostEffectPass implements AutoCloseable {
   private static final int SIZE = (new Std140SizeCalculator()).putVec2().get();
   private final String id;
   private final RenderPipeline pipeline;
   private final Identifier outputTargetId;
   private final Map uniformBuffers = new HashMap();
   private final MappableRingBuffer samplerInfoBuffer;
   private final List samplers;

   public PostEffectPass(RenderPipeline pipeline, Identifier outputTargetId, Map uniforms, List samplers) {
      this.pipeline = pipeline;
      this.id = pipeline.getLocation().toString();
      this.outputTargetId = outputTargetId;
      this.samplers = samplers;
      Iterator var5 = uniforms.entrySet().iterator();

      while(true) {
         Map.Entry entry;
         List list;
         do {
            if (!var5.hasNext()) {
               this.samplerInfoBuffer = new MappableRingBuffer(() -> {
                  return this.id + " SamplerInfo";
               }, 130, (samplers.size() + 1) * SIZE);
               return;
            }

            entry = (Map.Entry)var5.next();
            list = (List)entry.getValue();
         } while(list.isEmpty());

         Std140SizeCalculator std140SizeCalculator = new Std140SizeCalculator();
         Iterator var9 = list.iterator();

         while(var9.hasNext()) {
            UniformValue uniformValue = (UniformValue)var9.next();
            uniformValue.addSize(std140SizeCalculator);
         }

         int i = std140SizeCalculator.get();
         MemoryStack memoryStack = MemoryStack.stackPush();

         try {
            Std140Builder std140Builder = Std140Builder.onStack(memoryStack, i);
            Iterator var12 = list.iterator();

            while(true) {
               if (!var12.hasNext()) {
                  this.uniformBuffers.put((String)entry.getKey(), RenderSystem.getDevice().createBuffer(() -> {
                     String var10000 = this.id;
                     return var10000 + " / " + (String)entry.getKey();
                  }, 128, std140Builder.get()));
                  break;
               }

               UniformValue uniformValue2 = (UniformValue)var12.next();
               uniformValue2.write(std140Builder);
            }
         } catch (Throwable var15) {
            if (memoryStack != null) {
               try {
                  memoryStack.close();
               } catch (Throwable var14) {
                  var15.addSuppressed(var14);
               }
            }

            throw var15;
         }

         if (memoryStack != null) {
            memoryStack.close();
         }
      }
   }

   public void render(FrameGraphBuilder builder, Map handles, GpuBufferSlice gpuBufferSlice) {
      FramePass framePass = builder.createPass(this.id);
      Iterator var5 = this.samplers.iterator();

      while(var5.hasNext()) {
         Sampler sampler = (Sampler)var5.next();
         sampler.preRender(framePass, handles);
      }

      Handle handle = (Handle)handles.computeIfPresent(this.outputTargetId, (id, handlex) -> {
         return framePass.transfer(handlex);
      });
      if (handle == null) {
         throw new IllegalStateException("Missing handle for target " + String.valueOf(this.outputTargetId));
      } else {
         framePass.setRenderer(() -> {
            Framebuffer framebuffer = (Framebuffer)handle.get();
            RenderSystem.backupProjectionMatrix();
            RenderSystem.setProjectionMatrix(gpuBufferSlice, ProjectionType.ORTHOGRAPHIC);
            CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
            List list = this.samplers.stream().map((samplerx) -> {
               return Pair.of(samplerx.samplerName(), samplerx.getTexture(handles));
            }).toList();
            GpuBuffer.MappedView mappedView = commandEncoder.mapBuffer(this.samplerInfoBuffer.getBlocking(), false, true);

            try {
               Std140Builder std140Builder = Std140Builder.intoBuffer(mappedView.data());
               std140Builder.putVec2((float)framebuffer.textureWidth, (float)framebuffer.textureHeight);
               Iterator var9 = list.iterator();

               while(var9.hasNext()) {
                  Pair pair = (Pair)var9.next();
                  std140Builder.putVec2((float)((GpuTextureView)pair.getSecond()).getWidth(0), (float)((GpuTextureView)pair.getSecond()).getHeight(0));
               }
            } catch (Throwable var16) {
               if (mappedView != null) {
                  try {
                     mappedView.close();
                  } catch (Throwable var14) {
                     var16.addSuppressed(var14);
                  }
               }

               throw var16;
            }

            if (mappedView != null) {
               mappedView.close();
            }

            GpuBuffer gpuBuffer = RenderSystem.getQuadVertexBuffer();
            RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS);
            GpuBuffer gpuBuffer2 = shapeIndexBuffer.getIndexBuffer(6);
            RenderPass renderPass = commandEncoder.createRenderPass(() -> {
               return "Post pass " + this.id;
            }, framebuffer.getColorAttachmentView(), OptionalInt.empty(), framebuffer.useDepthAttachment ? framebuffer.getDepthAttachmentView() : null, OptionalDouble.empty());

            try {
               renderPass.setPipeline(this.pipeline);
               RenderSystem.bindDefaultUniforms(renderPass);
               renderPass.setUniform("SamplerInfo", this.samplerInfoBuffer.getBlocking());
               Iterator var11 = this.uniformBuffers.entrySet().iterator();

               while(var11.hasNext()) {
                  Map.Entry entry = (Map.Entry)var11.next();
                  renderPass.setUniform((String)entry.getKey(), (GpuBuffer)entry.getValue());
               }

               renderPass.setVertexBuffer(0, gpuBuffer);
               renderPass.setIndexBuffer(gpuBuffer2, shapeIndexBuffer.getIndexType());
               var11 = list.iterator();

               while(true) {
                  if (!var11.hasNext()) {
                     renderPass.drawIndexed(0, 0, 6, 1);
                     break;
                  }

                  Pair pair2 = (Pair)var11.next();
                  renderPass.bindSampler((String)pair2.getFirst() + "Sampler", (GpuTextureView)pair2.getSecond());
               }
            } catch (Throwable var15) {
               if (renderPass != null) {
                  try {
                     renderPass.close();
                  } catch (Throwable var13) {
                     var15.addSuppressed(var13);
                  }
               }

               throw var15;
            }

            if (renderPass != null) {
               renderPass.close();
            }

            this.samplerInfoBuffer.rotate();
            RenderSystem.restoreProjectionMatrix();
            Iterator var21 = this.samplers.iterator();

            while(var21.hasNext()) {
               Sampler sampler = (Sampler)var21.next();
               sampler.postRender(handles);
            }

         });
      }
   }

   public void close() {
      Iterator var1 = this.uniformBuffers.values().iterator();

      while(var1.hasNext()) {
         GpuBuffer gpuBuffer = (GpuBuffer)var1.next();
         gpuBuffer.close();
      }

      this.samplerInfoBuffer.close();
   }

   @Environment(EnvType.CLIENT)
   public interface Sampler {
      void preRender(FramePass pass, Map internalTargets);

      default void postRender(Map internalTargets) {
      }

      GpuTextureView getTexture(Map internalTargets);

      String samplerName();
   }

   @Environment(EnvType.CLIENT)
   public static record TargetSampler(String samplerName, Identifier targetId, boolean depthBuffer, boolean bilinear) implements Sampler {
      public TargetSampler(String string, Identifier identifier, boolean bl, boolean bl2) {
         this.samplerName = string;
         this.targetId = identifier;
         this.depthBuffer = bl;
         this.bilinear = bl2;
      }

      private Handle getTarget(Map internalTargets) {
         Handle handle = (Handle)internalTargets.get(this.targetId);
         if (handle == null) {
            throw new IllegalStateException("Missing handle for target " + String.valueOf(this.targetId));
         } else {
            return handle;
         }
      }

      public void preRender(FramePass pass, Map internalTargets) {
         pass.dependsOn(this.getTarget(internalTargets));
      }

      public void postRender(Map internalTargets) {
         if (this.bilinear) {
            ((Framebuffer)this.getTarget(internalTargets).get()).setFilter(FilterMode.NEAREST);
         }

      }

      public GpuTextureView getTexture(Map internalTargets) {
         Handle handle = this.getTarget(internalTargets);
         Framebuffer framebuffer = (Framebuffer)handle.get();
         framebuffer.setFilter(this.bilinear ? FilterMode.LINEAR : FilterMode.NEAREST);
         GpuTextureView gpuTextureView = this.depthBuffer ? framebuffer.getDepthAttachmentView() : framebuffer.getColorAttachmentView();
         if (gpuTextureView == null) {
            String var10002 = this.depthBuffer ? "depth" : "color";
            throw new IllegalStateException("Missing " + var10002 + "texture for target " + String.valueOf(this.targetId));
         } else {
            return gpuTextureView;
         }
      }

      public String samplerName() {
         return this.samplerName;
      }

      public Identifier targetId() {
         return this.targetId;
      }

      public boolean depthBuffer() {
         return this.depthBuffer;
      }

      public boolean bilinear() {
         return this.bilinear;
      }
   }

   @Environment(EnvType.CLIENT)
   public static record TextureSampler(String samplerName, AbstractTexture texture, int width, int height) implements Sampler {
      public TextureSampler(String string, AbstractTexture abstractTexture, int i, int j) {
         this.samplerName = string;
         this.texture = abstractTexture;
         this.width = i;
         this.height = j;
      }

      public void preRender(FramePass pass, Map internalTargets) {
      }

      public GpuTextureView getTexture(Map internalTargets) {
         return this.texture.getGlTextureView();
      }

      public String samplerName() {
         return this.samplerName;
      }

      public AbstractTexture texture() {
         return this.texture;
      }

      public int width() {
         return this.width;
      }

      public int height() {
         return this.height;
      }
   }
}
