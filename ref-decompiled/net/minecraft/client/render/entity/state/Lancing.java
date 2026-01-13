/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 *  net.minecraft.client.render.entity.state.ArmedEntityRenderState
 *  net.minecraft.client.render.entity.state.BipedEntityRenderState
 *  net.minecraft.client.render.entity.state.Lancing
 *  net.minecraft.client.render.entity.state.Lancing$class_12153
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.KineticWeaponComponent
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.Arm
 *  net.minecraft.util.Hand
 *  net.minecraft.util.math.Easing
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RotationAxis
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.Lancing;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.KineticWeaponComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Easing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class Lancing {
    static float method_75390(float f, float g, float h) {
        return MathHelper.clamp((float)MathHelper.getLerpProgress((float)f, (float)g, (float)h), (float)0.0f, (float)1.0f);
    }

    public static <T extends BipedEntityRenderState> void positionArmForSpear(ModelPart arm, ModelPart head, boolean right, ItemStack itemStack, T state) {
        int i = right ? 1 : -1;
        arm.yaw = -0.1f * (float)i + head.yaw;
        arm.pitch = -1.5707964f + head.pitch + 0.8f;
        if (state.isGliding || state.leaningPitch > 0.0f) {
            arm.pitch -= 0.9599311f;
        }
        arm.yaw = (float)Math.PI / 180 * Math.clamp(57.295776f * arm.yaw, -60.0f, 60.0f);
        arm.pitch = (float)Math.PI / 180 * Math.clamp(57.295776f * arm.pitch, -120.0f, 30.0f);
        if (state.itemUseTime <= 0.0f || state.isUsingItem && state.activeHand != (right ? Hand.MAIN_HAND : Hand.OFF_HAND)) {
            return;
        }
        KineticWeaponComponent kineticWeaponComponent = (KineticWeaponComponent)itemStack.get(DataComponentTypes.KINETIC_WEAPON);
        if (kineticWeaponComponent == null) {
            return;
        }
        class_12153 lv = class_12153.method_75397((KineticWeaponComponent)kineticWeaponComponent, (float)state.itemUseTime);
        arm.yaw += (float)(-i) * lv.swayScaleFast() * ((float)Math.PI / 180) * lv.swayIntensity() * 1.0f;
        arm.roll += (float)(-i) * lv.swayScaleSlow() * ((float)Math.PI / 180) * lv.swayIntensity() * 0.5f;
        arm.pitch += (float)Math.PI / 180 * (-40.0f * lv.raiseProgressStart() + 30.0f * lv.raiseProgressMiddle() + -20.0f * lv.raiseProgressEnd() + 20.0f * lv.lowerProgress() + 10.0f * lv.raiseBackProgress() + 0.6f * lv.swayScaleSlow() * lv.swayIntensity());
    }

    public static <S extends ArmedEntityRenderState> void method_75392(S armedEntityRenderState, MatrixStack matrixStack, float f, Arm arm, ItemStack itemStack) {
        KineticWeaponComponent kineticWeaponComponent = (KineticWeaponComponent)itemStack.get(DataComponentTypes.KINETIC_WEAPON);
        if (kineticWeaponComponent == null || f == 0.0f) {
            return;
        }
        float g = Easing.inQuad((float)Lancing.method_75390((float)armedEntityRenderState.handSwingProgress, (float)0.05f, (float)0.2f));
        float h = Easing.inOutExpo((float)Lancing.method_75390((float)armedEntityRenderState.handSwingProgress, (float)0.4f, (float)1.0f));
        class_12153 lv = class_12153.method_75397((KineticWeaponComponent)kineticWeaponComponent, (float)f);
        int i = arm == Arm.RIGHT ? 1 : -1;
        float j = 1.0f - Easing.outBack((float)(1.0f - lv.raiseProgress()));
        float k = 0.125f;
        float l = Lancing.method_75916((float)armedEntityRenderState.timeSinceLastKineticAttack);
        matrixStack.translate(0.0, (double)(-l) * 0.4, (double)(-kineticWeaponComponent.forwardMovement() * (j - lv.raiseBackProgress()) + l));
        matrixStack.multiply((Quaternionfc)RotationAxis.NEGATIVE_X.rotationDegrees(70.0f * (lv.raiseProgress() - lv.raiseBackProgress()) - 40.0f * (g - h)), 0.0f, -0.03125f, 0.125f);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees((float)(i * 90) * (lv.raiseProgress() - lv.swayProgress() + 3.0f * h + g)), 0.0f, 0.0f, 0.125f);
    }

    public static <T extends BipedEntityRenderState> void method_75393(BipedEntityModel<T> bipedEntityModel, T bipedEntityRenderState) {
        float f = bipedEntityRenderState.handSwingProgress;
        Arm arm = bipedEntityRenderState.preferredArm;
        bipedEntityModel.rightArm.yaw -= bipedEntityModel.body.yaw;
        bipedEntityModel.leftArm.yaw -= bipedEntityModel.body.yaw;
        bipedEntityModel.leftArm.pitch -= bipedEntityModel.body.yaw;
        float g = Easing.inOutSine((float)Lancing.method_75390((float)f, (float)0.0f, (float)0.05f));
        float h = Easing.inQuad((float)Lancing.method_75390((float)f, (float)0.05f, (float)0.2f));
        float i = Easing.inOutExpo((float)Lancing.method_75390((float)f, (float)0.4f, (float)1.0f));
        bipedEntityModel.getArm((Arm)arm).pitch += (90.0f * g - 120.0f * h + 30.0f * i) * ((float)Math.PI / 180);
    }

    public static <S extends ArmedEntityRenderState> void method_75395(S armedEntityRenderState, MatrixStack matrixStack) {
        if (armedEntityRenderState.handSwingProgress <= 0.0f) {
            return;
        }
        KineticWeaponComponent kineticWeaponComponent = (KineticWeaponComponent)armedEntityRenderState.getMainHandItemStack().get(DataComponentTypes.KINETIC_WEAPON);
        float f = kineticWeaponComponent != null ? kineticWeaponComponent.forwardMovement() : 0.0f;
        float g = 0.125f;
        float h = armedEntityRenderState.handSwingProgress;
        float i = Easing.inQuad((float)Lancing.method_75390((float)h, (float)0.05f, (float)0.2f));
        float j = Easing.inOutExpo((float)Lancing.method_75390((float)h, (float)0.4f, (float)1.0f));
        matrixStack.multiply((Quaternionfc)RotationAxis.NEGATIVE_X.rotationDegrees(70.0f * (i - j)), 0.0f, -0.125f, 0.125f);
        matrixStack.translate(0.0f, f * (i - j), 0.0f);
    }

    private static float method_75916(float f) {
        return 0.4f * (Easing.outQuart((float)Lancing.method_75390((float)f, (float)1.0f, (float)3.0f)) - Easing.inOutSine((float)Lancing.method_75390((float)f, (float)3.0f, (float)10.0f)));
    }

    public static void method_75396(float f, MatrixStack matrixStack, float g, Arm arm, ItemStack itemStack) {
        KineticWeaponComponent kineticWeaponComponent = (KineticWeaponComponent)itemStack.get(DataComponentTypes.KINETIC_WEAPON);
        if (kineticWeaponComponent == null) {
            return;
        }
        class_12153 lv = class_12153.method_75397((KineticWeaponComponent)kineticWeaponComponent, (float)g);
        int i = arm == Arm.RIGHT ? 1 : -1;
        matrixStack.translate((double)((float)i * (lv.raiseProgress() * 0.15f + lv.raiseProgressEnd() * -0.05f + lv.swayProgress() * -0.1f + lv.swayScaleSlow() * 0.005f)), (double)(lv.raiseProgress() * -0.075f + lv.raiseProgressMiddle() * 0.075f + lv.swayScaleFast() * 0.01f), (double)lv.raiseProgressStart() * 0.05 + (double)lv.raiseProgressEnd() * -0.05 + (double)(lv.swayScaleSlow() * 0.005f));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(-65.0f * Easing.inOutBack((float)lv.raiseProgress()) - 35.0f * lv.lowerProgress() + 100.0f * lv.raiseBackProgress() + -0.5f * lv.swayScaleFast()), 0.0f, 0.1f, 0.0f);
        matrixStack.multiply((Quaternionfc)RotationAxis.NEGATIVE_Y.rotationDegrees((float)i * (-90.0f * Lancing.method_75390((float)lv.raiseProgress(), (float)0.5f, (float)0.55f) + 90.0f * lv.swayProgress() + 2.0f * lv.swayScaleSlow())), (float)i * 0.15f, 0.0f, 0.0f);
        matrixStack.translate(0.0f, -Lancing.method_75916((float)f), 0.0f);
    }

    public static void method_75391(float f, MatrixStack matrixStack, int i, Arm arm) {
        float g = Easing.inOutSine((float)Lancing.method_75390((float)f, (float)0.0f, (float)0.05f));
        float h = Easing.outBack((float)Lancing.method_75390((float)f, (float)0.05f, (float)0.2f));
        float j = Easing.inOutExpo((float)Lancing.method_75390((float)f, (float)0.4f, (float)1.0f));
        matrixStack.translate((float)i * 0.1f * (g - h), -0.075f * (g - j), 0.65f * (g - h));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(-70.0f * (g - j)));
        matrixStack.translate(0.0, 0.0, -0.25 * (double)(j - h));
    }
}

