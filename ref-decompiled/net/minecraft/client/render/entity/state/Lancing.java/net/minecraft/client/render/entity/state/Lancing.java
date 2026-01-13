/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
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

@Environment(value=EnvType.CLIENT)
public class Lancing {
    static float method_75390(float f, float g, float h) {
        return MathHelper.clamp(MathHelper.getLerpProgress(f, g, h), 0.0f, 1.0f);
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
        KineticWeaponComponent kineticWeaponComponent = itemStack.get(DataComponentTypes.KINETIC_WEAPON);
        if (kineticWeaponComponent == null) {
            return;
        }
        class_12153 lv = class_12153.method_75397(kineticWeaponComponent, state.itemUseTime);
        arm.yaw += (float)(-i) * lv.swayScaleFast() * ((float)Math.PI / 180) * lv.swayIntensity() * 1.0f;
        arm.roll += (float)(-i) * lv.swayScaleSlow() * ((float)Math.PI / 180) * lv.swayIntensity() * 0.5f;
        arm.pitch += (float)Math.PI / 180 * (-40.0f * lv.raiseProgressStart() + 30.0f * lv.raiseProgressMiddle() + -20.0f * lv.raiseProgressEnd() + 20.0f * lv.lowerProgress() + 10.0f * lv.raiseBackProgress() + 0.6f * lv.swayScaleSlow() * lv.swayIntensity());
    }

    public static <S extends ArmedEntityRenderState> void method_75392(S armedEntityRenderState, MatrixStack matrixStack, float f, Arm arm, ItemStack itemStack) {
        KineticWeaponComponent kineticWeaponComponent = itemStack.get(DataComponentTypes.KINETIC_WEAPON);
        if (kineticWeaponComponent == null || f == 0.0f) {
            return;
        }
        float g = Easing.inQuad(Lancing.method_75390(armedEntityRenderState.handSwingProgress, 0.05f, 0.2f));
        float h = Easing.inOutExpo(Lancing.method_75390(armedEntityRenderState.handSwingProgress, 0.4f, 1.0f));
        class_12153 lv = class_12153.method_75397(kineticWeaponComponent, f);
        int i = arm == Arm.RIGHT ? 1 : -1;
        float j = 1.0f - Easing.outBack(1.0f - lv.raiseProgress());
        float k = 0.125f;
        float l = Lancing.method_75916(armedEntityRenderState.timeSinceLastKineticAttack);
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
        float g = Easing.inOutSine(Lancing.method_75390(f, 0.0f, 0.05f));
        float h = Easing.inQuad(Lancing.method_75390(f, 0.05f, 0.2f));
        float i = Easing.inOutExpo(Lancing.method_75390(f, 0.4f, 1.0f));
        bipedEntityModel.getArm((Arm)arm).pitch += (90.0f * g - 120.0f * h + 30.0f * i) * ((float)Math.PI / 180);
    }

    public static <S extends ArmedEntityRenderState> void method_75395(S armedEntityRenderState, MatrixStack matrixStack) {
        if (armedEntityRenderState.handSwingProgress <= 0.0f) {
            return;
        }
        KineticWeaponComponent kineticWeaponComponent = armedEntityRenderState.getMainHandItemStack().get(DataComponentTypes.KINETIC_WEAPON);
        float f = kineticWeaponComponent != null ? kineticWeaponComponent.forwardMovement() : 0.0f;
        float g = 0.125f;
        float h = armedEntityRenderState.handSwingProgress;
        float i = Easing.inQuad(Lancing.method_75390(h, 0.05f, 0.2f));
        float j = Easing.inOutExpo(Lancing.method_75390(h, 0.4f, 1.0f));
        matrixStack.multiply((Quaternionfc)RotationAxis.NEGATIVE_X.rotationDegrees(70.0f * (i - j)), 0.0f, -0.125f, 0.125f);
        matrixStack.translate(0.0f, f * (i - j), 0.0f);
    }

    private static float method_75916(float f) {
        return 0.4f * (Easing.outQuart(Lancing.method_75390(f, 1.0f, 3.0f)) - Easing.inOutSine(Lancing.method_75390(f, 3.0f, 10.0f)));
    }

