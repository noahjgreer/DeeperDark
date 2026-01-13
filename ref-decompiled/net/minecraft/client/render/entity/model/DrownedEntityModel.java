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
 *  net.minecraft.client.model.TexturedModelData
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 *  net.minecraft.client.render.entity.model.BipedEntityModel$ArmPose
 *  net.minecraft.client.render.entity.model.DrownedEntityModel
 *  net.minecraft.client.render.entity.model.ZombieEntityModel
 *  net.minecraft.client.render.entity.state.ZombieEntityRenderState
 *  net.minecraft.util.math.MathHelper
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
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class DrownedEntityModel
extends ZombieEntityModel<ZombieEntityRenderState> {
    public DrownedEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static TexturedModelData getTexturedModelData(Dilation dilation) {
        ModelData modelData = BipedEntityModel.getModelData((Dilation)dilation, (float)0.0f);
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("left_arm", ModelPartBuilder.create().uv(32, 48).cuboid(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation), ModelTransform.origin((float)5.0f, (float)2.0f, (float)0.0f));
        modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(16, 48).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation), ModelTransform.origin((float)1.9f, (float)12.0f, (float)0.0f));
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)64);
    }

    public void setAngles(ZombieEntityRenderState zombieEntityRenderState) {
        float f;
        super.setAngles(zombieEntityRenderState);
        if (zombieEntityRenderState.leftArmPose == BipedEntityModel.ArmPose.THROW_TRIDENT) {
            this.leftArm.pitch = this.leftArm.pitch * 0.5f - (float)Math.PI;
            this.leftArm.yaw = 0.0f;
        }
        if (zombieEntityRenderState.rightArmPose == BipedEntityModel.ArmPose.THROW_TRIDENT) {
            this.rightArm.pitch = this.rightArm.pitch * 0.5f - (float)Math.PI;
            this.rightArm.yaw = 0.0f;
        }
        if ((f = zombieEntityRenderState.leaningPitch) > 0.0f) {
            this.rightArm.pitch = MathHelper.lerpAngleRadians((float)f, (float)this.rightArm.pitch, (float)-2.5132742f) + f * 0.35f * MathHelper.sin((double)(0.1f * zombieEntityRenderState.age));
            this.leftArm.pitch = MathHelper.lerpAngleRadians((float)f, (float)this.leftArm.pitch, (float)-2.5132742f) - f * 0.35f * MathHelper.sin((double)(0.1f * zombieEntityRenderState.age));
            this.rightArm.roll = MathHelper.lerpAngleRadians((float)f, (float)this.rightArm.roll, (float)-0.15f);
            this.leftArm.roll = MathHelper.lerpAngleRadians((float)f, (float)this.leftArm.roll, (float)0.15f);
            this.leftLeg.pitch -= f * 0.55f * MathHelper.sin((double)(0.1f * zombieEntityRenderState.age));
            this.rightLeg.pitch += f * 0.55f * MathHelper.sin((double)(0.1f * zombieEntityRenderState.age));
            this.head.pitch = 0.0f;
        }
    }
}

