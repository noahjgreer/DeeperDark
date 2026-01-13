/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.MathHelper;

static class StatusEffectInstance.Fading {
    private float factor;
    private float lastFactor;

    StatusEffectInstance.Fading() {
    }

    public void skipFading(StatusEffectInstance effect) {
        this.lastFactor = this.factor = StatusEffectInstance.Fading.shouldFadeIn(effect) ? 1.0f : 0.0f;
    }

    public void copyFrom(StatusEffectInstance.Fading fading) {
        this.factor = fading.factor;
        this.lastFactor = fading.lastFactor;
    }

    public void update(StatusEffectInstance effect) {
        int i;
        float f;
        this.lastFactor = this.factor;
        boolean bl = StatusEffectInstance.Fading.shouldFadeIn(effect);
        float f2 = f = bl ? 1.0f : 0.0f;
        if (this.factor == f) {
            return;
        }
        StatusEffect statusEffect = effect.getEffectType().value();
        int n = i = bl ? statusEffect.getFadeInTicks() : statusEffect.getFadeOutTicks();
        if (i == 0) {
            this.factor = f;
        } else {
            float g = 1.0f / (float)i;
            this.factor += MathHelper.clamp(f - this.factor, -g, g);
        }
    }

    private static boolean shouldFadeIn(StatusEffectInstance effect) {
        return !effect.isDurationBelow(effect.getEffectType().value().getFadeOutThresholdTicks());
    }

    public float calculate(LivingEntity entity, float tickProgress) {
        if (entity.isRemoved()) {
            this.lastFactor = this.factor;
        }
        return MathHelper.lerp(tickProgress, this.lastFactor, this.factor);
    }
}
