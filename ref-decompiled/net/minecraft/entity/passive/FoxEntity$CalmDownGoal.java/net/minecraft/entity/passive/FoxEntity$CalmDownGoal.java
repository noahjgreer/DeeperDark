/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.math.BlockPos;

abstract class FoxEntity.CalmDownGoal
extends Goal {
    private final TargetPredicate WORRIABLE_ENTITY_PREDICATE;

    FoxEntity.CalmDownGoal() {
        this.WORRIABLE_ENTITY_PREDICATE = TargetPredicate.createAttackable().setBaseMaxDistance(12.0).ignoreVisibility().setPredicate(new FoxEntity.WorriableEntityFilter(FoxEntity.this));
    }

    protected boolean isAtFavoredLocation() {
        BlockPos blockPos = BlockPos.ofFloored(FoxEntity.this.getX(), FoxEntity.this.getBoundingBox().maxY, FoxEntity.this.getZ());
        return !FoxEntity.this.getEntityWorld().isSkyVisible(blockPos) && FoxEntity.this.getPathfindingFavor(blockPos) >= 0.0f;
    }

    protected boolean canCalmDown() {
        return !FoxEntity.CalmDownGoal.castToServerWorld(FoxEntity.this.getEntityWorld()).getTargets(LivingEntity.class, this.WORRIABLE_ENTITY_PREDICATE, FoxEntity.this, FoxEntity.this.getBoundingBox().expand(12.0, 6.0, 12.0)).isEmpty();
    }
}
