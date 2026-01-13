/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;

static class BeeEntity.StingTargetGoal
extends ActiveTargetGoal<PlayerEntity> {
    BeeEntity.StingTargetGoal(BeeEntity bee) {
        super(bee, PlayerEntity.class, 10, true, false, bee::shouldAngerAt);
    }

    @Override
    public boolean canStart() {
        return this.canSting() && super.canStart();
    }

    @Override
    public boolean shouldContinue() {
        boolean bl = this.canSting();
        if (!bl || this.mob.getTarget() == null) {
            this.target = null;
            return false;
        }
        return super.shouldContinue();
    }

    private boolean canSting() {
        BeeEntity beeEntity = (BeeEntity)this.mob;
        return beeEntity.hasAngerTime() && !beeEntity.hasStung();
    }
}
