/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.raid;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.raid.RaiderEntity;

protected static class RaiderEntity.PatrolApproachGoal
extends Goal {
    private final RaiderEntity raider;
    private final float squaredDistance;
    public final TargetPredicate closeRaiderPredicate = TargetPredicate.createNonAttackable().setBaseMaxDistance(8.0).ignoreVisibility().ignoreDistanceScalingFactor();

    public RaiderEntity.PatrolApproachGoal(IllagerEntity raider, float distance) {
        this.raider = raider;
        this.squaredDistance = distance * distance;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        LivingEntity livingEntity = this.raider.getAttacker();
        return this.raider.getRaid() == null && this.raider.isRaidCenterSet() && this.raider.getTarget() != null && !this.raider.isAttacking() && (livingEntity == null || livingEntity.getType() != EntityType.PLAYER);
    }

    @Override
    public void start() {
        super.start();
        this.raider.getNavigation().stop();
        List list = RaiderEntity.PatrolApproachGoal.getServerWorld(this.raider).getTargets(RaiderEntity.class, this.closeRaiderPredicate, this.raider, this.raider.getBoundingBox().expand(8.0, 8.0, 8.0));
        for (RaiderEntity raiderEntity : list) {
            raiderEntity.setTarget(this.raider.getTarget());
        }
    }

    @Override
    public void stop() {
        super.stop();
        LivingEntity livingEntity = this.raider.getTarget();
        if (livingEntity != null) {
            List list = RaiderEntity.PatrolApproachGoal.getServerWorld(this.raider).getTargets(RaiderEntity.class, this.closeRaiderPredicate, this.raider, this.raider.getBoundingBox().expand(8.0, 8.0, 8.0));
            for (RaiderEntity raiderEntity : list) {
                raiderEntity.setTarget(livingEntity);
                raiderEntity.setAttacking(true);
            }
            this.raider.setAttacking(true);
        }
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity livingEntity = this.raider.getTarget();
        if (livingEntity == null) {
            return;
        }
        if (this.raider.squaredDistanceTo(livingEntity) > (double)this.squaredDistance) {
            this.raider.getLookControl().lookAt(livingEntity, 30.0f, 30.0f);
            if (this.raider.random.nextInt(50) == 0) {
                this.raider.playAmbientSound();
            }
        } else {
            this.raider.setAttacking(true);
        }
        super.tick();
    }
}
