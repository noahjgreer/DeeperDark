package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;

@Environment(EnvType.CLIENT)
public abstract class MobEntityRenderer extends LivingEntityRenderer {
   public MobEntityRenderer(EntityRendererFactory.Context context, EntityModel entityModel, float f) {
      super(context, entityModel, f);
   }

   protected boolean hasLabel(MobEntity mobEntity, double d) {
      return super.hasLabel((LivingEntity)mobEntity, d) && (mobEntity.shouldRenderName() || mobEntity.hasCustomName() && mobEntity == this.dispatcher.targetedEntity);
   }

   protected float getShadowRadius(LivingEntityRenderState livingEntityRenderState) {
      return super.getShadowRadius(livingEntityRenderState) * livingEntityRenderState.ageScale;
   }

   // $FF: synthetic method
   protected float getShadowRadius(final EntityRenderState state) {
      return this.getShadowRadius((LivingEntityRenderState)state);
   }
}
