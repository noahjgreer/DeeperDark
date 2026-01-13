/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.ai.control.JumpControl;
import net.minecraft.entity.mob.CreakingEntity;

class CreakingEntity.CreakingJumpControl
extends JumpControl {
    public CreakingEntity.CreakingJumpControl(CreakingEntity creaking) {
        super(creaking);
    }

    @Override
    public void tick() {
        if (CreakingEntity.this.isUnrooted()) {
            super.tick();
        } else {
            CreakingEntity.this.setJumping(false);
        }
    }
}
