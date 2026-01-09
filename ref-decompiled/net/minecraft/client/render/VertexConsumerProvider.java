package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectSortedMaps;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SequencedMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.BufferAllocator;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public interface VertexConsumerProvider {
   static Immediate immediate(BufferAllocator buffer) {
      return immediate(Object2ObjectSortedMaps.emptyMap(), buffer);
   }

   static Immediate immediate(SequencedMap layerBuffers, BufferAllocator fallbackBuffer) {
      return new Immediate(fallbackBuffer, layerBuffers);
   }

   VertexConsumer getBuffer(RenderLayer layer);

   @Environment(EnvType.CLIENT)
   public static class Immediate implements VertexConsumerProvider {
      protected final BufferAllocator allocator;
      protected final SequencedMap layerBuffers;
      protected final Map pending = new HashMap();
      @Nullable
      protected RenderLayer currentLayer;

      protected Immediate(BufferAllocator allocator, SequencedMap sequencedMap) {
         this.allocator = allocator;
         this.layerBuffers = sequencedMap;
      }

      public VertexConsumer getBuffer(RenderLayer renderLayer) {
         BufferBuilder bufferBuilder = (BufferBuilder)this.pending.get(renderLayer);
         if (bufferBuilder != null && !renderLayer.areVerticesNotShared()) {
            this.draw(renderLayer, bufferBuilder);
            bufferBuilder = null;
         }

         if (bufferBuilder != null) {
            return bufferBuilder;
         } else {
            BufferAllocator bufferAllocator = (BufferAllocator)this.layerBuffers.get(renderLayer);
            if (bufferAllocator != null) {
               bufferBuilder = new BufferBuilder(bufferAllocator, renderLayer.getDrawMode(), renderLayer.getVertexFormat());
            } else {
               if (this.currentLayer != null) {
                  this.draw(this.currentLayer);
               }

               bufferBuilder = new BufferBuilder(this.allocator, renderLayer.getDrawMode(), renderLayer.getVertexFormat());
               this.currentLayer = renderLayer;
            }

            this.pending.put(renderLayer, bufferBuilder);
            return bufferBuilder;
         }
      }

      public void drawCurrentLayer() {
         if (this.currentLayer != null) {
            this.draw(this.currentLayer);
            this.currentLayer = null;
         }

      }

      public void draw() {
         this.drawCurrentLayer();
         Iterator var1 = this.layerBuffers.keySet().iterator();

         while(var1.hasNext()) {
            RenderLayer renderLayer = (RenderLayer)var1.next();
            this.draw(renderLayer);
         }

      }

      public void draw(RenderLayer layer) {
         BufferBuilder bufferBuilder = (BufferBuilder)this.pending.remove(layer);
         if (bufferBuilder != null) {
            this.draw(layer, bufferBuilder);
         }

      }

      private void draw(RenderLayer layer, BufferBuilder builder) {
         BuiltBuffer builtBuffer = builder.endNullable();
         if (builtBuffer != null) {
            if (layer.isTranslucent()) {
               BufferAllocator bufferAllocator = (BufferAllocator)this.layerBuffers.getOrDefault(layer, this.allocator);
               builtBuffer.sortQuads(bufferAllocator, RenderSystem.getProjectionType().getVertexSorter());
            }

            layer.draw(builtBuffer);
         }

         if (layer.equals(this.currentLayer)) {
            this.currentLayer = null;
         }

      }
   }
}
