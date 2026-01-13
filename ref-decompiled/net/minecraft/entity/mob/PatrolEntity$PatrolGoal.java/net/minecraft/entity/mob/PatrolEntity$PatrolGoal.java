/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;

public static class PatrolEntity.PatrolGoal<T extends PatrolEntity>
extends Goal {
    private static final int field_30474 = 200;
    private final T entity;
    private final double leaderSpeed;
    private final double followSpeed;
    private long nextPatrolSearchTime;

    public PatrolEntity.PatrolGoal(T entity, double leaderSpeed, double followSpeed) {
        this.entity = entity;
        this.leaderSpeed = leaderSpeed;
        this.followSpeed = followSpeed;
        this.nextPatrolSearchTime = -1L;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        boolean bl = ((Entity)this.entity).getEntityWorld().getTime() < this.nextPatrolSearchTime;
        return ((PatrolEntity)this.entity).isRaidCenterSet() && ((MobEntity)this.entity).getTarget() == null && !((Entity)this.entity).hasControllingPassenger() && ((PatrolEntity)this.entity).hasPatrolTarget() && !bl;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void tick() {
        boolean bl = ((PatrolEntity)this.entity).isPatrolLeader();
        EntityNavigation entityNavigation = ((MobEntity)this.entity).getNavigation();
        if (entityNavigation.isIdle()) {
            List<PatrolEntity> list = this.findPatrolTargets();
            if (((PatrolEntity)this.entity).isRaidCenterSet() && list.isEmpty()) {
                ((PatrolEntity)this.entity).setPatrolling(false);
            } else if (!bl || !((PatrolEntity)this.entity).getPatrolTarget().isWithinDistance(((Entity)this.entity).getEntityPos(), 10.0)) {
                Vec3d vec3d = Vec3d.ofBottomCenter(((PatrolEntity)this.entity).getPatrolTarget());
                Vec3d vec3d2 = ((Entity)this.entity).getEntityPos();
                Vec3d vec3d3 = vec3d2.subtract(vec3d);
                vec3d = vec3d3.rotateY(90.0f).multiply(0.4).add(vec3d);
                Vec3d vec3d4 = vec3d.subtract(vec3d2).normalize().multiply(10.0).add(vec3d2);
                BlockPos blockPos = BlockPos.ofFloored(vec3d4);
                blockPos = ((Entity)this.entity).getEntityWorld().getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockPos);
                if (!entityNavigation.startMovingTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), bl ? this.followSpeed : this.leaderSpeed)) {
                    this.wander();
                    this.nextPatrolSearchTime = ((Entity)this.entity).getEntityWorld().getTime() + 200L;
                } else if (bl) {
                    for (PatrolEntity patrolEntity : list) {
                        patrolEntity.setPatrolTarget(blockPos);
                    }
                }
            } else {
                ((PatrolEntity)this.entity).setRandomPatrolTarget();
            }
        }
    }

    private List<PatrolEntity> findPatrolTargets() {
        return ((Entity)this.entity).getEntityWorld().getEntitiesByClass(PatrolEntity.class, ((Entity)this.entity).getBoundingBox().expand(16.0), patrolEntity -> patrolEntity.hasNoRaid() && !patrolEntity.isPartOf((Entity)this.entity));
    }

    private boolean wander() {
        Random random = ((Entity)this.entity).getRandom();
        BlockPos blockPos = ((Entity)this.entity).getEntityWorld().getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ((Entity)this.entity).getBlockPos().add(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
        return ((MobEntity)this.entity).getNavigation().startMovingTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), this.leaderSpeed);
    }
}
