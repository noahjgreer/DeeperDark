/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity.passive;

import java.util.EnumSet;
import net.minecraft.entity.ai.AboveGroundTargeting;
import net.minecraft.entity.ai.NoPenaltySolidTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jspecify.annotations.Nullable;

class BeeEntity.BeeWanderAroundGoal
extends Goal {
    BeeEntity.BeeWanderAroundGoal() {
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        return BeeEntity.this.navigation.isIdle() && BeeEntity.this.random.nextInt(10) == 0;
    }

    @Override
    public boolean shouldContinue() {
        return BeeEntity.this.navigation.isFollowingPath();
    }

    @Override
    public void start() {
        Vec3d vec3d = this.getRandomLocation();
        if (vec3d != null) {
            BeeEntity.this.navigation.startMovingAlong(BeeEntity.this.navigation.findPathTo(BlockPos.ofFloored(vec3d), 1), 1.0);
        }
    }

    private @Nullable Vec3d getRandomLocation() {
        Vec3d vec3d2;
        if (BeeEntity.this.hasValidHive() && !BeeEntity.this.isWithinDistance(BeeEntity.this.hivePos, this.getMaxWanderDistance())) {
            Vec3d vec3d = Vec3d.ofCenter(BeeEntity.this.hivePos);
            vec3d2 = vec3d.subtract(BeeEntity.this.getEntityPos()).normalize();
        } else {
            vec3d2 = BeeEntity.this.getRotationVec(0.0f);
        }
        int i = 8;
        Vec3d vec3d3 = AboveGroundTargeting.find(BeeEntity.this, 8, 7, vec3d2.x, vec3d2.z, 1.5707964f, 3, 1);
        if (vec3d3 != null) {
            return vec3d3;
        }
        return NoPenaltySolidTargeting.find(BeeEntity.this, 8, 4, -2, vec3d2.x, vec3d2.z, 1.5707963705062866);
    }

    private int getMaxWanderDistance() {
        int i = BeeEntity.this.hasHivePos() || BeeEntity.this.hasFlower() ? 24 : 16;
        return 48 - i;
    }
}
