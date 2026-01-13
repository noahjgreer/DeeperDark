/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.mob.CreakingEntity;

class CreakingEntity.CreakingMoveControl
extends MoveControl {
    public CreakingEntity.CreakingMoveControl(CreakingEntity creaking) {
        super(creaking);
    }

    @Override
    public void tick() {
        if (CreakingEntity.this.isUnrooted()) {
            super.tick();
        }
    }
}
