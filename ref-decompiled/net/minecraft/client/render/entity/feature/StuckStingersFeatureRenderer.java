package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.StingerModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class StuckStingersFeatureRenderer extends StuckObjectsFeatureRenderer {
   private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/bee/bee_stinger.png");

   public StuckStingersFeatureRenderer(LivingEntityRenderer entityRenderer, EntityRendererFactory.Context context) {
      super(entityRenderer, new StingerModel(context.getPart(EntityModelLayers.BEE_STINGER)), TEXTURE, StuckObjectsFeatureRenderer.RenderPosition.ON_SURFACE);
   }

   protected int getObjectCount(PlayerEntityRenderState playerRenderState) {
      return playerRenderState.stingerCount;
   }
}
