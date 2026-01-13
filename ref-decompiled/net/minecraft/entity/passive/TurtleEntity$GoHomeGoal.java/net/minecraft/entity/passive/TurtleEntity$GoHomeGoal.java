/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

static class TurtleEntity.GoHomeGoal
extends Goal {
    private final TurtleEntity turtle;
    private final double speed;
    private boolean noPath;
    private int homeReachingTryTicks;
    private static final int MAX_TRY_TICKS = 600;

    TurtleEntity.GoHomeGoal(TurtleEntity turtle, double speed) {
        this.turtle = turtle;
        this.speed = speed;
    }

    @Override
    public boolean canStart() {
        if (this.turtle.isBaby()) {
            return false;
        }
        if (this.turtle.hasEgg()) {
            return true;
        }
        if (this.turtle.getRandom().nextInt(TurtleEntity.GoHomeGoal.toGoalTicks(700)) != 0) {
            return false;
        }
        return !this.turtle.homePos.isWithinDistance(this.turtle.getEntityPos(), 64.0);
    }

    @Override
    public void start() {
        this.turtle.landBound = true;
        this.noPath = false;
        this.homeReachingTryTicks = 0;
    }

    @Override
    public void stop() {
        this.turtle.landBound = false;
    }

    @Override
    public boolean shouldContinue() {
        return !this.turtle.homePos.isWithinDistance(this.turtle.getEntityPos(), 7.0) && !this.noPath && this.homeReachingTryTicks <= this.getTickCount(600);
    }

    @Override
    public void tick() {
        BlockPos blockPos = this.turtle.homePos;
        boolean bl = blockPos.isWithinDistance(this.turtle.getEntityPos(), 16.0);
        if (bl) {
            ++this.homeReachingTryTicks;
        }
        if (this.turtle.getNavigation().isIdle()) {
            Vec3d vec3d = Vec3d.ofBottomCenter(blockPos);
            Vec3d vec3d2 = NoPenaltyTargeting.findTo(this.turtle, 16, 3, vec3d, 0.3141592741012573);
            if (vec3d2 == null) {
                vec3d2 = NoPenaltyTargeting.findTo(this.turtle, 8, 7, vec3d, 1.5707963705062866);
            }
            if (vec3d2 != null && !bl && !this.turtle.getEntityWorld().getBlockState(BlockPos.ofFloored(vec3d2)).isOf(Blocks.WATER)) {
                vec3d2 = NoPenaltyTargeting.findTo(this.turtle, 16, 5, vec3d, 1.5707963705062866);
            }
            if (vec3d2 == null) {
                this.noPath = true;
                return;
            }
            this.turtle.getNavigation().startMovingTo(vec3d2.x, vec3d2.y, vec3d2.z, this.speed);
        }
    }
}
