/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.PathAwareEntity;

class AbstractSkeletonEntity.1
extends MeleeAttackGoal {
    AbstractSkeletonEntity.1(PathAwareEntity pathAwareEntity, double d, boolean bl) {
        super(pathAwareEntity, d, bl);
    }

    @Override
    public void stop() {
        super.stop();
        AbstractSkeletonEntity.this.setAttacking(false);
    }

    @Override
    public void start() {
        super.start();
        AbstractSkeletonEntity.this.setAttacking(true);
    }
}
