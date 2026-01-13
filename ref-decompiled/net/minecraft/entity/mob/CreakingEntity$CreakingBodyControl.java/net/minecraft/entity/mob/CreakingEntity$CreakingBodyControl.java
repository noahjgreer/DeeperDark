/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.mob.CreakingEntity;

class CreakingEntity.CreakingBodyControl
extends BodyControl {
    public CreakingEntity.CreakingBodyControl(CreakingEntity creaking) {
        super(creaking);
    }

    @Override
    public void tick() {
        if (CreakingEntity.this.isUnrooted()) {
            super.tick();
        }
    }
}
