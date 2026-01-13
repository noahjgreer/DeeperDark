/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.Lancing;
import net.minecraft.component.type.KineticWeaponComponent;
import net.minecraft.util.math.Easing;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
record Lancing.class_12153(float raiseProgress, float raiseProgressStart, float raiseProgressMiddle, float raiseProgressEnd, float swayProgress, float lowerProgress, float raiseBackProgress, float swayIntensity, float swayScaleSlow, float swayScaleFast) {
    public static Lancing.class_12153 method_75397(KineticWeaponComponent kineticWeaponComponent, float f) {
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
        return new Lancing.class_12153(g, h, o, p, q, r, s, t, u, v);
    }
}
