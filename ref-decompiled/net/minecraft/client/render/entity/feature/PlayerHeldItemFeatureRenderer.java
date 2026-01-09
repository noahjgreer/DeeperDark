package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PlayerHeldItemFeatureRenderer extends HeldItemFeatureRenderer {
   private static final float HEAD_YAW = -0.5235988F;
   private static final float HEAD_ROLL = 1.5707964F;

   public PlayerHeldItemFeatureRenderer(FeatureRendererContext featureRendererContext) {
      super(featureRendererContext);
   }

   protected void renderItem(PlayerEntityRenderState playerEntityRenderState, ItemRenderState itemRenderState, Arm arm, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
      if (!itemRenderState.isEmpty()) {
         Hand hand = arm == playerEntityRenderState.mainArm ? Hand.MAIN_HAND : Hand.OFF_HAND;
         if (playerEntityRenderState.isUsingItem && playerEntityRenderState.activeHand == hand && playerEntityRenderState.handSwingProgress < 1.0E-5F && !playerEntityRenderState.spyglassState.isEmpty()) {
            this.renderSpyglass(playerEntityRenderState.spyglassState, arm, matrixStack, vertexConsumerProvider, i);
         } else {
            super.renderItem(playerEntityRenderState, itemRenderState, arm, matrixStack, vertexConsumerProvider, i);
         }

      }
   }

   private void renderSpyglass(ItemRenderState spyglassState, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
      matrices.push();
      this.getContextModel().getRootPart().applyTransform(matrices);
      ModelPart modelPart = ((ModelWithHead)this.getContextModel()).getHead();
      float f = modelPart.pitch;
      modelPart.pitch = MathHelper.clamp(modelPart.pitch, -0.5235988F, 1.5707964F);
      modelPart.applyTransform(matrices);
      modelPart.pitch = f;
      HeadFeatureRenderer.translate(matrices, HeadFeatureRenderer.HeadTransformation.DEFAULT);
      boolean bl = arm == Arm.LEFT;
      matrices.translate((bl ? -2.5F : 2.5F) / 16.0F, -0.0625F, 0.0F);
      spyglassState.render(matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
      matrices.pop();
   }
}
