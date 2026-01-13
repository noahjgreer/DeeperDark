/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.PathAwareEntity;

class BeeEntity.StingGoal
extends MeleeAttackGoal {
    BeeEntity.StingGoal(PathAwareEntity mob, double speed, boolean pauseWhenMobIdle) {
        super(mob, speed, pauseWhenMobIdle);
    }

    @Override
    public boolean canStart() {
        return super.canStart() && BeeEntity.this.hasAngerTime() && !BeeEntity.this.hasStung();
    }

    @Override
    public boolean shouldContinue() {
        return super.shouldContinue() && BeeEntity.this.hasAngerTime() && !BeeEntity.this.hasStung();
    }
}
