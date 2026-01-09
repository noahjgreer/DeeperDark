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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.state.VexEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class VexEntityModel extends EntityModel implements ModelWithArms {
   private final ModelPart body;
   private final ModelPart rightArm;
   private final ModelPart leftArm;
   private final ModelPart rightWing;
   private final ModelPart leftWing;
   private final ModelPart head;

   public VexEntityModel(ModelPart modelPart) {
      super(modelPart.getChild("root"), RenderLayer::getEntityTranslucent);
      this.body = this.root.getChild("body");
      this.rightArm = this.body.getChild("right_arm");
      this.leftArm = this.body.getChild("left_arm");
      this.rightWing = this.body.getChild("right_wing");
      this.leftWing = this.body.getChild("left_wing");
      this.head = this.root.getChild("head");
   }

   public static TexturedModelData getTexturedModelData() {
      ModelData modelData = new ModelData();
      ModelPartData modelPartData = modelData.getRoot();
      ModelPartData modelPartData2 = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.origin(0.0F, -2.5F, 0.0F));
      modelPartData2.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-2.5F, -5.0F, -2.5F, 5.0F, 5.0F, 5.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 20.0F, 0.0F));
      ModelPartData modelPartData3 = modelPartData2.addChild("body", ModelPartBuilder.create().uv(0, 10).cuboid(-1.5F, 0.0F, -1.0F, 3.0F, 4.0F, 2.0F, new Dilation(0.0F)).uv(0, 16).cuboid(-1.5F, 1.0F, -1.0F, 3.0F, 5.0F, 2.0F, new Dilation(-0.2F)), ModelTransform.origin(0.0F, 20.0F, 0.0F));
      modelPartData3.addChild("right_arm", ModelPartBuilder.create().uv(23, 0).cuboid(-1.25F, -0.5F, -1.0F, 2.0F, 4.0F, 2.0F, new Dilation(-0.1F)), ModelTransform.origin(-1.75F, 0.25F, 0.0F));
      modelPartData3.addChild("left_arm", ModelPartBuilder.create().uv(23, 6).cuboid(-0.75F, -0.5F, -1.0F, 2.0F, 4.0F, 2.0F, new Dilation(-0.1F)), ModelTransform.origin(1.75F, 0.25F, 0.0F));
      modelPartData3.addChild("left_wing", ModelPartBuilder.create().uv(16, 14).mirrored().cuboid(0.0F, 0.0F, 0.0F, 0.0F, 5.0F, 8.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.origin(0.5F, 1.0F, 1.0F));
      modelPartData3.addChild("right_wing", ModelPartBuilder.create().uv(16, 14).cuboid(0.0F, 0.0F, 0.0F, 0.0F, 5.0F, 8.0F, new Dilation(0.0F)), ModelTransform.origin(-0.5F, 1.0F, 1.0F));
      return TexturedModelData.of(modelData, 32, 32);
   }

   public void setAngles(VexEntityRenderState vexEntityRenderState) {
      super.setAngles(vexEntityRenderState);
      this.head.yaw = vexEntityRenderState.relativeHeadYaw * 0.017453292F;
      this.head.pitch = vexEntityRenderState.pitch * 0.017453292F;
      float f = MathHelper.cos(vexEntityRenderState.age * 5.5F * 0.017453292F) * 0.1F;
      this.rightArm.roll = 0.62831855F + f;
      this.leftArm.roll = -(0.62831855F + f);
      if (vexEntityRenderState.charging) {
         this.body.pitch = 0.0F;
         this.setChargingArmAngles(!vexEntityRenderState.rightHandItemState.isEmpty(), !vexEntityRenderState.leftHandItemState.isEmpty(), f);
      } else {
         this.body.pitch = 0.15707964F;
      }

      this.leftWing.yaw = 1.0995574F + MathHelper.cos(vexEntityRenderState.age * 45.836624F * 0.017453292F) * 0.017453292F * 16.2F;
      this.rightWing.yaw = -this.leftWing.yaw;
      this.leftWing.pitch = 0.47123888F;
      this.leftWing.roll = -0.47123888F;
      this.rightWing.pitch = 0.47123888F;
      this.rightWing.roll = 0.47123888F;
   }

   private void setChargingArmAngles(boolean bl, boolean bl2, float f) {
      if (!bl && !bl2) {
         this.rightArm.pitch = -1.2217305F;
         this.rightArm.yaw = 0.2617994F;
         this.rightArm.roll = -0.47123888F - f;
         this.leftArm.pitch = -1.2217305F;
         this.leftArm.yaw = -0.2617994F;
         this.leftArm.roll = 0.47123888F + f;
      } else {
         if (bl) {
            this.rightArm.pitch = 3.6651914F;
            this.rightArm.yaw = 0.2617994F;
            this.rightArm.roll = -0.47123888F - f;
         }

         if (bl2) {
            this.leftArm.pitch = 3.6651914F;
            this.leftArm.yaw = -0.2617994F;
            this.leftArm.roll = 0.47123888F + f;
         }

      }
   }

   public void setArmAngle(Arm arm, MatrixStack matrices) {
      boolean bl = arm == Arm.RIGHT;
      ModelPart modelPart = bl ? this.rightArm : this.leftArm;
      this.root.applyTransform(matrices);
      this.body.applyTransform(matrices);
      modelPart.applyTransform(matrices);
      matrices.scale(0.55F, 0.55F, 0.55F);
      this.translateForHand(matrices, bl);
   }

   private void translateForHand(MatrixStack matrices, boolean mainHand) {
      if (mainHand) {
         matrices.translate(0.046875, -0.15625, 0.078125);
      } else {
         matrices.translate(-0.046875, -0.15625, 0.078125);
      }

   }
}
