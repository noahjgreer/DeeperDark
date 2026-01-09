package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.GhastEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.GhastEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GhastEntityRenderer extends MobEntityRenderer {
   private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/ghast/ghast.png");
   private static final Identifier SHOOTING_TEXTURE = Identifier.ofVanilla("textures/entity/ghast/ghast_shooting.png");

   public GhastEntityRenderer(EntityRendererFactory.Context context) {
      super(context, new GhastEntityModel(context.getPart(EntityModelLayers.GHAST)), 1.5F);
   }

   public Identifier getTexture(GhastEntityRenderState ghastEntityRenderState) {
      return ghastEntityRenderState.shooting ? SHOOTING_TEXTURE : TEXTURE;
   }

   public GhastEntityRenderState createRenderState() {
      return new GhastEntityRenderState();
   }

   public void updateRenderState(GhastEntity ghastEntity, GhastEntityRenderState ghastEntityRenderState, float f) {
      super.updateRenderState(ghastEntity, ghastEntityRenderState, f);
      ghastEntityRenderState.shooting = ghastEntity.isShooting();
   }

   // $FF: synthetic method
   public Identifier getTexture(final LivingEntityRenderState state) {
      return this.getTexture((GhastEntityRenderState)state);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
