/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.BeeEntity;

class BeeEntity.BeeRevengeGoal
extends RevengeGoal {
    BeeEntity.BeeRevengeGoal(BeeEntity bee) {
        super(bee, new Class[0]);
    }

    @Override
    public boolean shouldContinue() {
        return BeeEntity.this.hasAngerTime() && super.shouldContinue();
    }

    @Override
    protected void setMobEntityTarget(MobEntity mob, LivingEntity target) {
        if (mob instanceof BeeEntity && this.mob.canSee(target)) {
            mob.setTarget(target);
        }
    }
}
