/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.mob.CreakingEntity;

class CreakingEntity.CreakingLookControl
extends LookControl {
    public CreakingEntity.CreakingLookControl(CreakingEntity creaking) {
        super(creaking);
    }

    @Override
    public void tick() {
        if (CreakingEntity.this.isUnrooted()) {
            super.tick();
        }
    }
}
