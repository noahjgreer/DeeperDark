/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;

class VexEntity.TrackOwnerTargetGoal
extends TrackTargetGoal {
    private final TargetPredicate targetPredicate;

    public VexEntity.TrackOwnerTargetGoal(PathAwareEntity mob) {
        super(mob, false);
        this.targetPredicate = TargetPredicate.createNonAttackable().ignoreVisibility().ignoreDistanceScalingFactor();
    }

    @Override
    public boolean canStart() {
        MobEntity mobEntity = VexEntity.this.getOwner();
        return mobEntity != null && mobEntity.getTarget() != null && this.canTrack(mobEntity.getTarget(), this.targetPredicate);
    }

    @Override
    public void start() {
        MobEntity mobEntity = VexEntity.this.getOwner();
        VexEntity.this.setTarget(mobEntity != null ? mobEntity.getTarget() : null);
        super.start();
    }
}
