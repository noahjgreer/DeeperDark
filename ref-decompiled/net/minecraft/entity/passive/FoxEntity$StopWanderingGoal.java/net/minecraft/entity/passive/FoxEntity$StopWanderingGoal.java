/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;

class FoxEntity.StopWanderingGoal
extends Goal {
    int timer;

    public FoxEntity.StopWanderingGoal() {
        this.setControls(EnumSet.of(Goal.Control.LOOK, Goal.Control.JUMP, Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        return FoxEntity.this.isWalking();
    }

    @Override
    public boolean shouldContinue() {
        return this.canStart() && this.timer > 0;
    }

    @Override
    public void start() {
        this.timer = this.getTickCount(40);
    }

    @Override
    public void stop() {
        FoxEntity.this.setWalking(false);
    }

    @Override
    public void tick() {
        --this.timer;
    }
}
