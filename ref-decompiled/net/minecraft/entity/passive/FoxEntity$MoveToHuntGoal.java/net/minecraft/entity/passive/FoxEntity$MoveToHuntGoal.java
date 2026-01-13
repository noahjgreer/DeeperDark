/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.FoxEntity;

class FoxEntity.MoveToHuntGoal
extends Goal {
    public FoxEntity.MoveToHuntGoal() {
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (FoxEntity.this.isSleeping()) {
            return false;
        }
        LivingEntity livingEntity = FoxEntity.this.getTarget();
        return livingEntity != null && livingEntity.isAlive() && CHICKEN_AND_RABBIT_FILTER.test(livingEntity) && FoxEntity.this.squaredDistanceTo(livingEntity) > 36.0 && !FoxEntity.this.isInSneakingPose() && !FoxEntity.this.isRollingHead() && !FoxEntity.this.jumping;
    }

    @Override
    public void start() {
        FoxEntity.this.setSitting(false);
        FoxEntity.this.setWalking(false);
    }

    @Override
    public void stop() {
        LivingEntity livingEntity = FoxEntity.this.getTarget();
        if (livingEntity != null && FoxEntity.canJumpChase(FoxEntity.this, livingEntity)) {
            FoxEntity.this.setRollingHead(true);
            FoxEntity.this.setCrouching(true);
            FoxEntity.this.getNavigation().stop();
            FoxEntity.this.getLookControl().lookAt(livingEntity, FoxEntity.this.getMaxHeadRotation(), FoxEntity.this.getMaxLookPitchChange());
        } else {
            FoxEntity.this.setRollingHead(false);
            FoxEntity.this.setCrouching(false);
        }
    }

    @Override
    public void tick() {
        LivingEntity livingEntity = FoxEntity.this.getTarget();
        if (livingEntity == null) {
            return;
        }
        FoxEntity.this.getLookControl().lookAt(livingEntity, FoxEntity.this.getMaxHeadRotation(), FoxEntity.this.getMaxLookPitchChange());
        if (FoxEntity.this.squaredDistanceTo(livingEntity) <= 36.0) {
            FoxEntity.this.setRollingHead(true);
            FoxEntity.this.setCrouching(true);
            FoxEntity.this.getNavigation().stop();
        } else {
            FoxEntity.this.getNavigation().startMovingTo(livingEntity, 1.5);
        }
    }
}
