/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import net.minecraft.entity.ai.goal.BreakDoorGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.VindicatorEntity;

static class VindicatorEntity.BreakDoorGoal
extends BreakDoorGoal {
    public VindicatorEntity.BreakDoorGoal(MobEntity mobEntity) {
        super(mobEntity, 6, DIFFICULTY_ALLOWS_DOOR_BREAKING_PREDICATE);
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean shouldContinue() {
        VindicatorEntity vindicatorEntity = (VindicatorEntity)this.mob;
        return vindicatorEntity.hasActiveRaid() && super.shouldContinue();
    }

    @Override
    public boolean canStart() {
        VindicatorEntity vindicatorEntity = (VindicatorEntity)this.mob;
        return vindicatorEntity.hasActiveRaid() && vindicatorEntity.random.nextInt(VindicatorEntity.BreakDoorGoal.toGoalTicks(10)) == 0 && super.canStart();
    }

    @Override
    public void start() {
        super.start();
        this.mob.setDespawnCounter(0);
    }
}
