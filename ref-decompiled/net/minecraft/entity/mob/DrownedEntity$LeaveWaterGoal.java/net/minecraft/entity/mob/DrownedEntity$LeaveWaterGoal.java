/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

static class DrownedEntity.LeaveWaterGoal
extends MoveToTargetPosGoal {
    private final DrownedEntity drowned;

    public DrownedEntity.LeaveWaterGoal(DrownedEntity drowned, double speed) {
        super(drowned, speed, 8, 2);
        this.drowned = drowned;
    }

    @Override
    public boolean canStart() {
        return super.canStart() && !this.drowned.getEntityWorld().isDay() && this.drowned.isTouchingWater() && this.drowned.getY() >= (double)(this.drowned.getEntityWorld().getSeaLevel() - 3);
    }

    @Override
    public boolean shouldContinue() {
        return super.shouldContinue();
    }

    @Override
    protected boolean isTargetPos(WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.up();
        if (!world.isAir(blockPos) || !world.isAir(blockPos.up())) {
            return false;
        }
        return world.getBlockState(pos).hasSolidTopSurface(world, pos, this.drowned);
    }

    @Override
    public void start() {
        this.drowned.setTargetingUnderwater(false);
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
    }
}
