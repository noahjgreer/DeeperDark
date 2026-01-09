package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.ArrowEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.ArrowEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;

@Environment(EnvType.CLIENT)
public class StuckArrowsFeatureRenderer extends StuckObjectsFeatureRenderer {
   public StuckArrowsFeatureRenderer(LivingEntityRenderer entityRenderer, EntityRendererFactory.Context context) {
      super(entityRenderer, new ArrowEntityModel(context.getPart(EntityModelLayers.ARROW)), ArrowEntityRenderer.TEXTURE, StuckObjectsFeatureRenderer.RenderPosition.IN_CUBE);
   }

   protected int getObjectCount(PlayerEntityRenderState playerRenderState) {
      return playerRenderState.stuckArrowCount;
   }
}
