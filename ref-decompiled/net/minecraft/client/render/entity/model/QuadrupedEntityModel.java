package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class QuadrupedEntityModel extends EntityModel {
   protected final ModelPart head;
   protected final ModelPart body;
   protected final ModelPart rightHindLeg;
   protected final ModelPart leftHindLeg;
   protected final ModelPart rightFrontLeg;
   protected final ModelPart leftFrontLeg;

   protected QuadrupedEntityModel(ModelPart root) {
      super(root);
      this.head = root.getChild("head");
      this.body = root.getChild("body");
      this.rightHindLeg = root.getChild("right_hind_leg");
      this.leftHindLeg = root.getChild("left_hind_leg");
      this.rightFrontLeg = root.getChild("right_front_leg");
      this.leftFrontLeg = root.getChild("left_front_leg");
   }

   public static ModelData getModelData(int stanceWidth, boolean leftMirrored, boolean rightMirrored, Dilation dilation) {
      ModelData modelData = new ModelData();
      ModelPartData modelPartData = modelData.getRoot();
      modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, dilation), ModelTransform.origin(0.0F, (float)(18 - stanceWidth), -6.0F));
      modelPartData.addChild("body", ModelPartBuilder.create().uv(28, 8).cuboid(-5.0F, -10.0F, -7.0F, 10.0F, 16.0F, 8.0F, dilation), ModelTransform.of(0.0F, (float)(17 - stanceWidth), 2.0F, 1.5707964F, 0.0F, 0.0F));
      addLegs(modelPartData, leftMirrored, rightMirrored, stanceWidth, dilation);
      return modelData;
   }

   static void addLegs(ModelPartData root, boolean leftMirrored, boolean rightMirrored, int stanceWidth, Dilation dilation) {
      ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().mirrored(rightMirrored).uv(0, 16).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, (float)stanceWidth, 4.0F, dilation);
      ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().mirrored(leftMirrored).uv(0, 16).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, (float)stanceWidth, 4.0F, dilation);
      root.addChild("right_hind_leg", modelPartBuilder, ModelTransform.origin(-3.0F, (float)(24 - stanceWidth), 7.0F));
      root.addChild("left_hind_leg", modelPartBuilder2, ModelTransform.origin(3.0F, (float)(24 - stanceWidth), 7.0F));
      root.addChild("right_front_leg", modelPartBuilder, ModelTransform.origin(-3.0F, (float)(24 - stanceWidth), -5.0F));
      root.addChild("left_front_leg", modelPartBuilder2, ModelTransform.origin(3.0F, (float)(24 - stanceWidth), -5.0F));
   }

   public void setAngles(LivingEntityRenderState livingEntityRenderState) {
      super.setAngles(livingEntityRenderState);
      this.head.pitch = livingEntityRenderState.pitch * 0.017453292F;
      this.head.yaw = livingEntityRenderState.relativeHeadYaw * 0.017453292F;
      float f = livingEntityRenderState.limbSwingAnimationProgress;
      float g = livingEntityRenderState.limbSwingAmplitude;
      this.rightHindLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
      this.leftHindLeg.pitch = MathHelper.cos(f * 0.6662F + 3.1415927F) * 1.4F * g;
      this.rightFrontLeg.pitch = MathHelper.cos(f * 0.6662F + 3.1415927F) * 1.4F * g;
      this.leftFrontLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
   }
}
