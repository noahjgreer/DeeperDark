/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.ai.goal.LongDoorInteractGoal;
import net.minecraft.entity.raid.RaiderEntity;

protected class IllagerEntity.LongDoorInteractGoal
extends LongDoorInteractGoal {
    public IllagerEntity.LongDoorInteractGoal(RaiderEntity raider) {
        super(raider, false);
    }

    @Override
    public boolean canStart() {
        return super.canStart() && IllagerEntity.this.hasActiveRaid();
    }
}
