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
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.state.LancerEntityRenderState;
import net.minecraft.util.Arm;
import net.minecraft.util.SwingAnimationType;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ArmPosing {
    public static void hold(ModelPart holdingArm, ModelPart otherArm, ModelPart head, boolean rightArm) {
        ModelPart modelPart = rightArm ? holdingArm : otherArm;
        ModelPart modelPart2 = rightArm ? otherArm : holdingArm;
        modelPart.yaw = (rightArm ? -0.3f : 0.3f) + head.yaw;
        modelPart2.yaw = (rightArm ? 0.6f : -0.6f) + head.yaw;
        modelPart.pitch = -1.5707964f + head.pitch + 0.1f;
        modelPart2.pitch = -1.5f + head.pitch;
    }

    public static void charge(ModelPart holdingArm, ModelPart pullingArm, float crossbowPullTime, float f, boolean rightArm) {
        ModelPart modelPart = rightArm ? holdingArm : pullingArm;
        ModelPart modelPart2 = rightArm ? pullingArm : holdingArm;
        modelPart.yaw = rightArm ? -0.8f : 0.8f;
        modelPart2.pitch = modelPart.pitch = -0.97079635f;
        float g = MathHelper.clamp(f, 0.0f, crossbowPullTime);
        float h = g / crossbowPullTime;
        modelPart2.yaw = MathHelper.lerp(h, 0.4f, 0.85f) * (float)(rightArm ? 1 : -1);
        modelPart2.pitch = MathHelper.lerp(h, modelPart2.pitch, -1.5707964f);
    }

    public static void meleeAttack(ModelPart rightArm, ModelPart leftArm, Arm mainArm, float swingProgress, float animationProgress) {
        float f = MathHelper.sin(swingProgress * (float)Math.PI);
        float g = MathHelper.sin((1.0f - (1.0f - swingProgress) * (1.0f - swingProgress)) * (float)Math.PI);
        rightArm.roll = 0.0f;
        leftArm.roll = 0.0f;
        rightArm.yaw = 0.15707964f;
        leftArm.yaw = -0.15707964f;
        if (mainArm == Arm.RIGHT) {
            rightArm.pitch = -1.8849558f + MathHelper.cos(animationProgress * 0.09f) * 0.15f;
            leftArm.pitch = -0.0f + MathHelper.cos(animationProgress * 0.19f) * 0.5f;
            rightArm.pitch += f * 2.2f - g * 0.4f;
            leftArm.pitch += f * 1.2f - g * 0.4f;
        } else {
            rightArm.pitch = -0.0f + MathHelper.cos(animationProgress * 0.19f) * 0.5f;
            leftArm.pitch = -1.8849558f + MathHelper.cos(animationProgress * 0.09f) * 0.15f;
            rightArm.pitch += f * 1.2f - g * 0.4f;
            leftArm.pitch += f * 2.2f - g * 0.4f;
        }
        ArmPosing.swingArms(rightArm, leftArm, animationProgress);
    }

    public static void swingArm(ModelPart arm, float animationProgress, float sigma) {
        arm.roll += sigma * (MathHelper.cos(animationProgress * 0.09f) * 0.05f + 0.05f);
        arm.pitch += sigma * (MathHelper.sin(animationProgress * 0.067f) * 0.05f);
    }

    public static void swingArms(ModelPart rightArm, ModelPart leftArm, float animationProgress) {
        ArmPosing.swingArm(rightArm, animationProgress, 1.0f);
        ArmPosing.swingArm(leftArm, animationProgress, -1.0f);
    }

    public static <T extends LancerEntityRenderState> void zombieArms(ModelPart leftArm, ModelPart rightArm, boolean attacking, T lancerEntityRenderState) {
        boolean bl;
        boolean bl2 = bl = lancerEntityRenderState.swingAnimationType != SwingAnimationType.STAB;
        if (bl) {
            float f = lancerEntityRenderState.handSwingProgress;
            float g = (float)(-Math.PI) / (attacking ? 1.5f : 2.25f);
            float h = MathHelper.sin(f * (float)Math.PI);
            float i = MathHelper.sin((1.0f - (1.0f - f) * (1.0f - f)) * (float)Math.PI);
            rightArm.roll = 0.0f;
            rightArm.yaw = -(0.1f - h * 0.6f);
            rightArm.pitch = g;
            rightArm.pitch += h * 1.2f - i * 0.4f;
            leftArm.roll = 0.0f;
            leftArm.yaw = 0.1f - h * 0.6f;
            leftArm.pitch = g;
            leftArm.pitch += h * 1.2f - i * 0.4f;
        }
        ArmPosing.swingArms(rightArm, leftArm, lancerEntityRenderState.age);
    }
}
