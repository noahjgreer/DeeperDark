package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

/** @deprecated */
@Deprecated
@Environment(EnvType.CLIENT)
public abstract class AgeableMobEntityRenderer extends MobEntityRenderer {
   private final EntityModel adultModel;
   private final EntityModel babyModel;

   public AgeableMobEntityRenderer(EntityRendererFactory.Context context, EntityModel model, EntityModel babyModel, float shadowRadius) {
      super(context, model, shadowRadius);
      this.adultModel = model;
      this.babyModel = babyModel;
   }

   public void render(LivingEntityRenderState livingEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
      this.model = livingEntityRenderState.baby ? this.babyModel : this.adultModel;
      super.render(livingEntityRenderState, matrixStack, vertexConsumerProvider, i);
   }
}
