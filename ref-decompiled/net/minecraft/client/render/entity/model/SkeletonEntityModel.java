package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.SkeletonEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SkeletonEntityModel extends BipedEntityModel {
   public SkeletonEntityModel(ModelPart modelPart) {
      super(modelPart);
   }

   public static TexturedModelData getTexturedModelData() {
      ModelData modelData = BipedEntityModel.getModelData(Dilation.NONE, 0.0F);
      ModelPartData modelPartData = modelData.getRoot();
      addLimbs(modelPartData);
      return TexturedModelData.of(modelData, 64, 32);
   }

   protected static void addLimbs(ModelPartData data) {
      data.addChild("right_arm", ModelPartBuilder.create().uv(40, 16).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F), ModelTransform.origin(-5.0F, 2.0F, 0.0F));
      data.addChild("left_arm", ModelPartBuilder.create().uv(40, 16).mirrored().cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F), ModelTransform.origin(5.0F, 2.0F, 0.0F));
      data.addChild("right_leg", ModelPartBuilder.create().uv(0, 16).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F), ModelTransform.origin(-2.0F, 12.0F, 0.0F));
      data.addChild("left_leg", ModelPartBuilder.create().uv(0, 16).mirrored().cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F), ModelTransform.origin(2.0F, 12.0F, 0.0F));
   }

   public void setAngles(SkeletonEntityRenderState skeletonEntityRenderState) {
      super.setAngles((BipedEntityRenderState)skeletonEntityRenderState);
      if (skeletonEntityRenderState.attacking && !skeletonEntityRenderState.holdingBow) {
         float f = skeletonEntityRenderState.handSwingProgress;
         float g = MathHelper.sin(f * 3.1415927F);
         float h = MathHelper.sin((1.0F - (1.0F - f) * (1.0F - f)) * 3.1415927F);
         this.rightArm.roll = 0.0F;
         this.leftArm.roll = 0.0F;
         this.rightArm.yaw = -(0.1F - g * 0.6F);
         this.leftArm.yaw = 0.1F - g * 0.6F;
         this.rightArm.pitch = -1.5707964F;
         this.leftArm.pitch = -1.5707964F;
         ModelPart var10000 = this.rightArm;
         var10000.pitch -= g * 1.2F - h * 0.4F;
         var10000 = this.leftArm;
         var10000.pitch -= g * 1.2F - h * 0.4F;
         ArmPosing.swingArms(this.rightArm, this.leftArm, skeletonEntityRenderState.age);
      }

   }

   public void setArmAngle(Arm arm, MatrixStack matrices) {
      this.getRootPart().applyTransform(matrices);
      float f = arm == Arm.RIGHT ? 1.0F : -1.0F;
      ModelPart modelPart = this.getArm(arm);
      modelPart.originX += f;
      modelPart.applyTransform(matrices);
      modelPart.originX -= f;
   }
}
