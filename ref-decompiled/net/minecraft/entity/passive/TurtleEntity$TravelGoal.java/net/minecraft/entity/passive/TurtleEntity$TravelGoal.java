/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

static class TurtleEntity.TravelGoal
extends Goal {
    private final TurtleEntity turtle;
    private final double speed;
    private boolean noPath;

    TurtleEntity.TravelGoal(TurtleEntity turtle, double speed) {
        this.turtle = turtle;
        this.speed = speed;
    }

    @Override
    public boolean canStart() {
        return !this.turtle.landBound && !this.turtle.hasEgg() && this.turtle.isTouchingWater();
    }

    @Override
    public void start() {
        int i = 512;
        int j = 4;
        Random random = this.turtle.random;
        int k = random.nextInt(1025) - 512;
        int l = random.nextInt(9) - 4;
        int m = random.nextInt(1025) - 512;
        if ((double)l + this.turtle.getY() > (double)(this.turtle.getEntityWorld().getSeaLevel() - 1)) {
            l = 0;
        }
        this.turtle.travelPos = BlockPos.ofFloored((double)k + this.turtle.getX(), (double)l + this.turtle.getY(), (double)m + this.turtle.getZ());
        this.noPath = false;
    }

    @Override
    public void tick() {
        if (this.turtle.travelPos == null) {
            this.noPath = true;
            return;
        }
        if (this.turtle.getNavigation().isIdle()) {
            Vec3d vec3d = Vec3d.ofBottomCenter(this.turtle.travelPos);
            Vec3d vec3d2 = NoPenaltyTargeting.findTo(this.turtle, 16, 3, vec3d, 0.3141592741012573);
            if (vec3d2 == null) {
                vec3d2 = NoPenaltyTargeting.findTo(this.turtle, 8, 7, vec3d, 1.5707963705062866);
            }
            if (vec3d2 != null) {
                int i = MathHelper.floor(vec3d2.x);
                int j = MathHelper.floor(vec3d2.z);
                int k = 34;
                if (!this.turtle.getEntityWorld().isRegionLoaded(i - 34, j - 34, i + 34, j + 34)) {
                    vec3d2 = null;
                }
            }
            if (vec3d2 == null) {
                this.noPath = true;
                return;
            }
            this.turtle.getNavigation().startMovingTo(vec3d2.x, vec3d2.y, vec3d2.z, this.speed);
        }
    }

    @Override
    public boolean shouldContinue() {
        return !this.turtle.getNavigation().isIdle() && !this.noPath && !this.turtle.landBound && !this.turtle.isInLove() && !this.turtle.hasEgg();
    }

    @Override
    public void stop() {
        this.turtle.travelPos = null;
        super.stop();
    }
}
