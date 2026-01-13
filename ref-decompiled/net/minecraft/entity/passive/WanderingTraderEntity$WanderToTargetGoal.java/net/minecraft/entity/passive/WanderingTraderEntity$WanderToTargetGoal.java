/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

class WanderingTraderEntity.WanderToTargetGoal
extends Goal {
    final WanderingTraderEntity trader;
    final double proximityDistance;
    final double speed;

    WanderingTraderEntity.WanderToTargetGoal(WanderingTraderEntity trader, double proximityDistance, double speed) {
        this.trader = trader;
        this.proximityDistance = proximityDistance;
        this.speed = speed;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public void stop() {
        this.trader.setWanderTarget(null);
        WanderingTraderEntity.this.navigation.stop();
    }

    @Override
    public boolean canStart() {
        BlockPos blockPos = this.trader.getWanderTarget();
        return blockPos != null && this.isTooFarFrom(blockPos, this.proximityDistance);
    }

    @Override
    public void tick() {
        BlockPos blockPos = this.trader.getWanderTarget();
        if (blockPos != null && WanderingTraderEntity.this.navigation.isIdle()) {
            if (this.isTooFarFrom(blockPos, 10.0)) {
                Vec3d vec3d = new Vec3d((double)blockPos.getX() - this.trader.getX(), (double)blockPos.getY() - this.trader.getY(), (double)blockPos.getZ() - this.trader.getZ()).normalize();
                Vec3d vec3d2 = vec3d.multiply(10.0).add(this.trader.getX(), this.trader.getY(), this.trader.getZ());
                WanderingTraderEntity.this.navigation.startMovingTo(vec3d2.x, vec3d2.y, vec3d2.z, this.speed);
            } else {
                WanderingTraderEntity.this.navigation.startMovingTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), this.speed);
            }
        }
    }

    private boolean isTooFarFrom(BlockPos pos, double proximityDistance) {
        return !pos.isWithinDistance(this.trader.getEntityPos(), proximityDistance);
    }
}
