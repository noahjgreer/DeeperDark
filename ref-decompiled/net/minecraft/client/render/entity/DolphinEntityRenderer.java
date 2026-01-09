package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.DolphinHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.DolphinEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.DolphinEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ItemHolderEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DolphinEntityRenderer extends AgeableMobEntityRenderer {
   private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/dolphin.png");

   public DolphinEntityRenderer(EntityRendererFactory.Context context) {
      super(context, new DolphinEntityModel(context.getPart(EntityModelLayers.DOLPHIN)), new DolphinEntityModel(context.getPart(EntityModelLayers.DOLPHIN_BABY)), 0.7F);
      this.addFeature(new DolphinHeldItemFeatureRenderer(this));
   }

   public Identifier getTexture(DolphinEntityRenderState dolphinEntityRenderState) {
      return TEXTURE;
   }

   public DolphinEntityRenderState createRenderState() {
      return new DolphinEntityRenderState();
   }

   public void updateRenderState(DolphinEntity dolphinEntity, DolphinEntityRenderState dolphinEntityRenderState, float f) {
      super.updateRenderState(dolphinEntity, dolphinEntityRenderState, f);
      ItemHolderEntityRenderState.update(dolphinEntity, dolphinEntityRenderState, this.itemModelResolver);
      dolphinEntityRenderState.moving = dolphinEntity.getVelocity().horizontalLengthSquared() > 1.0E-7;
   }

   // $FF: synthetic method
   public Identifier getTexture(final LivingEntityRenderState state) {
      return this.getTexture((DolphinEntityRenderState)state);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