    public static void method_75396(float f, MatrixStack matrixStack, float g, Arm arm, ItemStack itemStack) {
        KineticWeaponComponent kineticWeaponComponent = itemStack.get(DataComponentTypes.KINETIC_WEAPON);
        if (kineticWeaponComponent == null) {
            return;
        }
        class_12153 lv = class_12153.method_75397(kineticWeaponComponent, g);
        int i = arm == Arm.RIGHT ? 1 : -1;
        matrixStack.translate((double)((float)i * (lv.raiseProgress() * 0.15f + lv.raiseProgressEnd() * -0.05f + lv.swayProgress() * -0.1f + lv.swayScaleSlow() * 0.005f)), (double)(lv.raiseProgress() * -0.075f + lv.raiseProgressMiddle() * 0.075f + lv.swayScaleFast() * 0.01f), (double)lv.raiseProgressStart() * 0.05 + (double)lv.raiseProgressEnd() * -0.05 + (double)(lv.swayScaleSlow() * 0.005f));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(-65.0f * Easing.inOutBack(lv.raiseProgress()) - 35.0f * lv.lowerProgress() + 100.0f * lv.raiseBackProgress() + -0.5f * lv.swayScaleFast()), 0.0f, 0.1f, 0.0f);
        matrixStack.multiply((Quaternionfc)RotationAxis.NEGATIVE_Y.rotationDegrees((float)i * (-90.0f * Lancing.method_75390(lv.raiseProgress(), 0.5f, 0.55f) + 90.0f * lv.swayProgress() + 2.0f * lv.swayScaleSlow())), (float)i * 0.15f, 0.0f, 0.0f);
        matrixStack.translate(0.0f, -Lancing.method_75916(f), 0.0f);
    }

    public static void method_75391(float f, MatrixStack matrixStack, int i, Arm arm) {
        float g = Easing.inOutSine(Lancing.method_75390(f, 0.0f, 0.05f));
        float h = Easing.outBack(Lancing.method_75390(f, 0.05f, 0.2f));
        float j = Easing.inOutExpo(Lancing.method_75390(f, 0.4f, 1.0f));
        matrixStack.translate((float)i * 0.1f * (g - h), -0.075f * (g - j), 0.65f * (g - h));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(-70.0f * (g - j)));
        matrixStack.translate(0.0, 0.0, -0.25 * (double)(j - h));
    }

    @Environment(value=EnvType.CLIENT)
    record class_12153(float raiseProgress, float raiseProgressStart, float raiseProgressMiddle, float raiseProgressEnd, float swayProgress, float lowerProgress, float raiseBackProgress, float swayIntensity, float swayScaleSlow, float swayScaleFast) {
        public static class_12153 method_75397(KineticWeaponComponent kineticWeaponComponent, float f) {
            int i = kineticWeaponComponent.delayTicks();
            int j = kineticWeaponComponent.dismountConditions().map(KineticWeaponComponent.Condition::maxDurationTicks).orElse(0) + i;
            int k = j - 20;
            int l = kineticWeaponComponent.knockbackConditions().map(KineticWeaponComponent.Condition::maxDurationTicks).orElse(0) + i;
            int m = l - 40;
            int n = kineticWeaponComponent.damageConditions().map(KineticWeaponComponent.Condition::maxDurationTicks).orElse(0) + i;
            float g = Lancing.method_75390(f, 0.0f, i);
            float h = Lancing.method_75390(g, 0.0f, 0.5f);
            float o = Lancing.method_75390(g, 0.5f, 0.8f);
            float p = Lancing.method_75390(g, 0.8f, 1.0f);
            float q = Lancing.method_75390(f, k, m);
            float r = Easing.outCubic(Easing.inOutElastic(Lancing.method_75390(f - 20.0f, m, l)));
            float s = Lancing.method_75390(f, n - 5, n);
            float t = 2.0f * Easing.outCirc(q) - 2.0f * Easing.inCirc(s);
            float u = MathHelper.sin(f * 19.0f * ((float)Math.PI / 180)) * t;
            float v = MathHelper.sin(f * 30.0f * ((float)Math.PI / 180)) * t;
            return new class_12153(g, h, o, p, q, r, s, t, u, v);
        }
    }
}
