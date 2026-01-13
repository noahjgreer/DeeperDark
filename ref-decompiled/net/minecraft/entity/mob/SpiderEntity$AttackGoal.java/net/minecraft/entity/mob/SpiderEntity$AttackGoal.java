/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.SpiderEntity;

static class SpiderEntity.AttackGoal
extends MeleeAttackGoal {
    public SpiderEntity.AttackGoal(SpiderEntity spider) {
        super(spider, 1.0, true);
    }

    @Override
    public boolean canStart() {
        return super.canStart() && !this.mob.hasPassengers();
    }

    @Override
    public boolean shouldContinue() {
        float f = this.mob.getBrightnessAtEyes();
        if (f >= 0.5f && this.mob.getRandom().nextInt(100) == 0) {
            this.mob.setTarget(null);
            return false;
        }
        return super.shouldContinue();
    }
}
