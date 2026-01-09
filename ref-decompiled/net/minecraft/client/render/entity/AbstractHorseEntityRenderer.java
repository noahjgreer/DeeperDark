package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingHorseEntityRenderState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.AbstractHorseEntity;

@Environment(EnvType.CLIENT)
public abstract class AbstractHorseEntityRenderer extends AgeableMobEntityRenderer {
   public AbstractHorseEntityRenderer(EntityRendererFactory.Context context, EntityModel model, EntityModel babyModel) {
      super(context, model, babyModel, 0.75F);
   }

   public void updateRenderState(AbstractHorseEntity abstractHorseEntity, LivingHorseEntityRenderState livingHorseEntityRenderState, float f) {
      super.updateRenderState(abstractHorseEntity, livingHorseEntityRenderState, f);
      livingHorseEntityRenderState.saddleStack = abstractHorseEntity.getEquippedStack(EquipmentSlot.SADDLE).copy();
      livingHorseEntityRenderState.hasPassengers = abstractHorseEntity.hasPassengers();
      livingHorseEntityRenderState.eatingGrassAnimationProgress = abstractHorseEntity.getEatingGrassAnimationProgress(f);
      livingHorseEntityRenderState.angryAnimationProgress = abstractHorseEntity.getAngryAnimationProgress(f);
      livingHorseEntityRenderState.eatingAnimationProgress = abstractHorseEntity.getEatingAnimationProgress(f);
      livingHorseEntityRenderState.waggingTail = abstractHorseEntity.tailWagTicks > 0;
   }
}
