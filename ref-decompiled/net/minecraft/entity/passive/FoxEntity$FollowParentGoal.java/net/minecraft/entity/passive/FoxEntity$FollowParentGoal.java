/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.passive.FoxEntity;

static class FoxEntity.FollowParentGoal
extends FollowParentGoal {
    private final FoxEntity fox;

    public FoxEntity.FollowParentGoal(FoxEntity fox, double speed) {
        super(fox, speed);
        this.fox = fox;
    }

    @Override
    public boolean canStart() {
        return !this.fox.isAggressive() && super.canStart();
    }

    @Override
    public boolean shouldContinue() {
        return !this.fox.isAggressive() && super.shouldContinue();
    }

    @Override
    public void start() {
        this.fox.stopActions();
        super.start();
    }
}
