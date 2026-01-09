package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.SkeletonOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.BoggedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.client.render.entity.state.BoggedEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.SkeletonEntityRenderState;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.BoggedEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BoggedEntityRenderer extends AbstractSkeletonEntityRenderer {
   private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/skeleton/bogged.png");
   private static final Identifier OVERLAY_TEXTURE = Identifier.ofVanilla("textures/entity/skeleton/bogged_overlay.png");

   public BoggedEntityRenderer(EntityRendererFactory.Context context) {
      super(context, EntityModelLayers.BOGGED_INNER_ARMOR, EntityModelLayers.BOGGED_OUTER_ARMOR, (SkeletonEntityModel)(new BoggedEntityModel(context.getPart(EntityModelLayers.BOGGED))));
      this.addFeature(new SkeletonOverlayFeatureRenderer(this, context.getEntityModels(), EntityModelLayers.BOGGED_OUTER, OVERLAY_TEXTURE));
   }

   public Identifier getTexture(BoggedEntityRenderState boggedEntityRenderState) {
      return TEXTURE;
   }

   public BoggedEntityRenderState createRenderState() {
      return new BoggedEntityRenderState();
   }

   public void updateRenderState(BoggedEntity boggedEntity, BoggedEntityRenderState boggedEntityRenderState, float f) {
      super.updateRenderState((AbstractSkeletonEntity)boggedEntity, (SkeletonEntityRenderState)boggedEntityRenderState, f);
      boggedEntityRenderState.sheared = boggedEntity.isSheared();
   }

   // $FF: synthetic method
   public Identifier getTexture(final LivingEntityRenderState state) {
      return this.getTexture((BoggedEntityRenderState)state);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
