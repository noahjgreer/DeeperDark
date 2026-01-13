/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.PandaEntity;

static class PandaEntity.LieOnBackGoal
extends Goal {
    private final PandaEntity panda;
    private int nextLieOnBackAge;

    public PandaEntity.LieOnBackGoal(PandaEntity panda) {
        this.panda = panda;
    }

    @Override
    public boolean canStart() {
        return this.nextLieOnBackAge < this.panda.age && this.panda.isLazy() && this.panda.isIdle() && this.panda.random.nextInt(PandaEntity.LieOnBackGoal.toGoalTicks(400)) == 1;
    }

    @Override
    public boolean shouldContinue() {
        if (this.panda.isTouchingWater() || !this.panda.isLazy() && this.panda.random.nextInt(PandaEntity.LieOnBackGoal.toGoalTicks(600)) == 1) {
            return false;
        }
        return this.panda.random.nextInt(PandaEntity.LieOnBackGoal.toGoalTicks(2000)) != 1;
    }

    @Override
    public void start() {
        this.panda.setLyingOnBack(true);
        this.nextLieOnBackAge = 0;
    }

    @Override
    public void stop() {
        this.panda.setLyingOnBack(false);
        this.nextLieOnBackAge = this.panda.age + 200;
    }
}
