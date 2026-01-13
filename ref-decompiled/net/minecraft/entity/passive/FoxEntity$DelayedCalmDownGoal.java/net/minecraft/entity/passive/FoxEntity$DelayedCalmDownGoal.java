/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.FoxEntity;

class FoxEntity.DelayedCalmDownGoal
extends FoxEntity.CalmDownGoal {
    private static final int MAX_CALM_DOWN_TIME = FoxEntity.DelayedCalmDownGoal.toGoalTicks(140);
    private int timer;

    public FoxEntity.DelayedCalmDownGoal() {
        super(FoxEntity.this);
        this.timer = FoxEntity.this.random.nextInt(MAX_CALM_DOWN_TIME);
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK, Goal.Control.JUMP));
    }

    @Override
    public boolean canStart() {
        if (FoxEntity.this.sidewaysSpeed != 0.0f || FoxEntity.this.upwardSpeed != 0.0f || FoxEntity.this.forwardSpeed != 0.0f) {
            return false;
        }
        return this.canNotCalmDown() || FoxEntity.this.isSleeping();
    }

    @Override
    public boolean shouldContinue() {
        return this.canNotCalmDown();
    }

    private boolean canNotCalmDown() {
        if (this.timer > 0) {
            --this.timer;
            return false;
        }
        return FoxEntity.this.getEntityWorld().isDay() && this.isAtFavoredLocation() && !this.canCalmDown() && !FoxEntity.this.inPowderSnow;
    }

    @Override
    public void stop() {
        this.timer = FoxEntity.this.random.nextInt(MAX_CALM_DOWN_TIME);
        FoxEntity.this.stopActions();
    }

    @Override
    public void start() {
        FoxEntity.this.setSitting(false);
        FoxEntity.this.setCrouching(false);
        FoxEntity.this.setRollingHead(false);
        FoxEntity.this.setJumping(false);
        FoxEntity.this.setSleeping(true);
        FoxEntity.this.getNavigation().stop();
        FoxEntity.this.getMoveControl().moveTo(FoxEntity.this.getX(), FoxEntity.this.getY(), FoxEntity.this.getZ(), 0.0);
    }
}
