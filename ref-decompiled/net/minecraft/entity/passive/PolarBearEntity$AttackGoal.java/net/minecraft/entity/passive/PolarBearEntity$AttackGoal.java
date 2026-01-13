/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;

class PolarBearEntity.AttackGoal
extends MeleeAttackGoal {
    public PolarBearEntity.AttackGoal() {
        super(PolarBearEntity.this, 1.25, true);
    }

    @Override
    protected void attack(LivingEntity target) {
        if (this.canAttack(target)) {
            this.resetCooldown();
            this.mob.tryAttack(PolarBearEntity.AttackGoal.getServerWorld(this.mob), target);
            PolarBearEntity.this.setWarning(false);
        } else if (this.mob.squaredDistanceTo(target) < (double)((target.getWidth() + 3.0f) * (target.getWidth() + 3.0f))) {
            if (this.isCooledDown()) {
                PolarBearEntity.this.setWarning(false);
                this.resetCooldown();
            }
            if (this.getCooldown() <= 10) {
                PolarBearEntity.this.setWarning(true);
                PolarBearEntity.this.playWarningSound();
            }
        } else {
            this.resetCooldown();
            PolarBearEntity.this.setWarning(false);
        }
    }

    @Override
    public void stop() {
        PolarBearEntity.this.setWarning(false);
        super.stop();
    }
}
