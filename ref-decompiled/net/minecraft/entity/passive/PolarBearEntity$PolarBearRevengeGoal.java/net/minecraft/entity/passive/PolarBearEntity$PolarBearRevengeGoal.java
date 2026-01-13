/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PolarBearEntity;

class PolarBearEntity.PolarBearRevengeGoal
extends RevengeGoal {
    public PolarBearEntity.PolarBearRevengeGoal() {
        super(PolarBearEntity.this, new Class[0]);
    }

    @Override
    public void start() {
        super.start();
        if (PolarBearEntity.this.isBaby()) {
            this.callSameTypeForRevenge();
            this.stop();
        }
    }

    @Override
    protected void setMobEntityTarget(MobEntity mob, LivingEntity target) {
        if (mob instanceof PolarBearEntity && !mob.isBaby()) {
            super.setMobEntityTarget(mob, target);
        }
    }
}
