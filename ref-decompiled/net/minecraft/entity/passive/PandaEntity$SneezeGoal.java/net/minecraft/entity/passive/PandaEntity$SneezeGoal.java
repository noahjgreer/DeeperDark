/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.PandaEntity;

static class PandaEntity.SneezeGoal
extends Goal {
    private final PandaEntity panda;

    public PandaEntity.SneezeGoal(PandaEntity panda) {
        this.panda = panda;
    }

    @Override
    public boolean canStart() {
        if (!this.panda.isBaby() || !this.panda.isIdle()) {
            return false;
        }
        if (this.panda.isWeak() && this.panda.random.nextInt(PandaEntity.SneezeGoal.toGoalTicks(500)) == 1) {
            return true;
        }
        return this.panda.random.nextInt(PandaEntity.SneezeGoal.toGoalTicks(6000)) == 1;
    }

    @Override
    public boolean shouldContinue() {
        return false;
    }

    @Override
    public void start() {
        this.panda.setSneezing(true);
    }
}
