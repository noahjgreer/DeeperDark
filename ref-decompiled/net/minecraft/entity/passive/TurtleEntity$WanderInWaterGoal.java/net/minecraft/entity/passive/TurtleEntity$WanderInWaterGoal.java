/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

static class TurtleEntity.WanderInWaterGoal
extends MoveToTargetPosGoal {
    private static final int field_30385 = 1200;
    private final TurtleEntity turtle;

    TurtleEntity.WanderInWaterGoal(TurtleEntity turtle, double speed) {
        super(turtle, turtle.isBaby() ? 2.0 : speed, 24);
        this.turtle = turtle;
        this.lowestY = -1;
    }

    @Override
    public boolean shouldContinue() {
        return !this.turtle.isTouchingWater() && this.tryingTime <= 1200 && this.isTargetPos(this.turtle.getEntityWorld(), this.targetPos);
    }

    @Override
    public boolean canStart() {
        if (this.turtle.isBaby() && !this.turtle.isTouchingWater()) {
            return super.canStart();
        }
        if (!(this.turtle.landBound || this.turtle.isTouchingWater() || this.turtle.hasEgg())) {
            return super.canStart();
        }
        return false;
    }

    @Override
    public boolean shouldResetPath() {
        return this.tryingTime % 160 == 0;
    }

    @Override
    protected boolean isTargetPos(WorldView world, BlockPos pos) {
        return world.getBlockState(pos).isOf(Blocks.WATER);
    }
}
