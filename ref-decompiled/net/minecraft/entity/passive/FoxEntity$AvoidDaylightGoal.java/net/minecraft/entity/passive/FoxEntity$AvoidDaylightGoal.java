/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.goal.EscapeSunlightGoal;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

class FoxEntity.AvoidDaylightGoal
extends EscapeSunlightGoal {
    private int timer;

    public FoxEntity.AvoidDaylightGoal(double speed) {
        super(FoxEntity.this, speed);
        this.timer = FoxEntity.AvoidDaylightGoal.toGoalTicks(100);
    }

    @Override
    public boolean canStart() {
        if (FoxEntity.this.isSleeping() || this.mob.getTarget() != null) {
            return false;
        }
        if (FoxEntity.this.getEntityWorld().isThundering() && FoxEntity.this.getEntityWorld().isSkyVisible(this.mob.getBlockPos())) {
            return this.targetShadedPos();
        }
        if (this.timer > 0) {
            --this.timer;
            return false;
        }
        this.timer = 100;
        BlockPos blockPos = this.mob.getBlockPos();
        return FoxEntity.this.getEntityWorld().isDay() && FoxEntity.this.getEntityWorld().isSkyVisible(blockPos) && !((ServerWorld)FoxEntity.this.getEntityWorld()).isNearOccupiedPointOfInterest(blockPos) && this.targetShadedPos();
    }

    @Override
    public void start() {
        FoxEntity.this.stopActions();
        super.start();
    }
}
