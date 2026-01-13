/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.mob.DrownedEntity;

static class DrownedEntity.DrownedAttackGoal
extends ZombieAttackGoal {
    private final DrownedEntity drowned;

    public DrownedEntity.DrownedAttackGoal(DrownedEntity drowned, double speed, boolean pauseWhenMobIdle) {
        super(drowned, speed, pauseWhenMobIdle);
        this.drowned = drowned;
    }

    @Override
    public boolean canStart() {
        return super.canStart() && this.drowned.canDrownedAttackTarget(this.drowned.getTarget());
    }

    @Override
    public boolean shouldContinue() {
        return super.shouldContinue() && this.drowned.canDrownedAttackTarget(this.drowned.getTarget());
    }
}
