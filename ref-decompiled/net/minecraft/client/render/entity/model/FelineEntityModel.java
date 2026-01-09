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
import net.minecraft.client.render.entity.state.FelineEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class FelineEntityModel extends EntityModel {
   public static final ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(true, 10.0F, 4.0F, Set.of("head"));
   private static final float field_32527 = 0.0F;
   private static final float BODY_SIZE_Y = 16.0F;
   private static final float field_32529 = -9.0F;
   protected static final float HIND_LEG_PIVOT_Y = 18.0F;
   protected static final float HIND_LEG_PIVOT_Z = 5.0F;
   protected static final float FRONT_LEG_PIVOT_Y = 14.1F;
   private static final float FRONT_LEG_PIVOT_Z = -5.0F;
   private static final String TAIL1 = "tail1";
   private static final String TAIL2 = "tail2";
   protected final ModelPart leftHindLeg;
   protected final ModelPart rightHindLeg;
   protected final ModelPart leftFrontLeg;
   protected final ModelPart rightFrontLeg;
   protected final ModelPart upperTail;
   protected final ModelPart lowerTail;
   protected final ModelPart head;
   protected final ModelPart body;

   public FelineEntityModel(ModelPart modelPart) {
      super(modelPart);
      this.head = modelPart.getChild("head");
      this.body = modelPart.getChild("body");
      this.upperTail = modelPart.getChild("tail1");
      this.lowerTail = modelPart.getChild("tail2");
      this.leftHindLeg = modelPart.getChild("left_hind_leg");
      this.rightHindLeg = modelPart.getChild("right_hind_leg");
      this.leftFrontLeg = modelPart.getChild("left_front_leg");
      this.rightFrontLeg = modelPart.getChild("right_front_leg");
   }

   public static ModelData getModelData(Dilation dilation) {
      ModelData modelData = new ModelData();
      ModelPartData modelPartData = modelData.getRoot();
      Dilation dilation2 = new Dilation(-0.02F);
      modelPartData.addChild("head", ModelPartBuilder.create().cuboid("main", -2.5F, -2.0F, -3.0F, 5.0F, 4.0F, 5.0F, dilation).cuboid("nose", -1.5F, -0.001F, -4.0F, 3, 2, 2, dilation, 0, 24).cuboid("ear1", -2.0F, -3.0F, 0.0F, 1, 1, 2, dilation, 0, 10).cuboid("ear2", 1.0F, -3.0F, 0.0F, 1, 1, 2, dilation, 6, 10), ModelTransform.origin(0.0F, 15.0F, -9.0F));
      modelPartData.addChild("body", ModelPartBuilder.create().uv(20, 0).cuboid(-2.0F, 3.0F, -8.0F, 4.0F, 16.0F, 6.0F, dilation), ModelTransform.of(0.0F, 12.0F, -10.0F, 1.5707964F, 0.0F, 0.0F));
      modelPartData.addChild("tail1", ModelPartBuilder.create().uv(0, 15).cuboid(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, dilation), ModelTransform.of(0.0F, 15.0F, 8.0F, 0.9F, 0.0F, 0.0F));
      modelPartData.addChild("tail2", ModelPartBuilder.create().uv(4, 15).cuboid(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, dilation2), ModelTransform.origin(0.0F, 20.0F, 14.0F));
      ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(8, 13).cuboid(-1.0F, 0.0F, 1.0F, 2.0F, 6.0F, 2.0F, dilation);
      modelPartData.addChild("left_hind_leg", modelPartBuilder, ModelTransform.origin(1.1F, 18.0F, 5.0F));
      modelPartData.addChild("right_hind_leg", modelPartBuilder, ModelTransform.origin(-1.1F, 18.0F, 5.0F));
      ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(40, 0).cuboid(-1.0F, 0.0F, 0.0F, 2.0F, 10.0F, 2.0F, dilation);
      modelPartData.addChild("left_front_leg", modelPartBuilder2, ModelTransform.origin(1.2F, 14.1F, -5.0F));
      modelPartData.addChild("right_front_leg", modelPartBuilder2, ModelTransform.origin(-1.2F, 14.1F, -5.0F));
      return modelData;
   }

   public void setAngles(FelineEntityRenderState felineEntityRenderState) {
      super.setAngles(felineEntityRenderState);
      float f = felineEntityRenderState.ageScale;
      ModelPart var10000;
      if (felineEntityRenderState.inSneakingPose) {
         var10000 = this.body;
         var10000.originY += 1.0F * f;
         var10000 = this.head;
         var10000.originY += 2.0F * f;
         var10000 = this.upperTail;
         var10000.originY += 1.0F * f;
         var10000 = this.lowerTail;
         var10000.originY += -4.0F * f;
         var10000 = this.lowerTail;
         var10000.originZ += 2.0F * f;
         this.upperTail.pitch = 1.5707964F;
         this.lowerTail.pitch = 1.5707964F;
      } else if (felineEntityRenderState.sprinting) {
         this.lowerTail.originY = this.upperTail.originY;
         var10000 = this.lowerTail;
         var10000.originZ += 2.0F * f;
         this.upperTail.pitch = 1.5707964F;
         this.lowerTail.pitch = 1.5707964F;
      }

      this.head.pitch = felineEntityRenderState.pitch * 0.017453292F;
      this.head.yaw = felineEntityRenderState.relativeHeadYaw * 0.017453292F;
      if (!felineEntityRenderState.inSittingPose) {
         this.body.pitch = 1.5707964F;
         float g = felineEntityRenderState.limbSwingAmplitude;
         float h = felineEntityRenderState.limbSwingAnimationProgress;
         if (felineEntityRenderState.sprinting) {
            this.leftHindLeg.pitch = MathHelper.cos(h * 0.6662F) * g;
            this.rightHindLeg.pitch = MathHelper.cos(h * 0.6662F + 0.3F) * g;
            this.leftFrontLeg.pitch = MathHelper.cos(h * 0.6662F + 3.1415927F + 0.3F) * g;
            this.rightFrontLeg.pitch = MathHelper.cos(h * 0.6662F + 3.1415927F) * g;
            this.lowerTail.pitch = 1.7278761F + 0.31415927F * MathHelper.cos(h) * g;
         } else {
            this.leftHindLeg.pitch = MathHelper.cos(h * 0.6662F) * g;
            this.rightHindLeg.pitch = MathHelper.cos(h * 0.6662F + 3.1415927F) * g;
            this.leftFrontLeg.pitch = MathHelper.cos(h * 0.6662F + 3.1415927F) * g;
            this.rightFrontLeg.pitch = MathHelper.cos(h * 0.6662F) * g;
            if (!felineEntityRenderState.inSneakingPose) {
               this.lowerTail.pitch = 1.7278761F + 0.7853982F * MathHelper.cos(h) * g;
            } else {
               this.lowerTail.pitch = 1.7278761F + 0.47123894F * MathHelper.cos(h) * g;
            }
         }
      }

      if (felineEntityRenderState.inSittingPose) {
         this.body.pitch = 0.7853982F;
         var10000 = this.body;
         var10000.originY += -4.0F * f;
         var10000 = this.body;
         var10000.originZ += 5.0F * f;
         var10000 = this.head;
         var10000.originY += -3.3F * f;
         var10000 = this.head;
         var10000.originZ += 1.0F * f;
         var10000 = this.upperTail;
         var10000.originY += 8.0F * f;
         var10000 = this.upperTail;
         var10000.originZ += -2.0F * f;
         var10000 = this.lowerTail;
         var10000.originY += 2.0F * f;
         var10000 = this.lowerTail;
         var10000.originZ += -0.8F * f;
         this.upperTail.pitch = 1.7278761F;
         this.lowerTail.pitch = 2.670354F;
         this.leftFrontLeg.pitch = -0.15707964F;
         var10000 = this.leftFrontLeg;
         var10000.originY += 2.0F * f;
         var10000 = this.leftFrontLeg;
         var10000.originZ -= 2.0F * f;
         this.rightFrontLeg.pitch = -0.15707964F;
         var10000 = this.rightFrontLeg;
         var10000.originY += 2.0F * f;
         var10000 = this.rightFrontLeg;
         var10000.originZ -= 2.0F * f;
         this.leftHindLeg.pitch = -1.5707964F;
         var10000 = this.leftHindLeg;
         var10000.originY += 3.0F * f;
         var10000 = this.leftHindLeg;
         var10000.originZ -= 4.0F * f;
         this.rightHindLeg.pitch = -1.5707964F;
         var10000 = this.rightHindLeg;
         var10000.originY += 3.0F * f;
         var10000 = this.rightHindLeg;
         var10000.originZ -= 4.0F * f;
      }

      if (felineEntityRenderState.sleepAnimationProgress > 0.0F) {
         this.head.roll = MathHelper.lerpAngleDegrees(felineEntityRenderState.sleepAnimationProgress, this.head.roll, -1.2707963F);
         this.head.yaw = MathHelper.lerpAngleDegrees(felineEntityRenderState.sleepAnimationProgress, this.head.yaw, 1.2707963F);
         this.leftFrontLeg.pitch = -1.2707963F;
         this.rightFrontLeg.pitch = -0.47079635F;
         this.rightFrontLeg.roll = -0.2F;
         var10000 = this.rightFrontLeg;
         var10000.originX += f;
         this.leftHindLeg.pitch = -0.4F;
         this.rightHindLeg.pitch = 0.5F;
         this.rightHindLeg.roll = -0.5F;
         var10000 = this.rightHindLeg;
         var10000.originX += 0.8F * f;
         var10000 = this.rightHindLeg;
         var10000.originY += 2.0F * f;
         this.upperTail.pitch = MathHelper.lerpAngleDegrees(felineEntityRenderState.tailCurlAnimationProgress, this.upperTail.pitch, 0.8F);
         this.lowerTail.pitch = MathHelper.lerpAngleDegrees(felineEntityRenderState.tailCurlAnimationProgress, this.lowerTail.pitch, -0.4F);
      }

      if (felineEntityRenderState.headDownAnimationProgress > 0.0F) {
         this.head.pitch = MathHelper.lerpAngleDegrees(felineEntityRenderState.headDownAnimationProgress, this.head.pitch, -0.58177644F);
      }

   }
}
