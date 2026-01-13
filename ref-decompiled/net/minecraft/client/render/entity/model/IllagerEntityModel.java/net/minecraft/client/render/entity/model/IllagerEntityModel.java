/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
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
import net.minecraft.client.render.entity.model.ArmPosing;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.entity.state.IllagerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class IllagerEntityModel<S extends IllagerEntityRenderState>
extends EntityModel<S>
implements ModelWithArms<S>,
ModelWithHead {
    private final ModelPart head;
    private final ModelPart hat;
    private final ModelPart arms;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart rightArm;
    private final ModelPart leftArm;

    public IllagerEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.head = modelPart.getChild("head");
        this.hat = this.head.getChild("hat");
        this.hat.visible = false;
        this.arms = modelPart.getChild("arms");
        this.leftLeg = modelPart.getChild("left_leg");
        this.rightLeg = modelPart.getChild("right_leg");
        this.leftArm = modelPart.getChild("left_arm");
        this.rightArm = modelPart.getChild("right_arm");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f), ModelTransform.origin(0.0f, 0.0f, 0.0f));
        modelPartData2.addChild("hat", ModelPartBuilder.create().uv(32, 0).cuboid(-4.0f, -10.0f, -4.0f, 8.0f, 12.0f, 8.0f, new Dilation(0.45f)), ModelTransform.NONE);
        modelPartData2.addChild("nose", ModelPartBuilder.create().uv(24, 0).cuboid(-1.0f, -1.0f, -6.0f, 2.0f, 4.0f, 2.0f), ModelTransform.origin(0.0f, -2.0f, 0.0f));
        modelPartData.addChild("body", ModelPartBuilder.create().uv(16, 20).cuboid(-4.0f, 0.0f, -3.0f, 8.0f, 12.0f, 6.0f).uv(0, 38).cuboid(-4.0f, 0.0f, -3.0f, 8.0f, 20.0f, 6.0f, new Dilation(0.5f)), ModelTransform.origin(0.0f, 0.0f, 0.0f));
        ModelPartData modelPartData3 = modelPartData.addChild("arms", ModelPartBuilder.create().uv(44, 22).cuboid(-8.0f, -2.0f, -2.0f, 4.0f, 8.0f, 4.0f).uv(40, 38).cuboid(-4.0f, 2.0f, -2.0f, 8.0f, 4.0f, 4.0f), ModelTransform.of(0.0f, 3.0f, -1.0f, -0.75f, 0.0f, 0.0f));
        modelPartData3.addChild("left_shoulder", ModelPartBuilder.create().uv(44, 22).mirrored().cuboid(4.0f, -2.0f, -2.0f, 4.0f, 8.0f, 4.0f), ModelTransform.NONE);
        modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(0, 22).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f), ModelTransform.origin(-2.0f, 12.0f, 0.0f));
        modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(0, 22).mirrored().cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f), ModelTransform.origin(2.0f, 12.0f, 0.0f));
        modelPartData.addChild("right_arm", ModelPartBuilder.create().uv(40, 46).cuboid(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f), ModelTransform.origin(-5.0f, 2.0f, 0.0f));
        modelPartData.addChild("left_arm", ModelPartBuilder.create().uv(40, 46).mirrored().cuboid(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f), ModelTransform.origin(5.0f, 2.0f, 0.0f));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(S illagerEntityRenderState) {
        boolean bl;
        super.setAngles(illagerEntityRenderState);
        this.head.yaw = ((IllagerEntityRenderState)illagerEntityRenderState).relativeHeadYaw * ((float)Math.PI / 180);
        this.head.pitch = ((IllagerEntityRenderState)illagerEntityRenderState).pitch * ((float)Math.PI / 180);
        if (((IllagerEntityRenderState)illagerEntityRenderState).hasVehicle) {
            this.rightArm.pitch = -0.62831855f;
            this.rightArm.yaw = 0.0f;
            this.rightArm.roll = 0.0f;
            this.leftArm.pitch = -0.62831855f;
            this.leftArm.yaw = 0.0f;
            this.leftArm.roll = 0.0f;
            this.rightLeg.pitch = -1.4137167f;
            this.rightLeg.yaw = 0.31415927f;
            this.rightLeg.roll = 0.07853982f;
            this.leftLeg.pitch = -1.4137167f;
            this.leftLeg.yaw = -0.31415927f;
            this.leftLeg.roll = -0.07853982f;
        } else {
            float f = ((IllagerEntityRenderState)illagerEntityRenderState).limbSwingAmplitude;
            float g = ((IllagerEntityRenderState)illagerEntityRenderState).limbSwingAnimationProgress;
            this.rightArm.pitch = MathHelper.cos(g * 0.6662f + (float)Math.PI) * 2.0f * f * 0.5f;
            this.rightArm.yaw = 0.0f;
            this.rightArm.roll = 0.0f;
            this.leftArm.pitch = MathHelper.cos(g * 0.6662f) * 2.0f * f * 0.5f;
            this.leftArm.yaw = 0.0f;
            this.leftArm.roll = 0.0f;
            this.rightLeg.pitch = MathHelper.cos(g * 0.6662f) * 1.4f * f * 0.5f;
            this.rightLeg.yaw = 0.0f;
            this.rightLeg.roll = 0.0f;
            this.leftLeg.pitch = MathHelper.cos(g * 0.6662f + (float)Math.PI) * 1.4f * f * 0.5f;
            this.leftLeg.yaw = 0.0f;
            this.leftLeg.roll = 0.0f;
        }
        IllagerEntity.State state = ((IllagerEntityRenderState)illagerEntityRenderState).illagerState;
        if (state == IllagerEntity.State.ATTACKING) {
            if (((ArmedEntityRenderState)illagerEntityRenderState).getMainHandItemState().isEmpty()) {
                ArmPosing.zombieArms(this.leftArm, this.rightArm, true, illagerEntityRenderState);
            } else {
                ArmPosing.meleeAttack(this.rightArm, this.leftArm, ((IllagerEntityRenderState)illagerEntityRenderState).illagerMainArm, ((IllagerEntityRenderState)illagerEntityRenderState).handSwingProgress, ((IllagerEntityRenderState)illagerEntityRenderState).age);
            }
        } else if (state == IllagerEntity.State.SPELLCASTING) {
            this.rightArm.originZ = 0.0f;
            this.rightArm.originX = -5.0f;
            this.leftArm.originZ = 0.0f;
            this.leftArm.originX = 5.0f;
            this.rightArm.pitch = MathHelper.cos(((IllagerEntityRenderState)illagerEntityRenderState).age * 0.6662f) * 0.25f;
            this.leftArm.pitch = MathHelper.cos(((IllagerEntityRenderState)illagerEntityRenderState).age * 0.6662f) * 0.25f;
            this.rightArm.roll = 2.3561945f;
            this.leftArm.roll = -2.3561945f;
            this.rightArm.yaw = 0.0f;
            this.leftArm.yaw = 0.0f;
        } else if (state == IllagerEntity.State.BOW_AND_ARROW) {
            this.rightArm.yaw = -0.1f + this.head.yaw;
            this.rightArm.pitch = -1.5707964f + this.head.pitch;
            this.leftArm.pitch = -0.9424779f + this.head.pitch;
            this.leftArm.yaw = this.head.yaw - 0.4f;
            this.leftArm.roll = 1.5707964f;
        } else if (state == IllagerEntity.State.CROSSBOW_HOLD) {
            ArmPosing.hold(this.rightArm, this.leftArm, this.head, true);
        } else if (state == IllagerEntity.State.CROSSBOW_CHARGE) {
            ArmPosing.charge(this.rightArm, this.leftArm, ((IllagerEntityRenderState)illagerEntityRenderState).crossbowPullTime, ((IllagerEntityRenderState)illagerEntityRenderState).itemUseTime, true);
        } else if (state == IllagerEntity.State.CELEBRATING) {
            this.rightArm.originZ = 0.0f;
            this.rightArm.originX = -5.0f;
            this.rightArm.pitch = MathHelper.cos(((IllagerEntityRenderState)illagerEntityRenderState).age * 0.6662f) * 0.05f;
            this.rightArm.roll = 2.670354f;
            this.rightArm.yaw = 0.0f;
            this.leftArm.originZ = 0.0f;
            this.leftArm.originX = 5.0f;
            this.leftArm.pitch = MathHelper.cos(((IllagerEntityRenderState)illagerEntityRenderState).age * 0.6662f) * 0.05f;
            this.leftArm.roll = -2.3561945f;
            this.leftArm.yaw = 0.0f;
        }
        this.arms.visible = bl = state == IllagerEntity.State.CROSSED;
        this.leftArm.visible = !bl;
        this.rightArm.visible = !bl;
    }

    private ModelPart getAttackingArm(Arm arm) {
        if (arm == Arm.LEFT) {
            return this.leftArm;
        }
        return this.rightArm;
    }

    public ModelPart getHat() {
        return this.hat;
    }

    @Override
    public ModelPart getHead() {
        return this.head;
    }

    @Override
    public void setArmAngle(IllagerEntityRenderState illagerEntityRenderState, Arm arm, MatrixStack matrixStack) {
        this.root.applyTransform(matrixStack);
        this.getAttackingArm(arm).applyTransform(matrixStack);
    }
}
