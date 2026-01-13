/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.mob.MobEntity;

class BeeEntity.BeeLookControl
extends LookControl {
    BeeEntity.BeeLookControl(MobEntity entity) {
        super(entity);
    }

    @Override
    public void tick() {
        if (BeeEntity.this.hasAngerTime()) {
            return;
        }
        super.tick();
    }

    @Override
    protected boolean shouldStayHorizontal() {
        return !BeeEntity.this.pollinateGoal.isRunning();
    }
}
