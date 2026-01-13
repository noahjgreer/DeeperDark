/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.mob.MobEntity;

class FoxEntity.LookAtEntityGoal
extends LookAtEntityGoal {
    public FoxEntity.LookAtEntityGoal(MobEntity fox, Class<? extends LivingEntity> targetType, float range) {
        super(fox, targetType, range);
    }

    @Override
    public boolean canStart() {
        return super.canStart() && !FoxEntity.this.isWalking() && !FoxEntity.this.isRollingHead();
    }

    @Override
    public boolean shouldContinue() {
        return super.shouldContinue() && !FoxEntity.this.isWalking() && !FoxEntity.this.isRollingHead();
    }
}
