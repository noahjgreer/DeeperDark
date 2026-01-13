/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.passive.PandaEntity;

static class PandaEntity.AttackGoal
extends MeleeAttackGoal {
    private final PandaEntity panda;

    public PandaEntity.AttackGoal(PandaEntity panda, double speed, boolean pauseWhenMobIdle) {
        super(panda, speed, pauseWhenMobIdle);
        this.panda = panda;
    }

    @Override
    public boolean canStart() {
        return this.panda.isIdle() && super.canStart();
    }
}
