package net.minecraft.client.render.entity.model;

import java.util.Set;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BipedEntityModel extends EntityModel implements ModelWithArms, ModelWithHead {
   public static final ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(true, 16.0F, 0.0F, 2.0F, 2.0F, 24.0F, Set.of("head"));
   public static final float field_32505 = 0.25F;
   public static final float field_32506 = 0.5F;
   public static final float field_42513 = -0.1F;
   private static final float field_42512 = 0.005F;
   private static final float SPYGLASS_ARM_YAW_OFFSET = 0.2617994F;
   private static final float SPYGLASS_ARM_PITCH_OFFSET = 1.9198622F;
   private static final float SPYGLASS_SNEAKING_ARM_PITCH_OFFSET = 0.2617994F;
   private static final float field_46576 = -1.3962634F;
   private static final float field_46577 = 0.43633232F;
   private static final float field_46724 = 0.5235988F;
   public static final float field_39069 = 1.4835298F;
   public static final float field_39070 = 0.5235988F;
   public final ModelPart head;
   public final ModelPart hat;
   public final ModelPart body;
   public final ModelPart rightArm;
   public final ModelPart leftArm;
   public final ModelPart rightLeg;
   public final ModelPart leftLeg;

   public BipedEntityModel(ModelPart modelPart) {
      this(modelPart, RenderLayer::getEntityCutoutNoCull);
   }

   public BipedEntityModel(ModelPart modelPart, Function function) {
      super(modelPart, function);
      this.head = modelPart.getChild("head");
      this.hat = this.head.getChild("hat");
      this.body = modelPart.getChild("body");
      this.rightArm = modelPart.getChild("right_arm");
      this.leftArm = modelPart.getChild("left_arm");
      this.rightLeg = modelPart.getChild("right_leg");
      this.leftLeg = modelPart.getChild("left_leg");
   }

   public static ModelData getModelData(Dilation dilation, float pivotOffsetY) {
      ModelData modelData = new ModelData();
      ModelPartData modelPartData = modelData.getRoot();
      ModelPartData modelPartData2 = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, dilation), ModelTransform.origin(0.0F, 0.0F + pivotOffsetY, 0.0F));
      modelPartData2.addChild("hat", ModelPartBuilder.create().uv(32, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, dilation.add(0.5F)), ModelTransform.NONE);
      modelPartData.addChild("body", ModelPartBuilder.create().uv(16, 16).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, dilation), ModelTransform.origin(0.0F, 0.0F + pivotOffsetY, 0.0F));
      modelPartData.addChild("right_arm", ModelPartBuilder.create().uv(40, 16).cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation), ModelTransform.origin(-5.0F, 2.0F + pivotOffsetY, 0.0F));
      modelPartData.addChild("left_arm", ModelPartBuilder.create().uv(40, 16).mirrored().cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation), ModelTransform.origin(5.0F, 2.0F + pivotOffsetY, 0.0F));
      modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation), ModelTransform.origin(-1.9F, 12.0F + pivotOffsetY, 0.0F));
      modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(0, 16).mirrored().cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation), ModelTransform.origin(1.9F, 12.0F + pivotOffsetY, 0.0F));
      return modelData;
   }

   public void setAngles(BipedEntityRenderState bipedEntityRenderState) {
      super.setAngles(bipedEntityRenderState);
      ArmPose armPose = bipedEntityRenderState.leftArmPose;
      ArmPose armPose2 = bipedEntityRenderState.rightArmPose;
      float f = bipedEntityRenderState.leaningPitch;
      boolean bl = bipedEntityRenderState.isGliding;
      this.head.pitch = bipedEntityRenderState.pitch * 0.017453292F;
      this.head.yaw = bipedEntityRenderState.relativeHeadYaw * 0.017453292F;
      if (bl) {
         this.head.pitch = -0.7853982F;
      } else if (f > 0.0F) {
         this.head.pitch = MathHelper.lerpAngleRadians(f, this.head.pitch, -0.7853982F);
      }

      float g = bipedEntityRenderState.limbSwingAnimationProgress;
      float h = bipedEntityRenderState.limbSwingAmplitude;
      this.rightArm.pitch = MathHelper.cos(g * 0.6662F + 3.1415927F) * 2.0F * h * 0.5F / bipedEntityRenderState.limbAmplitudeInverse;
      this.leftArm.pitch = MathHelper.cos(g * 0.6662F) * 2.0F * h * 0.5F / bipedEntityRenderState.limbAmplitudeInverse;
      this.rightLeg.pitch = MathHelper.cos(g * 0.6662F) * 1.4F * h / bipedEntityRenderState.limbAmplitudeInverse;
      this.leftLeg.pitch = MathHelper.cos(g * 0.6662F + 3.1415927F) * 1.4F * h / bipedEntityRenderState.limbAmplitudeInverse;
      this.rightLeg.yaw = 0.005F;
      this.leftLeg.yaw = -0.005F;
      this.rightLeg.roll = 0.005F;
      this.leftLeg.roll = -0.005F;
      ModelPart var10000;
      if (bipedEntityRenderState.hasVehicle) {
         var10000 = this.rightArm;
         var10000.pitch += -0.62831855F;
         var10000 = this.leftArm;
         var10000.pitch += -0.62831855F;
         this.rightLeg.pitch = -1.4137167F;
         this.rightLeg.yaw = 0.31415927F;
         this.rightLeg.roll = 0.07853982F;
         this.leftLeg.pitch = -1.4137167F;
         this.leftLeg.yaw = -0.31415927F;
         this.leftLeg.roll = -0.07853982F;
      }

      boolean bl2 = bipedEntityRenderState.mainArm == Arm.RIGHT;
      boolean bl3;
      if (bipedEntityRenderState.isUsingItem) {
         bl3 = bipedEntityRenderState.activeHand == Hand.MAIN_HAND;
         if (bl3 == bl2) {
            this.positionRightArm(bipedEntityRenderState, armPose2);
         } else {
            this.positionLeftArm(bipedEntityRenderState, armPose);
         }
      } else {
         bl3 = bl2 ? armPose.isTwoHanded() : armPose2.isTwoHanded();
         if (bl2 != bl3) {
            this.positionLeftArm(bipedEntityRenderState, armPose);
            this.positionRightArm(bipedEntityRenderState, armPose2);
         } else {
            this.positionRightArm(bipedEntityRenderState, armPose2);
            this.positionLeftArm(bipedEntityRenderState, armPose);
         }
      }

      this.animateArms(bipedEntityRenderState, bipedEntityRenderState.age);
      if (bipedEntityRenderState.isInSneakingPose) {
         this.body.pitch = 0.5F;
         var10000 = this.rightArm;
         var10000.pitch += 0.4F;
         var10000 = this.leftArm;
         var10000.pitch += 0.4F;
         var10000 = this.rightLeg;
         var10000.originZ += 4.0F;
         var10000 = this.leftLeg;
         var10000.originZ += 4.0F;
         var10000 = this.head;
         var10000.originY += 4.2F;
         var10000 = this.body;
         var10000.originY += 3.2F;
         var10000 = this.leftArm;
         var10000.originY += 3.2F;
         var10000 = this.rightArm;
         var10000.originY += 3.2F;
      }

      if (armPose2 != BipedEntityModel.ArmPose.SPYGLASS) {
         ArmPosing.swingArm(this.rightArm, bipedEntityRenderState.age, 1.0F);
      }

      if (armPose != BipedEntityModel.ArmPose.SPYGLASS) {
         ArmPosing.swingArm(this.leftArm, bipedEntityRenderState.age, -1.0F);
      }

      if (f > 0.0F) {
         float i = g % 26.0F;
         Arm arm = bipedEntityRenderState.preferredArm;
         float j = arm == Arm.RIGHT && bipedEntityRenderState.handSwingProgress > 0.0F ? 0.0F : f;
         float k = arm == Arm.LEFT && bipedEntityRenderState.handSwingProgress > 0.0F ? 0.0F : f;
         float l;
         if (!bipedEntityRenderState.isUsingItem) {
            if (i < 14.0F) {
               this.leftArm.pitch = MathHelper.lerpAngleRadians(k, this.leftArm.pitch, 0.0F);
               this.rightArm.pitch = MathHelper.lerp(j, this.rightArm.pitch, 0.0F);
               this.leftArm.yaw = MathHelper.lerpAngleRadians(k, this.leftArm.yaw, 3.1415927F);
               this.rightArm.yaw = MathHelper.lerp(j, this.rightArm.yaw, 3.1415927F);
               this.leftArm.roll = MathHelper.lerpAngleRadians(k, this.leftArm.roll, 3.1415927F + 1.8707964F * this.method_2807(i) / this.method_2807(14.0F));
               this.rightArm.roll = MathHelper.lerp(j, this.rightArm.roll, 3.1415927F - 1.8707964F * this.method_2807(i) / this.method_2807(14.0F));
            } else if (i >= 14.0F && i < 22.0F) {
               l = (i - 14.0F) / 8.0F;
               this.leftArm.pitch = MathHelper.lerpAngleRadians(k, this.leftArm.pitch, 1.5707964F * l);
               this.rightArm.pitch = MathHelper.lerp(j, this.rightArm.pitch, 1.5707964F * l);
               this.leftArm.yaw = MathHelper.lerpAngleRadians(k, this.leftArm.yaw, 3.1415927F);
               this.rightArm.yaw = MathHelper.lerp(j, this.rightArm.yaw, 3.1415927F);
               this.leftArm.roll = MathHelper.lerpAngleRadians(k, this.leftArm.roll, 5.012389F - 1.8707964F * l);
               this.rightArm.roll = MathHelper.lerp(j, this.rightArm.roll, 1.2707963F + 1.8707964F * l);
            } else if (i >= 22.0F && i < 26.0F) {
               l = (i - 22.0F) / 4.0F;
               this.leftArm.pitch = MathHelper.lerpAngleRadians(k, this.leftArm.pitch, 1.5707964F - 1.5707964F * l);
               this.rightArm.pitch = MathHelper.lerp(j, this.rightArm.pitch, 1.5707964F - 1.5707964F * l);
               this.leftArm.yaw = MathHelper.lerpAngleRadians(k, this.leftArm.yaw, 3.1415927F);
               this.rightArm.yaw = MathHelper.lerp(j, this.rightArm.yaw, 3.1415927F);
               this.leftArm.roll = MathHelper.lerpAngleRadians(k, this.leftArm.roll, 3.1415927F);
               this.rightArm.roll = MathHelper.lerp(j, this.rightArm.roll, 3.1415927F);
            }
         }

         l = 0.3F;
         float m = 0.33333334F;
         this.leftLeg.pitch = MathHelper.lerp(f, this.leftLeg.pitch, 0.3F * MathHelper.cos(g * 0.33333334F + 3.1415927F));
         this.rightLeg.pitch = MathHelper.lerp(f, this.rightLeg.pitch, 0.3F * MathHelper.cos(g * 0.33333334F));
      }

   }

   private void positionRightArm(BipedEntityRenderState state, ArmPose armPose) {
      switch (armPose.ordinal()) {
         case 0:
            this.rightArm.yaw = 0.0F;
            break;
         case 1:
            this.rightArm.pitch = this.rightArm.pitch * 0.5F - 0.31415927F;
            this.rightArm.yaw = 0.0F;
            break;
         case 2:
            this.positionBlockingArm(this.rightArm, true);
            break;
         case 3:
            this.rightArm.yaw = -0.1F + this.head.yaw;
            this.leftArm.yaw = 0.1F + this.head.yaw + 0.4F;
            this.rightArm.pitch = -1.5707964F + this.head.pitch;
            this.leftArm.pitch = -1.5707964F + this.head.pitch;
            break;
         case 4:
            this.rightArm.pitch = this.rightArm.pitch * 0.5F - 3.1415927F;
            this.rightArm.yaw = 0.0F;
            break;
         case 5:
            ArmPosing.charge(this.rightArm, this.leftArm, state.crossbowPullTime, state.itemUseTime, true);
            break;
         case 6:
            ArmPosing.hold(this.rightArm, this.leftArm, this.head, true);
            break;
         case 7:
            this.rightArm.pitch = MathHelper.clamp(this.head.pitch - 1.9198622F - (state.isInSneakingPose ? 0.2617994F : 0.0F), -2.4F, 3.3F);
            this.rightArm.yaw = this.head.yaw - 0.2617994F;
            break;
         case 8:
            this.rightArm.pitch = MathHelper.clamp(this.head.pitch, -1.2F, 1.2F) - 1.4835298F;
            this.rightArm.yaw = this.head.yaw - 0.5235988F;
            break;
         case 9:
            this.rightArm.pitch = this.rightArm.pitch * 0.5F - 0.62831855F;
            this.rightArm.yaw = 0.0F;
      }

   }

   private void positionLeftArm(BipedEntityRenderState state, ArmPose armPose) {
      switch (armPose.ordinal()) {
         case 0:
            this.leftArm.yaw = 0.0F;
            break;
         case 1:
            this.leftArm.pitch = this.leftArm.pitch * 0.5F - 0.31415927F;
            this.leftArm.yaw = 0.0F;
            break;
         case 2:
            this.positionBlockingArm(this.leftArm, false);
            break;
         case 3:
            this.rightArm.yaw = -0.1F + this.head.yaw - 0.4F;
            this.leftArm.yaw = 0.1F + this.head.yaw;
            this.rightArm.pitch = -1.5707964F + this.head.pitch;
            this.leftArm.pitch = -1.5707964F + this.head.pitch;
            break;
         case 4:
            this.leftArm.pitch = this.leftArm.pitch * 0.5F - 3.1415927F;
            this.leftArm.yaw = 0.0F;
            break;
         case 5:
            ArmPosing.charge(this.rightArm, this.leftArm, state.crossbowPullTime, state.itemUseTime, false);
            break;
         case 6:
            ArmPosing.hold(this.rightArm, this.leftArm, this.head, false);
            break;
         case 7:
            this.leftArm.pitch = MathHelper.clamp(this.head.pitch - 1.9198622F - (state.isInSneakingPose ? 0.2617994F : 0.0F), -2.4F, 3.3F);
            this.leftArm.yaw = this.head.yaw + 0.2617994F;
            break;
         case 8:
            this.leftArm.pitch = MathHelper.clamp(this.head.pitch, -1.2F, 1.2F) - 1.4835298F;
            this.leftArm.yaw = this.head.yaw + 0.5235988F;
            break;
         case 9:
            this.leftArm.pitch = this.leftArm.pitch * 0.5F - 0.62831855F;
            this.leftArm.yaw = 0.0F;
      }

   }

   private void positionBlockingArm(ModelPart arm, boolean rightArm) {
      arm.pitch = arm.pitch * 0.5F - 0.9424779F + MathHelper.clamp(this.head.pitch, -1.3962634F, 0.43633232F);
      arm.yaw = (rightArm ? -30.0F : 30.0F) * 0.017453292F + MathHelper.clamp(this.head.yaw, -0.5235988F, 0.5235988F);
   }

   protected void animateArms(BipedEntityRenderState state, float animationProgress) {
      float f = state.handSwingProgress;
      if (!(f <= 0.0F)) {
         Arm arm = state.preferredArm;
         ModelPart modelPart = this.getArm(arm);
         this.body.yaw = MathHelper.sin(MathHelper.sqrt(f) * 6.2831855F) * 0.2F;
         ModelPart var10000;
         if (arm == Arm.LEFT) {
            var10000 = this.body;
            var10000.yaw *= -1.0F;
         }

         float h = state.ageScale;
         this.rightArm.originZ = MathHelper.sin(this.body.yaw) * 5.0F * h;
         this.rightArm.originX = -MathHelper.cos(this.body.yaw) * 5.0F * h;
         this.leftArm.originZ = -MathHelper.sin(this.body.yaw) * 5.0F * h;
         this.leftArm.originX = MathHelper.cos(this.body.yaw) * 5.0F * h;
         var10000 = this.rightArm;
         var10000.yaw += this.body.yaw;
         var10000 = this.leftArm;
         var10000.yaw += this.body.yaw;
         var10000 = this.leftArm;
         var10000.pitch += this.body.yaw;
         float g = 1.0F - f;
         g *= g;
         g *= g;
         g = 1.0F - g;
         float i = MathHelper.sin(g * 3.1415927F);
         float j = MathHelper.sin(f * 3.1415927F) * -(this.head.pitch - 0.7F) * 0.75F;
         modelPart.pitch -= i * 1.2F + j;
         modelPart.yaw += this.body.yaw * 2.0F;
         modelPart.roll += MathHelper.sin(f * 3.1415927F) * -0.4F;
      }
   }

   private float method_2807(float f) {
      return -65.0F * f + f * f;
   }

   public void copyTransforms(BipedEntityModel model) {
      model.head.copyTransform(this.head);
      model.body.copyTransform(this.body);
      model.rightArm.copyTransform(this.rightArm);
      model.leftArm.copyTransform(this.leftArm);
      model.rightLeg.copyTransform(this.rightLeg);
      model.leftLeg.copyTransform(this.leftLeg);
   }

   public void setVisible(boolean visible) {
      this.head.visible = visible;
      this.hat.visible = visible;
      this.body.visible = visible;
      this.rightArm.visible = visible;
      this.leftArm.visible = visible;
      this.rightLeg.visible = visible;
      this.leftLeg.visible = visible;
   }

   public void setArmAngle(Arm arm, MatrixStack matrices) {
      this.root.applyTransform(matrices);
      this.getArm(arm).applyTransform(matrices);
   }

   protected ModelPart getArm(Arm arm) {
      return arm == Arm.LEFT ? this.leftArm : this.rightArm;
   }

   public ModelPart getHead() {
      return this.head;
   }

   @Environment(EnvType.CLIENT)
   public static enum ArmPose {
      EMPTY(false),
      ITEM(false),
      BLOCK(false),
      BOW_AND_ARROW(true),
      THROW_SPEAR(false),
      CROSSBOW_CHARGE(true),
      CROSSBOW_HOLD(true),
      SPYGLASS(false),
      TOOT_HORN(false),
      BRUSH(false);

      private final boolean twoHanded;

      private ArmPose(final boolean twoHanded) {
         this.twoHanded = twoHanded;
      }

      public boolean isTwoHanded() {
         return this.twoHanded;
      }

      // $FF: synthetic method
      private static ArmPose[] method_36892() {
         return new ArmPose[]{EMPTY, ITEM, BLOCK, BOW_AND_ARROW, THROW_SPEAR, CROSSBOW_CHARGE, CROSSBOW_HOLD, SPYGLASS, TOOT_HORN, BRUSH};
      }
   }
}
