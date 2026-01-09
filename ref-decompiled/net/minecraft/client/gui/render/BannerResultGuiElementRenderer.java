package net.minecraft.client.gui.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.render.state.special.BannerResultGuiElementRenderState;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class BannerResultGuiElementRenderer extends SpecialGuiElementRenderer {
   public BannerResultGuiElementRenderer(VertexConsumerProvider.Immediate immediate) {
      super(immediate);
   }

   public Class getElementClass() {
      return BannerResultGuiElementRenderState.class;
   }

   protected void render(BannerResultGuiElementRenderState bannerResultGuiElementRenderState, MatrixStack matrixStack) {
      MinecraftClient.getInstance().gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.ITEMS_FLAT);
      matrixStack.translate(0.0F, 0.25F, 0.0F);
      BannerBlockEntityRenderer.renderCanvas(matrixStack, this.vertexConsumers, 15728880, OverlayTexture.DEFAULT_UV, bannerResultGuiElementRenderState.flag(), ModelBaker.BANNER_BASE, true, bannerResultGuiElementRenderState.baseColor(), bannerResultGuiElementRenderState.resultBannerPatterns());
   }

   protected String getName() {
      return "banner result";
   }
}
