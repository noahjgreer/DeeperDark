/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SpiderEntity;

static class SpiderEntity.TargetGoal<T extends LivingEntity>
extends ActiveTargetGoal<T> {
    public SpiderEntity.TargetGoal(SpiderEntity spider, Class<T> targetEntityClass) {
        super((MobEntity)spider, targetEntityClass, true);
    }

    @Override
    public boolean canStart() {
        float f = this.mob.getBrightnessAtEyes();
        if (f >= 0.5f) {
            return false;
        }
        return super.canStart();
    }
}
