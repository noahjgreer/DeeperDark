/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Dilation
 *  net.minecraft.client.model.ModelData
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.model.ModelPartBuilder
 *  net.minecraft.client.model.ModelPartData
 *  net.minecraft.client.model.ModelTransform
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.entity.model.ArmPosing
 *  net.minecraft.client.render.entity.model.BabyModelTransformer
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 *  net.minecraft.client.render.entity.model.BipedEntityModel$1
 *  net.minecraft.client.render.entity.model.BipedEntityModel$ArmPose
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EquipmentModelData
 *  net.minecraft.client.render.entity.model.ModelTransformer
 *  net.minecraft.client.render.entity.model.ModelWithArms
 *  net.minecraft.client.render.entity.model.ModelWithHead
 *  net.minecraft.client.render.entity.state.BipedEntityRenderState
 *  net.minecraft.client.render.entity.state.Lancing
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.Arm
 *  net.minecraft.util.Hand
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.Easing
 *  net.minecraft.util.math.MathHelper
 */
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
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.entity.model.ArmPosing;
import net.minecraft.client.render.entity.model.BabyModelTransformer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EquipmentModelData;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.Lancing;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Easing;
import net.minecraft.util.math.MathHelper;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class BipedEntityModel<T extends BipedEntityRenderState>
extends EntityModel<T>
implements ModelWithArms<T>,
ModelWithHead {
    public static final ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(true, 16.0f, 0.0f, 2.0f, 2.0f, 24.0f, Set.of("head"));
    public static final float field_32505 = 0.25f;
    public static final float field_32506 = 0.5f;
    public static final float field_42513 = -0.1f;
    private static final float field_42512 = 0.005f;
    private static final float SPYGLASS_ARM_YAW_OFFSET = 0.2617994f;
    private static final float SPYGLASS_ARM_PITCH_OFFSET = 1.9198622f;
    private static final float SPYGLASS_SNEAKING_ARM_PITCH_OFFSET = 0.2617994f;
    private static final float field_46576 = -1.3962634f;
    private static final float field_46577 = 0.43633232f;
    private static final float field_46724 = 0.5235988f;
    public static final float field_39069 = 1.4835298f;
    public static final float field_39070 = 0.5235988f;
    public final ModelPart head;
    public final ModelPart hat;
    public final ModelPart body;
    public final ModelPart rightArm;
    public final ModelPart leftArm;
    public final ModelPart rightLeg;
    public final ModelPart leftLeg;

    public BipedEntityModel(ModelPart modelPart) {
        this(modelPart, RenderLayers::entityCutoutNoCull);
    }

    public BipedEntityModel(ModelPart modelPart, Function<Identifier, RenderLayer> function) {
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
        ModelPartData modelPartData2 = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, dilation), ModelTransform.origin((float)0.0f, (float)(0.0f + pivotOffsetY), (float)0.0f));
        modelPartData2.addChild("hat", ModelPartBuilder.create().uv(32, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, dilation.add(0.5f)), ModelTransform.NONE);
        modelPartData.addChild("body", ModelPartBuilder.create().uv(16, 16).cuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, dilation), ModelTransform.origin((float)0.0f, (float)(0.0f + pivotOffsetY), (float)0.0f));
        modelPartData.addChild("right_arm", ModelPartBuilder.create().uv(40, 16).cuboid(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation), ModelTransform.origin((float)-5.0f, (float)(2.0f + pivotOffsetY), (float)0.0f));
        modelPartData.addChild("left_arm", ModelPartBuilder.create().uv(40, 16).mirrored().cuboid(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation), ModelTransform.origin((float)5.0f, (float)(2.0f + pivotOffsetY), (float)0.0f));
        modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(0, 16).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation), ModelTransform.origin((float)-1.9f, (float)(12.0f + pivotOffsetY), (float)0.0f));
        modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(0, 16).mirrored().cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation), ModelTransform.origin((float)1.9f, (float)(12.0f + pivotOffsetY), (float)0.0f));
        return modelData;
    }

    public static EquipmentModelData<ModelData> createEquipmentModelData(Dilation hatDilation, Dilation armorDilation) {
        return BipedEntityModel.createEquipmentModelData(BipedEntityModel::createEquipmentModelData, (Dilation)hatDilation, (Dilation)armorDilation);
    }

    protected static EquipmentModelData<ModelData> createEquipmentModelData(Function<Dilation, ModelData> toModelData, Dilation hatDilation, Dilation armorDilation) {
        ModelData modelData = toModelData.apply(armorDilation);
        modelData.getRoot().resetChildrenExcept(Set.of("head"));
        ModelData modelData2 = toModelData.apply(armorDilation);
        modelData2.getRoot().resetChildrenExceptExact(Set.of("body", "left_arm", "right_arm"));
        ModelData modelData3 = toModelData.apply(hatDilation);
        modelData3.getRoot().resetChildrenExceptExact(Set.of("left_leg", "right_leg", "body"));
        ModelData modelData4 = toModelData.apply(armorDilation);
        modelData4.getRoot().resetChildrenExceptExact(Set.of("left_leg", "right_leg"));
        return new EquipmentModelData((Object)modelData, (Object)modelData2, (Object)modelData3, (Object)modelData4);
    }

    private static ModelData createEquipmentModelData(Dilation dilation) {
        ModelData modelData = BipedEntityModel.getModelData((Dilation)dilation, (float)0.0f);
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(0, 16).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation.add(-0.1f)), ModelTransform.origin((float)-1.9f, (float)12.0f, (float)0.0f));
        modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(0, 16).mirrored().cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation.add(-0.1f)), ModelTransform.origin((float)1.9f, (float)12.0f, (float)0.0f));
        return modelData;
    }

    public void setAngles(T bipedEntityRenderState) {
        boolean bl2;
        super.setAngles(bipedEntityRenderState);
        ArmPose armPose = ((BipedEntityRenderState)bipedEntityRenderState).leftArmPose;
        ArmPose armPose2 = ((BipedEntityRenderState)bipedEntityRenderState).rightArmPose;
        float f = ((BipedEntityRenderState)bipedEntityRenderState).leaningPitch;
        boolean bl = ((BipedEntityRenderState)bipedEntityRenderState).isGliding;
        this.head.pitch = ((BipedEntityRenderState)bipedEntityRenderState).pitch * ((float)Math.PI / 180);
        this.head.yaw = ((BipedEntityRenderState)bipedEntityRenderState).relativeHeadYaw * ((float)Math.PI / 180);
        if (bl) {
            this.head.pitch = -0.7853982f;
        } else if (f > 0.0f) {
            this.head.pitch = MathHelper.lerpAngleRadians((float)f, (float)this.head.pitch, (float)-0.7853982f);
        }
        float g = ((BipedEntityRenderState)bipedEntityRenderState).limbSwingAnimationProgress;
        float h = ((BipedEntityRenderState)bipedEntityRenderState).limbSwingAmplitude;
        this.rightArm.pitch = MathHelper.cos((double)(g * 0.6662f + (float)Math.PI)) * 2.0f * h * 0.5f / ((BipedEntityRenderState)bipedEntityRenderState).limbAmplitudeInverse;
        this.leftArm.pitch = MathHelper.cos((double)(g * 0.6662f)) * 2.0f * h * 0.5f / ((BipedEntityRenderState)bipedEntityRenderState).limbAmplitudeInverse;
        this.rightLeg.pitch = MathHelper.cos((double)(g * 0.6662f)) * 1.4f * h / ((BipedEntityRenderState)bipedEntityRenderState).limbAmplitudeInverse;
        this.leftLeg.pitch = MathHelper.cos((double)(g * 0.6662f + (float)Math.PI)) * 1.4f * h / ((BipedEntityRenderState)bipedEntityRenderState).limbAmplitudeInverse;
        this.rightLeg.yaw = 0.005f;
        this.leftLeg.yaw = -0.005f;
        this.rightLeg.roll = 0.005f;
        this.leftLeg.roll = -0.005f;
        if (((BipedEntityRenderState)bipedEntityRenderState).hasVehicle) {
            this.rightArm.pitch += -0.62831855f;
            this.leftArm.pitch += -0.62831855f;
            this.rightLeg.pitch = -1.4137167f;
            this.rightLeg.yaw = 0.31415927f;
            this.rightLeg.roll = 0.07853982f;
            this.leftLeg.pitch = -1.4137167f;
            this.leftLeg.yaw = -0.31415927f;
            this.leftLeg.roll = -0.07853982f;
        }
        boolean bl3 = bl2 = ((BipedEntityRenderState)bipedEntityRenderState).mainArm == Arm.RIGHT;
        if (((BipedEntityRenderState)bipedEntityRenderState).isUsingItem) {
            boolean bl4 = bl3 = ((BipedEntityRenderState)bipedEntityRenderState).activeHand == Hand.MAIN_HAND;
            if (bl3 == bl2) {
                this.positionRightArm(bipedEntityRenderState);
                if (!((BipedEntityRenderState)bipedEntityRenderState).rightArmPose.method_76639()) {
                    this.positionLeftArm(bipedEntityRenderState);
                }
            } else {
                this.positionLeftArm(bipedEntityRenderState);
                if (!((BipedEntityRenderState)bipedEntityRenderState).leftArmPose.method_76639()) {
                    this.positionRightArm(bipedEntityRenderState);
                }
            }
        } else {
            boolean bl5 = bl3 = bl2 ? armPose.isTwoHanded() : armPose2.isTwoHanded();
            if (bl2 != bl3) {
                this.positionLeftArm(bipedEntityRenderState);
                if (!((BipedEntityRenderState)bipedEntityRenderState).leftArmPose.method_76639()) {
                    this.positionRightArm(bipedEntityRenderState);
                }
            } else {
                this.positionRightArm(bipedEntityRenderState);
                if (!((BipedEntityRenderState)bipedEntityRenderState).rightArmPose.method_76639()) {
                    this.positionLeftArm(bipedEntityRenderState);
                }
            }
        }
        this.animateArms(bipedEntityRenderState);
        if (((BipedEntityRenderState)bipedEntityRenderState).isInSneakingPose) {
            this.body.pitch = 0.5f;
            this.rightArm.pitch += 0.4f;
            this.leftArm.pitch += 0.4f;
            this.rightLeg.originZ += 4.0f;
            this.leftLeg.originZ += 4.0f;
            this.head.originY += 4.2f;
            this.body.originY += 3.2f;
            this.leftArm.originY += 3.2f;
            this.rightArm.originY += 3.2f;
        }
        if (armPose2 != ArmPose.SPYGLASS) {
            ArmPosing.swingArm((ModelPart)this.rightArm, (float)((BipedEntityRenderState)bipedEntityRenderState).age, (float)1.0f);
        }
        if (armPose != ArmPose.SPYGLASS) {
            ArmPosing.swingArm((ModelPart)this.leftArm, (float)((BipedEntityRenderState)bipedEntityRenderState).age, (float)-1.0f);
        }
        if (f > 0.0f) {
            float l;
            float k;
            float i = g % 26.0f;
            Arm arm = ((BipedEntityRenderState)bipedEntityRenderState).preferredArm;
            float j = ((BipedEntityRenderState)bipedEntityRenderState).rightArmPose == ArmPose.SPEAR || arm == Arm.RIGHT && ((BipedEntityRenderState)bipedEntityRenderState).handSwingProgress > 0.0f ? 0.0f : f;
            float f2 = k = ((BipedEntityRenderState)bipedEntityRenderState).leftArmPose == ArmPose.SPEAR || arm == Arm.LEFT && ((BipedEntityRenderState)bipedEntityRenderState).handSwingProgress > 0.0f ? 0.0f : f;
            if (!((BipedEntityRenderState)bipedEntityRenderState).isUsingItem) {
                if (i < 14.0f) {
                    this.leftArm.pitch = MathHelper.lerpAngleRadians((float)k, (float)this.leftArm.pitch, (float)0.0f);
                    this.rightArm.pitch = MathHelper.lerp((float)j, (float)this.rightArm.pitch, (float)0.0f);
                    this.leftArm.yaw = MathHelper.lerpAngleRadians((float)k, (float)this.leftArm.yaw, (float)((float)Math.PI));
                    this.rightArm.yaw = MathHelper.lerp((float)j, (float)this.rightArm.yaw, (float)((float)Math.PI));
                    this.leftArm.roll = MathHelper.lerpAngleRadians((float)k, (float)this.leftArm.roll, (float)((float)Math.PI + 1.8707964f * this.method_2807(i) / this.method_2807(14.0f)));
                    this.rightArm.roll = MathHelper.lerp((float)j, (float)this.rightArm.roll, (float)((float)Math.PI - 1.8707964f * this.method_2807(i) / this.method_2807(14.0f)));
                } else if (i >= 14.0f && i < 22.0f) {
                    l = (i - 14.0f) / 8.0f;
                    this.leftArm.pitch = MathHelper.lerpAngleRadians((float)k, (float)this.leftArm.pitch, (float)(1.5707964f * l));
                    this.rightArm.pitch = MathHelper.lerp((float)j, (float)this.rightArm.pitch, (float)(1.5707964f * l));
                    this.leftArm.yaw = MathHelper.lerpAngleRadians((float)k, (float)this.leftArm.yaw, (float)((float)Math.PI));
                    this.rightArm.yaw = MathHelper.lerp((float)j, (float)this.rightArm.yaw, (float)((float)Math.PI));
                    this.leftArm.roll = MathHelper.lerpAngleRadians((float)k, (float)this.leftArm.roll, (float)(5.012389f - 1.8707964f * l));
                    this.rightArm.roll = MathHelper.lerp((float)j, (float)this.rightArm.roll, (float)(1.2707963f + 1.8707964f * l));
                } else if (i >= 22.0f && i < 26.0f) {
                    l = (i - 22.0f) / 4.0f;
                    this.leftArm.pitch = MathHelper.lerpAngleRadians((float)k, (float)this.leftArm.pitch, (float)(1.5707964f - 1.5707964f * l));
                    this.rightArm.pitch = MathHelper.lerp((float)j, (float)this.rightArm.pitch, (float)(1.5707964f - 1.5707964f * l));
                    this.leftArm.yaw = MathHelper.lerpAngleRadians((float)k, (float)this.leftArm.yaw, (float)((float)Math.PI));
                    this.rightArm.yaw = MathHelper.lerp((float)j, (float)this.rightArm.yaw, (float)((float)Math.PI));
                    this.leftArm.roll = MathHelper.lerpAngleRadians((float)k, (float)this.leftArm.roll, (float)((float)Math.PI));
                    this.rightArm.roll = MathHelper.lerp((float)j, (float)this.rightArm.roll, (float)((float)Math.PI));
                }
            }
            l = 0.3f;
            float m = 0.33333334f;
            this.leftLeg.pitch = MathHelper.lerp((float)f, (float)this.leftLeg.pitch, (float)(0.3f * MathHelper.cos((double)(g * 0.33333334f + (float)Math.PI))));
            this.rightLeg.pitch = MathHelper.lerp((float)f, (float)this.rightLeg.pitch, (float)(0.3f * MathHelper.cos((double)(g * 0.33333334f))));
        }
    }

    private void positionRightArm(T state) {
        switch (((BipedEntityRenderState)state).rightArmPose.ordinal()) {
            case 0: {
                this.rightArm.yaw = 0.0f;
                break;
            }
            case 2: {
                this.positionBlockingArm(this.rightArm, true);
                break;
            }
            case 1: {
                this.rightArm.pitch = this.rightArm.pitch * 0.5f - 0.31415927f;
                this.rightArm.yaw = 0.0f;
                break;
            }
            case 4: {
                this.rightArm.pitch = this.rightArm.pitch * 0.5f - (float)Math.PI;
                this.rightArm.yaw = 0.0f;
                break;
            }
            case 10: {
                Lancing.positionArmForSpear((ModelPart)this.rightArm, (ModelPart)this.head, (boolean)true, (ItemStack)state.getItemStackForArm(Arm.RIGHT), state);
                break;
            }
            case 3: {
                this.rightArm.yaw = -0.1f + this.head.yaw;
                this.leftArm.yaw = 0.1f + this.head.yaw + 0.4f;
                this.rightArm.pitch = -1.5707964f + this.head.pitch;
                this.leftArm.pitch = -1.5707964f + this.head.pitch;
                break;
            }
            case 5: {
                ArmPosing.charge((ModelPart)this.rightArm, (ModelPart)this.leftArm, (float)((BipedEntityRenderState)state).crossbowPullTime, (float)((BipedEntityRenderState)state).itemUseTime, (boolean)true);
                break;
            }
            case 6: {
                ArmPosing.hold((ModelPart)this.rightArm, (ModelPart)this.leftArm, (ModelPart)this.head, (boolean)true);
                break;
            }
            case 9: {
                this.rightArm.pitch = this.rightArm.pitch * 0.5f - 0.62831855f;
                this.rightArm.yaw = 0.0f;
                break;
            }
            case 7: {
                this.rightArm.pitch = MathHelper.clamp((float)(this.head.pitch - 1.9198622f - (((BipedEntityRenderState)state).isInSneakingPose ? 0.2617994f : 0.0f)), (float)-2.4f, (float)3.3f);
                this.rightArm.yaw = this.head.yaw - 0.2617994f;
                break;
            }
            case 8: {
                this.rightArm.pitch = MathHelper.clamp((float)this.head.pitch, (float)-1.2f, (float)1.2f) - 1.4835298f;
                this.rightArm.yaw = this.head.yaw - 0.5235988f;
            }
        }
    }

    private void positionLeftArm(T state) {
        switch (((BipedEntityRenderState)state).leftArmPose.ordinal()) {
            case 0: {
                this.leftArm.yaw = 0.0f;
                break;
            }
            case 2: {
                this.positionBlockingArm(this.leftArm, false);
                break;
            }
            case 1: {
                this.leftArm.pitch = this.leftArm.pitch * 0.5f - 0.31415927f;
                this.leftArm.yaw = 0.0f;
                break;
            }
            case 4: {
                this.leftArm.pitch = this.leftArm.pitch * 0.5f - (float)Math.PI;
                this.leftArm.yaw = 0.0f;
                break;
            }
            case 10: {
                Lancing.positionArmForSpear((ModelPart)this.leftArm, (ModelPart)this.head, (boolean)false, (ItemStack)state.getItemStackForArm(Arm.LEFT), state);
                break;
            }
            case 3: {
                this.rightArm.yaw = -0.1f + this.head.yaw - 0.4f;
                this.leftArm.yaw = 0.1f + this.head.yaw;
                this.rightArm.pitch = -1.5707964f + this.head.pitch;
                this.leftArm.pitch = -1.5707964f + this.head.pitch;
                break;
            }
            case 5: {
                ArmPosing.charge((ModelPart)this.rightArm, (ModelPart)this.leftArm, (float)((BipedEntityRenderState)state).crossbowPullTime, (float)((BipedEntityRenderState)state).itemUseTime, (boolean)false);
                break;
            }
            case 6: {
                ArmPosing.hold((ModelPart)this.rightArm, (ModelPart)this.leftArm, (ModelPart)this.head, (boolean)false);
                break;
            }
            case 9: {
                this.leftArm.pitch = this.leftArm.pitch * 0.5f - 0.62831855f;
                this.leftArm.yaw = 0.0f;
                break;
            }
            case 7: {
                this.leftArm.pitch = MathHelper.clamp((float)(this.head.pitch - 1.9198622f - (((BipedEntityRenderState)state).isInSneakingPose ? 0.2617994f : 0.0f)), (float)-2.4f, (float)3.3f);
                this.leftArm.yaw = this.head.yaw + 0.2617994f;
                break;
            }
            case 8: {
                this.leftArm.pitch = MathHelper.clamp((float)this.head.pitch, (float)-1.2f, (float)1.2f) - 1.4835298f;
                this.leftArm.yaw = this.head.yaw + 0.5235988f;
            }
        }
    }

    private void positionBlockingArm(ModelPart arm, boolean rightArm) {
        arm.pitch = arm.pitch * 0.5f - 0.9424779f + MathHelper.clamp((float)this.head.pitch, (float)-1.3962634f, (float)0.43633232f);
        arm.yaw = (rightArm ? -30.0f : 30.0f) * ((float)Math.PI / 180) + MathHelper.clamp((float)this.head.yaw, (float)-0.5235988f, (float)0.5235988f);
    }

    protected void animateArms(T state) {
        float f = ((BipedEntityRenderState)state).handSwingProgress;
        if (f <= 0.0f) {
            return;
        }
        this.body.yaw = MathHelper.sin((double)(MathHelper.sqrt((float)f) * ((float)Math.PI * 2))) * 0.2f;
        if (((BipedEntityRenderState)state).preferredArm == Arm.LEFT) {
            this.body.yaw *= -1.0f;
        }
        float g = ((BipedEntityRenderState)state).ageScale;
        this.rightArm.originZ = MathHelper.sin((double)this.body.yaw) * 5.0f * g;
        this.rightArm.originX = -MathHelper.cos((double)this.body.yaw) * 5.0f * g;
        this.leftArm.originZ = -MathHelper.sin((double)this.body.yaw) * 5.0f * g;
        this.leftArm.originX = MathHelper.cos((double)this.body.yaw) * 5.0f * g;
        this.rightArm.yaw += this.body.yaw;
        this.leftArm.yaw += this.body.yaw;
        this.leftArm.pitch += this.body.yaw;
        switch (1.field_63541[((BipedEntityRenderState)state).swingAnimationType.ordinal()]) {
            case 1: {
                float h = Easing.outQuart((float)f);
                float i = MathHelper.sin((double)(h * (float)Math.PI));
                float j = MathHelper.sin((double)(f * (float)Math.PI)) * -(this.head.pitch - 0.7f) * 0.75f;
                ModelPart modelPart = this.getArm(((BipedEntityRenderState)state).preferredArm);
                modelPart.pitch -= i * 1.2f + j;
                modelPart.yaw += this.body.yaw * 2.0f;
                modelPart.roll += MathHelper.sin((double)(f * (float)Math.PI)) * -0.4f;
                break;
            }
            case 2: {
                break;
            }
            case 3: {
                Lancing.method_75393((BipedEntityModel)this, state);
            }
        }
    }

    private float method_2807(float f) {
        return -65.0f * f + f * f;
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

    public void setArmAngle(BipedEntityRenderState bipedEntityRenderState, Arm arm, MatrixStack matrixStack) {
        this.root.applyTransform(matrixStack);
        this.getArm(arm).applyTransform(matrixStack);
    }

    public ModelPart getArm(Arm arm) {
        if (arm == Arm.LEFT) {
            return this.leftArm;
        }
        return this.rightArm;
    }

    public ModelPart getHead() {
        return this.head;
    }
}

