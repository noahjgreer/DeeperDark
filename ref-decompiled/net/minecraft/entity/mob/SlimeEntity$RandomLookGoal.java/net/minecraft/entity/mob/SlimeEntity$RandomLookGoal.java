/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SlimeEntity;

static class SlimeEntity.RandomLookGoal
extends Goal {
    private final SlimeEntity slime;
    private float targetYaw;
    private int timer;

    public SlimeEntity.RandomLookGoal(SlimeEntity slime) {
        this.slime = slime;
        this.setControls(EnumSet.of(Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        return this.slime.getTarget() == null && (this.slime.isOnGround() || this.slime.isTouchingWater() || this.slime.isInLava() || this.slime.hasStatusEffect(StatusEffects.LEVITATION)) && this.slime.getMoveControl() instanceof SlimeEntity.SlimeMoveControl;
    }

    @Override
    public void tick() {
        MoveControl moveControl;
        if (--this.timer <= 0) {
            this.timer = this.getTickCount(40 + this.slime.getRandom().nextInt(60));
            this.targetYaw = this.slime.getRandom().nextInt(360);
        }
        if ((moveControl = this.slime.getMoveControl()) instanceof SlimeEntity.SlimeMoveControl) {
            SlimeEntity.SlimeMoveControl slimeMoveControl = (SlimeEntity.SlimeMoveControl)moveControl;
            slimeMoveControl.look(this.targetYaw, false);
        }
    }
}
