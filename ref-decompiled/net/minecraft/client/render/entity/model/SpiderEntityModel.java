package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SpiderEntityModel extends EntityModel {
   private static final String BODY0 = "body0";
   private static final String BODY1 = "body1";
   private static final String RIGHT_MIDDLE_FRONT_LEG = "right_middle_front_leg";
   private static final String LEFT_MIDDLE_FRONT_LEG = "left_middle_front_leg";
   private static final String RIGHT_MIDDLE_HIND_LEG = "right_middle_hind_leg";
   private static final String LEFT_MIDDLE_HIND_LEG = "left_middle_hind_leg";
   private final ModelPart head;
   private final ModelPart rightHindLeg;
   private final ModelPart leftHindLeg;
   private final ModelPart rightMiddleLeg;
   private final ModelPart leftMiddleLeg;
   private final ModelPart rightMiddleFrontLeg;
   private final ModelPart leftMiddleFrontLeg;
   private final ModelPart rightFrontLeg;
   private final ModelPart leftFrontLeg;

   public SpiderEntityModel(ModelPart modelPart) {
      super(modelPart);
      this.head = modelPart.getChild("head");
      this.rightHindLeg = modelPart.getChild("right_hind_leg");
      this.leftHindLeg = modelPart.getChild("left_hind_leg");
      this.rightMiddleLeg = modelPart.getChild("right_middle_hind_leg");
      this.leftMiddleLeg = modelPart.getChild("left_middle_hind_leg");
      this.rightMiddleFrontLeg = modelPart.getChild("right_middle_front_leg");
      this.leftMiddleFrontLeg = modelPart.getChild("left_middle_front_leg");
      this.rightFrontLeg = modelPart.getChild("right_front_leg");
      this.leftFrontLeg = modelPart.getChild("left_front_leg");
   }

   public static TexturedModelData getTexturedModelData() {
      ModelData modelData = new ModelData();
      ModelPartData modelPartData = modelData.getRoot();
      int i = true;
      modelPartData.addChild("head", ModelPartBuilder.create().uv(32, 4).cuboid(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F), ModelTransform.origin(0.0F, 15.0F, -3.0F));
      modelPartData.addChild("body0", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F), ModelTransform.origin(0.0F, 15.0F, 0.0F));
      modelPartData.addChild("body1", ModelPartBuilder.create().uv(0, 12).cuboid(-5.0F, -4.0F, -6.0F, 10.0F, 8.0F, 12.0F), ModelTransform.origin(0.0F, 15.0F, 9.0F));
      ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(18, 0).cuboid(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F);
      ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(18, 0).mirrored().cuboid(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F);
      float f = 0.7853982F;
      float g = 0.3926991F;
      modelPartData.addChild("right_hind_leg", modelPartBuilder, ModelTransform.of(-4.0F, 15.0F, 2.0F, 0.0F, 0.7853982F, -0.7853982F));
      modelPartData.addChild("left_hind_leg", modelPartBuilder2, ModelTransform.of(4.0F, 15.0F, 2.0F, 0.0F, -0.7853982F, 0.7853982F));
      modelPartData.addChild("right_middle_hind_leg", modelPartBuilder, ModelTransform.of(-4.0F, 15.0F, 1.0F, 0.0F, 0.3926991F, -0.58119464F));
      modelPartData.addChild("left_middle_hind_leg", modelPartBuilder2, ModelTransform.of(4.0F, 15.0F, 1.0F, 0.0F, -0.3926991F, 0.58119464F));
      modelPartData.addChild("right_middle_front_leg", modelPartBuilder, ModelTransform.of(-4.0F, 15.0F, 0.0F, 0.0F, -0.3926991F, -0.58119464F));
      modelPartData.addChild("left_middle_front_leg", modelPartBuilder2, ModelTransform.of(4.0F, 15.0F, 0.0F, 0.0F, 0.3926991F, 0.58119464F));
      modelPartData.addChild("right_front_leg", modelPartBuilder, ModelTransform.of(-4.0F, 15.0F, -1.0F, 0.0F, -0.7853982F, -0.7853982F));
      modelPartData.addChild("left_front_leg", modelPartBuilder2, ModelTransform.of(4.0F, 15.0F, -1.0F, 0.0F, 0.7853982F, 0.7853982F));
      return TexturedModelData.of(modelData, 64, 32);
   }

   public void setAngles(LivingEntityRenderState livingEntityRenderState) {
      super.setAngles(livingEntityRenderState);
      this.head.yaw = livingEntityRenderState.relativeHeadYaw * 0.017453292F;
      this.head.pitch = livingEntityRenderState.pitch * 0.017453292F;
      float f = livingEntityRenderState.limbSwingAnimationProgress * 0.6662F;
      float g = livingEntityRenderState.limbSwingAmplitude;
      float h = -(MathHelper.cos(f * 2.0F + 0.0F) * 0.4F) * g;
      float i = -(MathHelper.cos(f * 2.0F + 3.1415927F) * 0.4F) * g;
      float j = -(MathHelper.cos(f * 2.0F + 1.5707964F) * 0.4F) * g;
      float k = -(MathHelper.cos(f * 2.0F + 4.712389F) * 0.4F) * g;
      float l = Math.abs(MathHelper.sin(f + 0.0F) * 0.4F) * g;
      float m = Math.abs(MathHelper.sin(f + 3.1415927F) * 0.4F) * g;
      float n = Math.abs(MathHelper.sin(f + 1.5707964F) * 0.4F) * g;
      float o = Math.abs(MathHelper.sin(f + 4.712389F) * 0.4F) * g;
      ModelPart var10000 = this.rightHindLeg;
      var10000.yaw += h;
      var10000 = this.leftHindLeg;
      var10000.yaw -= h;
      var10000 = this.rightMiddleLeg;
      var10000.yaw += i;
      var10000 = this.leftMiddleLeg;
      var10000.yaw -= i;
      var10000 = this.rightMiddleFrontLeg;
      var10000.yaw += j;
      var10000 = this.leftMiddleFrontLeg;
      var10000.yaw -= j;
      var10000 = this.rightFrontLeg;
      var10000.yaw += k;
      var10000 = this.leftFrontLeg;
      var10000.yaw -= k;
      var10000 = this.rightHindLeg;
      var10000.roll += l;
      var10000 = this.leftHindLeg;
      var10000.roll -= l;
      var10000 = this.rightMiddleLeg;
      var10000.roll += m;
      var10000 = this.leftMiddleLeg;
      var10000.roll -= m;
      var10000 = this.rightMiddleFrontLeg;
      var10000.roll += n;
      var10000 = this.leftMiddleFrontLeg;
      var10000.roll -= n;
      var10000 = this.rightFrontLeg;
      var10000.roll += o;
      var10000 = this.leftFrontLeg;
      var10000.roll -= o;
   }
}
