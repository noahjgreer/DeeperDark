/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.SlimeEntity;

static class SlimeEntity.SwimmingGoal
extends Goal {
    private final SlimeEntity slime;

    public SlimeEntity.SwimmingGoal(SlimeEntity slime) {
        this.slime = slime;
        this.setControls(EnumSet.of(Goal.Control.JUMP, Goal.Control.MOVE));
        slime.getNavigation().setCanSwim(true);
    }

    @Override
    public boolean canStart() {
        return (this.slime.isTouchingWater() || this.slime.isInLava()) && this.slime.getMoveControl() instanceof SlimeEntity.SlimeMoveControl;
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        MoveControl moveControl;
        if (this.slime.getRandom().nextFloat() < 0.8f) {
            this.slime.getJumpControl().setActive();
        }
        if ((moveControl = this.slime.getMoveControl()) instanceof SlimeEntity.SlimeMoveControl) {
            SlimeEntity.SlimeMoveControl slimeMoveControl = (SlimeEntity.SlimeMoveControl)moveControl;
            slimeMoveControl.move(1.2);
        }
    }
}
