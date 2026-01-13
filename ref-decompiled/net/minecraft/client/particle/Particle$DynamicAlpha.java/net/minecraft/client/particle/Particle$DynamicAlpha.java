/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public record Particle.DynamicAlpha(float startAlpha, float endAlpha, float startAtNormalizedAge, float endAtNormalizedAge) {
    public static final Particle.DynamicAlpha OPAQUE = new Particle.DynamicAlpha(1.0f, 1.0f, 0.0f, 1.0f);

    public boolean isOpaque() {
        return this.startAlpha >= 1.0f && this.endAlpha >= 1.0f;
    }

    public float getAlpha(int age, int maxAge, float tickProgress) {
        if (MathHelper.approximatelyEquals(this.startAlpha, this.endAlpha)) {
            return this.startAlpha;
        }
        float f = MathHelper.getLerpProgress(((float)age + tickProgress) / (float)maxAge, this.startAtNormalizedAge, this.endAtNormalizedAge);
        return MathHelper.clampedLerp(f, this.startAlpha, this.endAlpha);
    }
}
