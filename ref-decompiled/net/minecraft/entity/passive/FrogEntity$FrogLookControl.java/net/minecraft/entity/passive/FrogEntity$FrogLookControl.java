/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.mob.MobEntity;

class FrogEntity.FrogLookControl
extends LookControl {
    FrogEntity.FrogLookControl(MobEntity entity) {
        super(entity);
    }

    @Override
    protected boolean shouldStayHorizontal() {
        return FrogEntity.this.getFrogTarget().isEmpty();
    }
}
