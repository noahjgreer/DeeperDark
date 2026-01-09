package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ArmPosing {
   public static void hold(ModelPart holdingArm, ModelPart otherArm, ModelPart head, boolean rightArm) {
      ModelPart modelPart = rightArm ? holdingArm : otherArm;
      ModelPart modelPart2 = rightArm ? otherArm : holdingArm;
      modelPart.yaw = (rightArm ? -0.3F : 0.3F) + head.yaw;
      modelPart2.yaw = (rightArm ? 0.6F : -0.6F) + head.yaw;
      modelPart.pitch = -1.5707964F + head.pitch + 0.1F;
      modelPart2.pitch = -1.5F + head.pitch;
   }

   public static void charge(ModelPart holdingArm, ModelPart pullingArm, float crossbowPullTime, int itemUseTime, boolean rightArm) {
      ModelPart modelPart = rightArm ? holdingArm : pullingArm;
      ModelPart modelPart2 = rightArm ? pullingArm : holdingArm;
      modelPart.yaw = rightArm ? -0.8F : 0.8F;
      modelPart.pitch = -0.97079635F;
      modelPart2.pitch = modelPart.pitch;
      float f = MathHelper.clamp((float)itemUseTime, 0.0F, crossbowPullTime);
      float g = f / crossbowPullTime;
      modelPart2.yaw = MathHelper.lerp(g, 0.4F, 0.85F) * (float)(rightArm ? 1 : -1);
      modelPart2.pitch = MathHelper.lerp(g, modelPart2.pitch, -1.5707964F);
   }

   public static void meleeAttack(ModelPart rightArm, ModelPart leftArm, Arm mainArm, float swingProgress, float animationProgress) {
      float f = MathHelper.sin(swingProgress * 3.1415927F);
      float g = MathHelper.sin((1.0F - (1.0F - swingProgress) * (1.0F - swingProgress)) * 3.1415927F);
      rightArm.roll = 0.0F;
      leftArm.roll = 0.0F;
      rightArm.yaw = 0.15707964F;
      leftArm.yaw = -0.15707964F;
      if (mainArm == Arm.RIGHT) {
         rightArm.pitch = -1.8849558F + MathHelper.cos(animationProgress * 0.09F) * 0.15F;
         leftArm.pitch = -0.0F + MathHelper.cos(animationProgress * 0.19F) * 0.5F;
         rightArm.pitch += f * 2.2F - g * 0.4F;
         leftArm.pitch += f * 1.2F - g * 0.4F;
      } else {
         rightArm.pitch = -0.0F + MathHelper.cos(animationProgress * 0.19F) * 0.5F;
         leftArm.pitch = -1.8849558F + MathHelper.cos(animationProgress * 0.09F) * 0.15F;
         rightArm.pitch += f * 1.2F - g * 0.4F;
         leftArm.pitch += f * 2.2F - g * 0.4F;
      }

      swingArms(rightArm, leftArm, animationProgress);
   }

   public static void swingArm(ModelPart arm, float animationProgress, float sigma) {
      arm.roll += sigma * (MathHelper.cos(animationProgress * 0.09F) * 0.05F + 0.05F);
      arm.pitch += sigma * MathHelper.sin(animationProgress * 0.067F) * 0.05F;
   }

   public static void swingArms(ModelPart rightArm, ModelPart leftArm, float animationProgress) {
      swingArm(rightArm, animationProgress, 1.0F);
      swingArm(leftArm, animationProgress, -1.0F);
   }

   public static void zombieArms(ModelPart leftArm, ModelPart rightArm, boolean attacking, float swingProgress, float animationProgress) {
      float f = MathHelper.sin(swingProgress * 3.1415927F);
      float g = MathHelper.sin((1.0F - (1.0F - swingProgress) * (1.0F - swingProgress)) * 3.1415927F);
      rightArm.roll = 0.0F;
      leftArm.roll = 0.0F;
      rightArm.yaw = -(0.1F - f * 0.6F);
      leftArm.yaw = 0.1F - f * 0.6F;
      float h = -3.1415927F / (attacking ? 1.5F : 2.25F);
      rightArm.pitch = h;
      leftArm.pitch = h;
      rightArm.pitch += f * 1.2F - g * 0.4F;
      leftArm.pitch += f * 1.2F - g * 0.4F;
      swingArms(rightArm, leftArm, animationProgress);
   }
}
