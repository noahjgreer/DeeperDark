package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.client.render.entity.state.SkeletonEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SkeletonOverlayFeatureRenderer extends FeatureRenderer {
   private final SkeletonEntityModel model;
   private final Identifier texture;

   public SkeletonOverlayFeatureRenderer(FeatureRendererContext context, LoadedEntityModels loader, EntityModelLayer layer, Identifier texture) {
      super(context);
      this.texture = texture;
      this.model = new SkeletonEntityModel(loader.getModelPart(layer));
   }

   public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, SkeletonEntityRenderState skeletonEntityRenderState, float f, float g) {
      render(this.model, this.texture, matrixStack, vertexConsumerProvider, i, skeletonEntityRenderState, -1);
   }
}
