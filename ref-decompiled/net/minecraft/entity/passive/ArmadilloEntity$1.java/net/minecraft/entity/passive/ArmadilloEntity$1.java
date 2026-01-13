/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.mob.MobEntity;

class ArmadilloEntity.1
extends BodyControl {
    ArmadilloEntity.1(MobEntity mobEntity) {
        super(mobEntity);
    }

    @Override
    public void tick() {
        if (!ArmadilloEntity.this.isNotIdle()) {
            super.tick();
        }
    }
}
