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
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;

@Environment(EnvType.CLIENT)
public class ArmorStandArmorEntityModel extends BipedEntityModel {
   public ArmorStandArmorEntityModel(ModelPart modelPart) {
      super(modelPart);
   }

   public static TexturedModelData getTexturedModelData(Dilation dilation) {
      ModelData modelData = BipedEntityModel.getModelData(dilation, 0.0F);
      ModelPartData modelPartData = modelData.getRoot();
      ModelPartData modelPartData2 = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, dilation), ModelTransform.origin(0.0F, 1.0F, 0.0F));
      modelPartData2.addChild("hat", ModelPartBuilder.create().uv(32, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, dilation.add(0.5F)), ModelTransform.NONE);
      modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation.add(-0.1F)), ModelTransform.origin(-1.9F, 11.0F, 0.0F));
      modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(0, 16).mirrored().cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation.add(-0.1F)), ModelTransform.origin(1.9F, 11.0F, 0.0F));
      return TexturedModelData.of(modelData, 64, 32);
   }

   public void setAngles(ArmorStandEntityRenderState armorStandEntityRenderState) {
      super.setAngles((BipedEntityRenderState)armorStandEntityRenderState);
      this.head.pitch = 0.017453292F * armorStandEntityRenderState.headRotation.pitch();
      this.head.yaw = 0.017453292F * armorStandEntityRenderState.headRotation.yaw();
      this.head.roll = 0.017453292F * armorStandEntityRenderState.headRotation.roll();
      this.body.pitch = 0.017453292F * armorStandEntityRenderState.bodyRotation.pitch();
      this.body.yaw = 0.017453292F * armorStandEntityRenderState.bodyRotation.yaw();
      this.body.roll = 0.017453292F * armorStandEntityRenderState.bodyRotation.roll();
      this.leftArm.pitch = 0.017453292F * armorStandEntityRenderState.leftArmRotation.pitch();
      this.leftArm.yaw = 0.017453292F * armorStandEntityRenderState.leftArmRotation.yaw();
      this.leftArm.roll = 0.017453292F * armorStandEntityRenderState.leftArmRotation.roll();
      this.rightArm.pitch = 0.017453292F * armorStandEntityRenderState.rightArmRotation.pitch();
      this.rightArm.yaw = 0.017453292F * armorStandEntityRenderState.rightArmRotation.yaw();
      this.rightArm.roll = 0.017453292F * armorStandEntityRenderState.rightArmRotation.roll();
      this.leftLeg.pitch = 0.017453292F * armorStandEntityRenderState.leftLegRotation.pitch();
      this.leftLeg.yaw = 0.017453292F * armorStandEntityRenderState.leftLegRotation.yaw();
      this.leftLeg.roll = 0.017453292F * armorStandEntityRenderState.leftLegRotation.roll();
      this.rightLeg.pitch = 0.017453292F * armorStandEntityRenderState.rightLegRotation.pitch();
      this.rightLeg.yaw = 0.017453292F * armorStandEntityRenderState.rightLegRotation.yaw();
      this.rightLeg.roll = 0.017453292F * armorStandEntityRenderState.rightLegRotation.roll();
   }
}
