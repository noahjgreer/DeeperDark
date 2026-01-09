package net.minecraft.client.gui.render;

import java.util.Iterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.render.state.special.ProfilerChartGuiElementRenderState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.ProfilerTiming;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class ProfilerChartGuiElementRenderer extends SpecialGuiElementRenderer {
   public ProfilerChartGuiElementRenderer(VertexConsumerProvider.Immediate immediate) {
      super(immediate);
   }

   public Class getElementClass() {
      return ProfilerChartGuiElementRenderState.class;
   }

   protected void render(ProfilerChartGuiElementRenderState profilerChartGuiElementRenderState, MatrixStack matrixStack) {
      double d = 0.0;
      matrixStack.translate(0.0F, -5.0F, 0.0F);
      Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

      ProfilerTiming profilerTiming;
      for(Iterator var6 = profilerChartGuiElementRenderState.chartData().iterator(); var6.hasNext(); d += profilerTiming.parentSectionUsagePercentage) {
         profilerTiming = (ProfilerTiming)var6.next();
         int i = MathHelper.floor(profilerTiming.parentSectionUsagePercentage / 4.0) + 1;
         VertexConsumer vertexConsumer = this.vertexConsumers.getBuffer(RenderLayer.getDebugTriangleFan());
         int j = ColorHelper.fullAlpha(profilerTiming.getColor());
         int k = ColorHelper.mix(j, -8355712);
         vertexConsumer.vertex(matrix4f, 0.0F, 0.0F, 0.0F).color(j);

         int l;
         float f;
         float g;
         float h;
         for(l = i; l >= 0; --l) {
            f = (float)((d + profilerTiming.parentSectionUsagePercentage * (double)l / (double)i) * 6.2831854820251465 / 100.0);
            g = MathHelper.sin(f) * 105.0F;
            h = MathHelper.cos(f) * 105.0F * 0.5F;
            vertexConsumer.vertex(matrix4f, g, h, 0.0F).color(j);
         }

         vertexConsumer = this.vertexConsumers.getBuffer(RenderLayer.getDebugQuads());

         for(l = i; l > 0; --l) {
            f = (float)((d + profilerTiming.parentSectionUsagePercentage * (double)l / (double)i) * 6.2831854820251465 / 100.0);
            g = MathHelper.sin(f) * 105.0F;
            h = MathHelper.cos(f) * 105.0F * 0.5F;
            float m = (float)((d + profilerTiming.parentSectionUsagePercentage * (double)(l - 1) / (double)i) * 6.2831854820251465 / 100.0);
            float n = MathHelper.sin(m) * 105.0F;
            float o = MathHelper.cos(m) * 105.0F * 0.5F;
            if (!((h + o) / 2.0F < 0.0F)) {
               vertexConsumer.vertex(matrix4f, g, h, 0.0F).color(k);
               vertexConsumer.vertex(matrix4f, g, h + 10.0F, 0.0F).color(k);
               vertexConsumer.vertex(matrix4f, n, o + 10.0F, 0.0F).color(k);
               vertexConsumer.vertex(matrix4f, n, o, 0.0F).color(k);
            }
         }
      }

   }

   protected float getYOffset(int height, int windowScaleFactor) {
      return (float)height / 2.0F;
   }

   protected String getName() {
      return "profiler chart";
   }
}
