package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;

@Environment(EnvType.CLIENT)
public abstract class AbstractZombieModel extends BipedEntityModel {
   protected AbstractZombieModel(ModelPart modelPart) {
      super(modelPart);
   }

   public void setAngles(ZombieEntityRenderState zombieEntityRenderState) {
      super.setAngles((BipedEntityRenderState)zombieEntityRenderState);
      float f = zombieEntityRenderState.handSwingProgress;
      ArmPosing.zombieArms(this.leftArm, this.rightArm, zombieEntityRenderState.attacking, f, zombieEntityRenderState.age);
   }
}
