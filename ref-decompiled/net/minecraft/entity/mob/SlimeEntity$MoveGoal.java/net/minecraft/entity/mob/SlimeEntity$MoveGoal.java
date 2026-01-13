/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.SlimeEntity;

static class SlimeEntity.MoveGoal
extends Goal {
    private final SlimeEntity slime;

    public SlimeEntity.MoveGoal(SlimeEntity slime) {
        this.slime = slime;
        this.setControls(EnumSet.of(Goal.Control.JUMP, Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        return !this.slime.hasVehicle();
    }

    @Override
    public void tick() {
        MoveControl moveControl = this.slime.getMoveControl();
        if (moveControl instanceof SlimeEntity.SlimeMoveControl) {
            SlimeEntity.SlimeMoveControl slimeMoveControl = (SlimeEntity.SlimeMoveControl)moveControl;
            slimeMoveControl.move(1.0);
        }
    }
}
