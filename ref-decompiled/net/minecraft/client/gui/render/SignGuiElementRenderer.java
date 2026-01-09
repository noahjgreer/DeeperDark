package net.minecraft.client.gui.render;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.render.state.special.SignGuiElementRenderState;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class SignGuiElementRenderer extends SpecialGuiElementRenderer {
   public SignGuiElementRenderer(VertexConsumerProvider.Immediate immediate) {
      super(immediate);
   }

   public Class getElementClass() {
      return SignGuiElementRenderState.class;
   }

   protected void render(SignGuiElementRenderState signGuiElementRenderState, MatrixStack matrixStack) {
      MinecraftClient.getInstance().gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.ITEMS_FLAT);
      matrixStack.translate(0.0F, -0.75F, 0.0F);
      SpriteIdentifier spriteIdentifier = TexturedRenderLayers.getSignTextureId(signGuiElementRenderState.woodType());
      Model model = signGuiElementRenderState.signModel();
      VertexConsumerProvider.Immediate var10001 = this.vertexConsumers;
      Objects.requireNonNull(model);
      VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(var10001, model::getLayer);
      model.render(matrixStack, vertexConsumer, 15728880, OverlayTexture.DEFAULT_UV);
   }

   protected String getName() {
      return "sign";
   }
}
