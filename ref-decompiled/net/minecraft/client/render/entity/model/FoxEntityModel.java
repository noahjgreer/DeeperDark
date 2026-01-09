package net.minecraft.client.render.entity.model;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.state.FoxEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class FoxEntityModel extends EntityModel {
   public static final ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(true, 8.0F, 3.35F, Set.of("head"));
   public final ModelPart head;
   private final ModelPart body;
   private final ModelPart rightHindLeg;
   private final ModelPart leftHindLeg;
   private final ModelPart rightFrontLeg;
   private final ModelPart leftFrontLeg;
   private final ModelPart tail;
   private static final int field_32477 = 6;
   private static final float HEAD_Y_PIVOT = 16.5F;
   private static final float LEG_Y_PIVOT = 17.5F;
   private float legPitchModifier;

   public FoxEntityModel(ModelPart modelPart) {
      super(modelPart);
      this.head = modelPart.getChild("head");
      this.body = modelPart.getChild("body");
      this.rightHindLeg = modelPart.getChild("right_hind_leg");
      this.leftHindLeg = modelPart.getChild("left_hind_leg");
      this.rightFrontLeg = modelPart.getChild("right_front_leg");
      this.leftFrontLeg = modelPart.getChild("left_front_leg");
      this.tail = this.body.getChild("tail");
   }

   public static TexturedModelData getTexturedModelData() {
      ModelData modelData = new ModelData();
      ModelPartData modelPartData = modelData.getRoot();
      ModelPartData modelPartData2 = modelPartData.addChild("head", ModelPartBuilder.create().uv(1, 5).cuboid(-3.0F, -2.0F, -5.0F, 8.0F, 6.0F, 6.0F), ModelTransform.origin(-1.0F, 16.5F, -3.0F));
      modelPartData2.addChild("right_ear", ModelPartBuilder.create().uv(8, 1).cuboid(-3.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F), ModelTransform.NONE);
      modelPartData2.addChild("left_ear", ModelPartBuilder.create().uv(15, 1).cuboid(3.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F), ModelTransform.NONE);
      modelPartData2.addChild("nose", ModelPartBuilder.create().uv(6, 18).cuboid(-1.0F, 2.01F, -8.0F, 4.0F, 2.0F, 3.0F), ModelTransform.NONE);
      ModelPartData modelPartData3 = modelPartData.addChild("body", ModelPartBuilder.create().uv(24, 15).cuboid(-3.0F, 3.999F, -3.5F, 6.0F, 11.0F, 6.0F), ModelTransform.of(0.0F, 16.0F, -6.0F, 1.5707964F, 0.0F, 0.0F));
      Dilation dilation = new Dilation(0.001F);
      ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(4, 24).cuboid(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, dilation);
      ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(13, 24).cuboid(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, dilation);
      modelPartData.addChild("right_hind_leg", modelPartBuilder2, ModelTransform.origin(-5.0F, 17.5F, 7.0F));
      modelPartData.addChild("left_hind_leg", modelPartBuilder, ModelTransform.origin(-1.0F, 17.5F, 7.0F));
      modelPartData.addChild("right_front_leg", modelPartBuilder2, ModelTransform.origin(-5.0F, 17.5F, 0.0F));
      modelPartData.addChild("left_front_leg", modelPartBuilder, ModelTransform.origin(-1.0F, 17.5F, 0.0F));
      modelPartData3.addChild("tail", ModelPartBuilder.create().uv(30, 0).cuboid(2.0F, 0.0F, -1.0F, 4.0F, 9.0F, 5.0F), ModelTransform.of(-4.0F, 15.0F, -1.0F, -0.05235988F, 0.0F, 0.0F));
      return TexturedModelData.of(modelData, 48, 32);
   }

   public void setAngles(FoxEntityRenderState foxEntityRenderState) {
      super.setAngles(foxEntityRenderState);
      float f = foxEntityRenderState.limbSwingAmplitude;
      float g = foxEntityRenderState.limbSwingAnimationProgress;
      this.rightHindLeg.pitch = MathHelper.cos(g * 0.6662F) * 1.4F * f;
      this.leftHindLeg.pitch = MathHelper.cos(g * 0.6662F + 3.1415927F) * 1.4F * f;
      this.rightFrontLeg.pitch = MathHelper.cos(g * 0.6662F + 3.1415927F) * 1.4F * f;
      this.leftFrontLeg.pitch = MathHelper.cos(g * 0.6662F) * 1.4F * f;
      this.head.roll = foxEntityRenderState.headRoll;
      this.rightHindLeg.visible = true;
      this.leftHindLeg.visible = true;
      this.rightFrontLeg.visible = true;
      this.leftFrontLeg.visible = true;
      float h = foxEntityRenderState.ageScale;
      ModelPart var10000;
      float i;
      if (foxEntityRenderState.inSneakingPose) {
         var10000 = this.body;
         var10000.pitch += 0.10471976F;
         i = foxEntityRenderState.bodyRotationHeightOffset;
         var10000 = this.body;
         var10000.originY += i * h;
         var10000 = this.head;
         var10000.originY += i * h;
      } else if (foxEntityRenderState.sleeping) {
         this.body.roll = -1.5707964F;
         var10000 = this.body;
         var10000.originY += 5.0F * h;
         this.tail.pitch = -2.6179938F;
         if (foxEntityRenderState.baby) {
            this.tail.pitch = -2.1816616F;
            var10000 = this.body;
            var10000.originZ += 2.0F;
         }

         var10000 = this.head;
         var10000.originX += 2.0F * h;
         var10000 = this.head;
         var10000.originY += 2.99F * h;
         this.head.yaw = -2.0943952F;
         this.head.roll = 0.0F;
         this.rightHindLeg.visible = false;
         this.leftHindLeg.visible = false;
         this.rightFrontLeg.visible = false;
         this.leftFrontLeg.visible = false;
      } else if (foxEntityRenderState.sitting) {
         this.body.pitch = 0.5235988F;
         var10000 = this.body;
         var10000.originY -= 7.0F * h;
         var10000 = this.body;
         var10000.originZ += 3.0F * h;
         this.tail.pitch = 0.7853982F;
         var10000 = this.tail;
         var10000.originZ -= 1.0F * h;
         this.head.pitch = 0.0F;
         this.head.yaw = 0.0F;
         if (foxEntityRenderState.baby) {
            --this.head.originY;
            var10000 = this.head;
            var10000.originZ -= 0.375F;
         } else {
            var10000 = this.head;
            var10000.originY -= 6.5F;
            var10000 = this.head;
            var10000.originZ += 2.75F;
         }

         this.rightHindLeg.pitch = -1.3089969F;
         var10000 = this.rightHindLeg;
         var10000.originY += 4.0F * h;
         var10000 = this.rightHindLeg;
         var10000.originZ -= 0.25F * h;
         this.leftHindLeg.pitch = -1.3089969F;
         var10000 = this.leftHindLeg;
         var10000.originY += 4.0F * h;
         var10000 = this.leftHindLeg;
         var10000.originZ -= 0.25F * h;
         this.rightFrontLeg.pitch = -0.2617994F;
         this.leftFrontLeg.pitch = -0.2617994F;
      }

      if (!foxEntityRenderState.sleeping && !foxEntityRenderState.walking && !foxEntityRenderState.inSneakingPose) {
         this.head.pitch = foxEntityRenderState.pitch * 0.017453292F;
         this.head.yaw = foxEntityRenderState.relativeHeadYaw * 0.017453292F;
      }

      if (foxEntityRenderState.sleeping) {
         this.head.pitch = 0.0F;
         this.head.yaw = -2.0943952F;
         this.head.roll = MathHelper.cos(foxEntityRenderState.age * 0.027F) / 22.0F;
      }

      if (foxEntityRenderState.inSneakingPose) {
         i = MathHelper.cos(foxEntityRenderState.age) * 0.01F;
         this.body.yaw = i;
         this.rightHindLeg.roll = i;
         this.leftHindLeg.roll = i;
         this.rightFrontLeg.roll = i / 2.0F;
         this.leftFrontLeg.roll = i / 2.0F;
      }

      if (foxEntityRenderState.walking) {
         i = 0.1F;
         this.legPitchModifier += 0.67F;
         this.rightHindLeg.pitch = MathHelper.cos(this.legPitchModifier * 0.4662F) * 0.1F;
         this.leftHindLeg.pitch = MathHelper.cos(this.legPitchModifier * 0.4662F + 3.1415927F) * 0.1F;
         this.rightFrontLeg.pitch = MathHelper.cos(this.legPitchModifier * 0.4662F + 3.1415927F) * 0.1F;
         this.leftFrontLeg.pitch = MathHelper.cos(this.legPitchModifier * 0.4662F) * 0.1F;
      }

   }
}
