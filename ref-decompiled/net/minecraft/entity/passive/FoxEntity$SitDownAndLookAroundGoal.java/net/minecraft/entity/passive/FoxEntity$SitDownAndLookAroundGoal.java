/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.FoxEntity;

class FoxEntity.SitDownAndLookAroundGoal
extends FoxEntity.CalmDownGoal {
    private double lookX;
    private double lookZ;
    private int timer;
    private int counter;

    public FoxEntity.SitDownAndLookAroundGoal() {
        super(FoxEntity.this);
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        return FoxEntity.this.getAttacker() == null && FoxEntity.this.getRandom().nextFloat() < 0.02f && !FoxEntity.this.isSleeping() && FoxEntity.this.getTarget() == null && FoxEntity.this.getNavigation().isIdle() && !this.canCalmDown() && !FoxEntity.this.isChasing() && !FoxEntity.this.isInSneakingPose();
    }

    @Override
    public boolean shouldContinue() {
        return this.counter > 0;
    }

    @Override
    public void start() {
        this.chooseNewAngle();
        this.counter = 2 + FoxEntity.this.getRandom().nextInt(3);
        FoxEntity.this.setSitting(true);
        FoxEntity.this.getNavigation().stop();
    }

    @Override
    public void stop() {
        FoxEntity.this.setSitting(false);
    }

    @Override
    public void tick() {
        --this.timer;
        if (this.timer <= 0) {
            --this.counter;
            this.chooseNewAngle();
        }
        FoxEntity.this.getLookControl().lookAt(FoxEntity.this.getX() + this.lookX, FoxEntity.this.getEyeY(), FoxEntity.this.getZ() + this.lookZ, FoxEntity.this.getMaxHeadRotation(), FoxEntity.this.getMaxLookPitchChange());
    }

    private void chooseNewAngle() {
        double d = Math.PI * 2 * FoxEntity.this.getRandom().nextDouble();
        this.lookX = Math.cos(d);
        this.lookZ = Math.sin(d);
        this.timer = this.getTickCount(80 + FoxEntity.this.getRandom().nextInt(20));
    }
}
