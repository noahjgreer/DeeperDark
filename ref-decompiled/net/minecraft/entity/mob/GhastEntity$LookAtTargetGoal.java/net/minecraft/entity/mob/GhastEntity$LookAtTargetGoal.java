/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.MobEntity;

public static class GhastEntity.LookAtTargetGoal
extends Goal {
    private final MobEntity ghast;

    public GhastEntity.LookAtTargetGoal(MobEntity ghast) {
        this.ghast = ghast;
        this.setControls(EnumSet.of(Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        return true;
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        GhastEntity.updateYaw(this.ghast);
    }
}
