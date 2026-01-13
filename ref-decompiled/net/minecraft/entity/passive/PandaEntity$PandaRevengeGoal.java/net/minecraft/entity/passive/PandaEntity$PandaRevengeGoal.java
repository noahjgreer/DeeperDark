/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PandaEntity;

static class PandaEntity.PandaRevengeGoal
extends RevengeGoal {
    private final PandaEntity panda;

    public PandaEntity.PandaRevengeGoal(PandaEntity panda, Class<?> ... noRevengeTypes) {
        super(panda, noRevengeTypes);
        this.panda = panda;
    }

    @Override
    public boolean shouldContinue() {
        if (this.panda.shouldGetRevenge || this.panda.shouldAttack) {
            this.panda.setTarget(null);
            return false;
        }
        return super.shouldContinue();
    }

    @Override
    protected void setMobEntityTarget(MobEntity mob, LivingEntity target) {
        if (mob instanceof PandaEntity && mob.isAttacking()) {
            mob.setTarget(target);
        }
    }
}
