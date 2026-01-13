/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.VindicatorEntity;

static class VindicatorEntity.TargetGoal
extends ActiveTargetGoal<LivingEntity> {
    public VindicatorEntity.TargetGoal(VindicatorEntity vindicator) {
        super(vindicator, LivingEntity.class, 0, true, true, (target, world) -> target.isMobOrPlayer());
    }

    @Override
    public boolean canStart() {
        return ((VindicatorEntity)this.mob).johnny && super.canStart();
    }

    @Override
    public void start() {
        super.start();
        this.mob.setDespawnCounter(0);
    }
}
