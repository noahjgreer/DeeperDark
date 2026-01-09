package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PolarBearEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PolarBearEntityRenderState;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PolarBearEntityRenderer extends AgeableMobEntityRenderer {
   private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/bear/polarbear.png");

   public PolarBearEntityRenderer(EntityRendererFactory.Context context) {
      super(context, new PolarBearEntityModel(context.getPart(EntityModelLayers.POLAR_BEAR)), new PolarBearEntityModel(context.getPart(EntityModelLayers.POLAR_BEAR_BABY)), 0.9F);
   }

   public Identifier getTexture(PolarBearEntityRenderState polarBearEntityRenderState) {
      return TEXTURE;
   }

   public PolarBearEntityRenderState createRenderState() {
      return new PolarBearEntityRenderState();
   }

   public void updateRenderState(PolarBearEntity polarBearEntity, PolarBearEntityRenderState polarBearEntityRenderState, float f) {
      super.updateRenderState(polarBearEntity, polarBearEntityRenderState, f);
      polarBearEntityRenderState.warningAnimationProgress = polarBearEntity.getWarningAnimationProgress(f);
   }

   // $FF: synthetic method
   public Identifier getTexture(final LivingEntityRenderState state) {
      return this.getTexture((PolarBearEntityRenderState)state);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
