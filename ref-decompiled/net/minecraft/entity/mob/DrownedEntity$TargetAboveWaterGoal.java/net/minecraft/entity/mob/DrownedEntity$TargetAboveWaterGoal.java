/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.util.math.Vec3d;

static class DrownedEntity.TargetAboveWaterGoal
extends Goal {
    private final DrownedEntity drowned;
    private final double speed;
    private final int minY;
    private boolean foundTarget;

    public DrownedEntity.TargetAboveWaterGoal(DrownedEntity drowned, double speed, int minY) {
        this.drowned = drowned;
        this.speed = speed;
        this.minY = minY;
    }

    @Override
    public boolean canStart() {
        return !this.drowned.getEntityWorld().isDay() && this.drowned.isTouchingWater() && this.drowned.getY() < (double)(this.minY - 2);
    }

    @Override
    public boolean shouldContinue() {
        return this.canStart() && !this.foundTarget;
    }

    @Override
    public void tick() {
        if (this.drowned.getY() < (double)(this.minY - 1) && (this.drowned.getNavigation().isIdle() || this.drowned.hasFinishedCurrentPath())) {
            Vec3d vec3d = NoPenaltyTargeting.findTo(this.drowned, 4, 8, new Vec3d(this.drowned.getX(), this.minY - 1, this.drowned.getZ()), 1.5707963705062866);
            if (vec3d == null) {
                this.foundTarget = true;
                return;
            }
            this.drowned.getNavigation().startMovingTo(vec3d.x, vec3d.y, vec3d.z, this.speed);
        }
    }

    @Override
    public void start() {
        this.drowned.setTargetingUnderwater(true);
        this.foundTarget = false;
    }

    @Override
    public void stop() {
        this.drowned.setTargetingUnderwater(false);
    }
}
