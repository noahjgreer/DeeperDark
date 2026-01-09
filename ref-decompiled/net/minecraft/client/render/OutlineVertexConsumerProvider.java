package net.minecraft.client.render;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.math.ColorHelper;

@Environment(EnvType.CLIENT)
public class OutlineVertexConsumerProvider implements VertexConsumerProvider {
   private final VertexConsumerProvider.Immediate parent;
   private final VertexConsumerProvider.Immediate plainDrawer = VertexConsumerProvider.immediate(new BufferAllocator(1536));
   private int red = 255;
   private int green = 255;
   private int blue = 255;
   private int alpha = 255;

   public OutlineVertexConsumerProvider(VertexConsumerProvider.Immediate parent) {
      this.parent = parent;
   }

   public VertexConsumer getBuffer(RenderLayer renderLayer) {
      VertexConsumer vertexConsumer;
      if (renderLayer.isOutline()) {
         vertexConsumer = this.plainDrawer.getBuffer(renderLayer);
         return new OutlineVertexConsumer(vertexConsumer, this.red, this.green, this.blue, this.alpha);
      } else {
         vertexConsumer = this.parent.getBuffer(renderLayer);
         Optional optional = renderLayer.getAffectedOutline();
         if (optional.isPresent()) {
            VertexConsumer vertexConsumer2 = this.plainDrawer.getBuffer((RenderLayer)optional.get());
            OutlineVertexConsumer outlineVertexConsumer = new OutlineVertexConsumer(vertexConsumer2, this.red, this.green, this.blue, this.alpha);
            return VertexConsumers.union(outlineVertexConsumer, vertexConsumer);
         } else {
            return vertexConsumer;
         }
      }
   }

   public void setColor(int red, int green, int blue, int alpha) {
      this.red = red;
      this.green = green;
      this.blue = blue;
      this.alpha = alpha;
   }

   public void draw() {
      this.plainDrawer.draw();
   }

   @Environment(EnvType.CLIENT)
   static record OutlineVertexConsumer(VertexConsumer delegate, int color) implements VertexConsumer {
      public OutlineVertexConsumer(VertexConsumer delegate, int red, int green, int blue, int alpha) {
         this(delegate, ColorHelper.getArgb(alpha, red, green, blue));
      }

      private OutlineVertexConsumer(VertexConsumer vertexConsumer, int i) {
         this.delegate = vertexConsumer;
         this.color = i;
      }

      public VertexConsumer vertex(float x, float y, float z) {
         this.delegate.vertex(x, y, z).color(this.color);
         return this;
      }

      public VertexConsumer color(int red, int green, int blue, int alpha) {
         return this;
      }

      public VertexConsumer texture(float u, float v) {
         this.delegate.texture(u, v);
         return this;
      }

      public VertexConsumer overlay(int u, int v) {
         return this;
      }

      public VertexConsumer light(int u, int v) {
         return this;
      }

      public VertexConsumer normal(float x, float y, float z) {
         return this;
      }

      public VertexConsumer delegate() {
         return this.delegate;
      }

      public int color() {
         return this.color;
      }
   }
}
