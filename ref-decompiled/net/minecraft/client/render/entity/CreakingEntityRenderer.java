package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.EmissiveFeatureRenderer;
import net.minecraft.client.render.entity.model.CreakingEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.CreakingEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.mob.CreakingEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CreakingEntityRenderer extends MobEntityRenderer {
   private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/creaking/creaking.png");
   private static final Identifier EYES_TEXTURE = Identifier.ofVanilla("textures/entity/creaking/creaking_eyes.png");

   public CreakingEntityRenderer(EntityRendererFactory.Context context) {
      super(context, new CreakingEntityModel(context.getPart(EntityModelLayers.CREAKING)), 0.6F);
      this.addFeature(new EmissiveFeatureRenderer(this, EYES_TEXTURE, (state, tickProgress) -> {
         return 1.0F;
      }, CreakingEntityModel::getEmissiveParts, RenderLayer::getEyes, true));
   }

   public Identifier getTexture(CreakingEntityRenderState creakingEntityRenderState) {
      return TEXTURE;
   }

   public CreakingEntityRenderState createRenderState() {
      return new CreakingEntityRenderState();
   }

   public void updateRenderState(CreakingEntity creakingEntity, CreakingEntityRenderState creakingEntityRenderState, float f) {
      super.updateRenderState(creakingEntity, creakingEntityRenderState, f);
      creakingEntityRenderState.attackAnimationState.copyFrom(creakingEntity.attackAnimationState);
      creakingEntityRenderState.invulnerableAnimationState.copyFrom(creakingEntity.invulnerableAnimationState);
      creakingEntityRenderState.crumblingAnimationState.copyFrom(creakingEntity.crumblingAnimationState);
      if (creakingEntity.isCrumbling()) {
         creakingEntityRenderState.deathTime = 0.0F;
         creakingEntityRenderState.hurt = false;
         creakingEntityRenderState.glowingEyes = creakingEntity.hasGlowingEyesWhileCrumbling();
      } else {
         creakingEntityRenderState.glowingEyes = creakingEntity.isActive();
      }

      creakingEntityRenderState.unrooted = creakingEntity.isUnrooted();
   }

   // $FF: synthetic method
   public Identifier getTexture(final LivingEntityRenderState state) {
      return this.getTexture((CreakingEntityRenderState)state);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
