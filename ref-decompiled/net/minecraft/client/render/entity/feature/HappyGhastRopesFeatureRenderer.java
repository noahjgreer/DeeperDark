package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.HappyGhastEntityModel;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.state.HappyGhastEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class HappyGhastRopesFeatureRenderer extends FeatureRenderer {
   private final RenderLayer renderLayer;
   private final HappyGhastEntityModel model;
   private final HappyGhastEntityModel babyModel;

   public HappyGhastRopesFeatureRenderer(FeatureRendererContext context, LoadedEntityModels loader, Identifier texture) {
      super(context);
      this.renderLayer = RenderLayer.getEntityCutoutNoCull(texture);
      this.model = new HappyGhastEntityModel(loader.getModelPart(EntityModelLayers.HAPPY_GHAST_ROPES));
      this.babyModel = new HappyGhastEntityModel(loader.getModelPart(EntityModelLayers.HAPPY_GHAST_BABY_ROPES));
   }

   public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, HappyGhastEntityRenderState happyGhastEntityRenderState, float f, float g) {
      if (happyGhastEntityRenderState.hasRopes && happyGhastEntityRenderState.harnessStack.isIn(ItemTags.HARNESSES)) {
         HappyGhastEntityModel happyGhastEntityModel = happyGhastEntityRenderState.baby ? this.babyModel : this.model;
         happyGhastEntityModel.setAngles(happyGhastEntityRenderState);
         happyGhastEntityModel.render(matrixStack, vertexConsumerProvider.getBuffer(this.renderLayer), i, OverlayTexture.DEFAULT_UV);
      }
   }
}
