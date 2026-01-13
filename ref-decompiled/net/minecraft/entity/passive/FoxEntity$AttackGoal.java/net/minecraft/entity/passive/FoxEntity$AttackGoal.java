/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.sound.SoundEvents;

class FoxEntity.AttackGoal
extends MeleeAttackGoal {
    public FoxEntity.AttackGoal(double speed, boolean pauseWhenIdle) {
        super(FoxEntity.this, speed, pauseWhenIdle);
    }

    @Override
    protected void attack(LivingEntity target) {
        if (this.canAttack(target)) {
            this.resetCooldown();
            this.mob.tryAttack(FoxEntity.AttackGoal.getServerWorld(this.mob), target);
            FoxEntity.this.playSound(SoundEvents.ENTITY_FOX_BITE, 1.0f, 1.0f);
        }
    }

    @Override
    public void start() {
        FoxEntity.this.setRollingHead(false);
        super.start();
    }

    @Override
    public boolean canStart() {
        return !FoxEntity.this.isSitting() && !FoxEntity.this.isSleeping() && !FoxEntity.this.isInSneakingPose() && !FoxEntity.this.isWalking() && super.canStart();
    }
}
