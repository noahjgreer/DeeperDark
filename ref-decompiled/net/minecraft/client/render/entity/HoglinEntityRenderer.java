package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.HoglinEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class HoglinEntityRenderer extends AbstractHoglinEntityRenderer {
   private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/hoglin/hoglin.png");

   public HoglinEntityRenderer(EntityRendererFactory.Context context) {
      super(context, EntityModelLayers.HOGLIN, EntityModelLayers.HOGLIN_BABY, 0.7F);
   }

   public Identifier getTexture(HoglinEntityRenderState hoglinEntityRenderState) {
      return TEXTURE;
   }

   public void updateRenderState(HoglinEntity hoglinEntity, HoglinEntityRenderState hoglinEntityRenderState, float f) {
      super.updateRenderState((MobEntity)hoglinEntity, (HoglinEntityRenderState)hoglinEntityRenderState, f);
      hoglinEntityRenderState.canConvert = hoglinEntity.canConvert();
   }

   protected boolean isShaking(HoglinEntityRenderState hoglinEntityRenderState) {
      return super.isShaking(hoglinEntityRenderState) || hoglinEntityRenderState.canConvert;
   }

   // $FF: synthetic method
   protected boolean isShaking(final LivingEntityRenderState state) {
      return this.isShaking((HoglinEntityRenderState)state);
   }

   // $FF: synthetic method
   public Identifier getTexture(final LivingEntityRenderState state) {
      return this.getTexture((HoglinEntityRenderState)state);
   }
}
