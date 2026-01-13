/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.SlimeEntity;

static class SlimeEntity.FaceTowardTargetGoal
extends Goal {
    private final SlimeEntity slime;
    private int ticksLeft;

    public SlimeEntity.FaceTowardTargetGoal(SlimeEntity slime) {
        this.slime = slime;
        this.setControls(EnumSet.of(Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        LivingEntity livingEntity = this.slime.getTarget();
        if (livingEntity == null) {
            return false;
        }
        if (!this.slime.canTarget(livingEntity)) {
            return false;
        }
        return this.slime.getMoveControl() instanceof SlimeEntity.SlimeMoveControl;
    }

    @Override
    public void start() {
        this.ticksLeft = SlimeEntity.FaceTowardTargetGoal.toGoalTicks(300);
        super.start();
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity livingEntity = this.slime.getTarget();
        if (livingEntity == null) {
            return false;
        }
        if (!this.slime.canTarget(livingEntity)) {
            return false;
        }
        return --this.ticksLeft > 0;
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        MoveControl moveControl;
        LivingEntity livingEntity = this.slime.getTarget();
        if (livingEntity != null) {
            this.slime.lookAtEntity(livingEntity, 10.0f, 10.0f);
        }
        if ((moveControl = this.slime.getMoveControl()) instanceof SlimeEntity.SlimeMoveControl) {
            SlimeEntity.SlimeMoveControl slimeMoveControl = (SlimeEntity.SlimeMoveControl)moveControl;
            slimeMoveControl.look(this.slime.getYaw(), this.slime.canAttack());
        }
    }
}
