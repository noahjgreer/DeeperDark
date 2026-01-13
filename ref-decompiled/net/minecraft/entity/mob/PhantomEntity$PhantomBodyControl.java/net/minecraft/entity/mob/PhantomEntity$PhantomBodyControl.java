/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.mob.MobEntity;

class PhantomEntity.PhantomBodyControl
extends BodyControl {
    public PhantomEntity.PhantomBodyControl(MobEntity entity) {
        super(entity);
    }

    @Override
    public void tick() {
        PhantomEntity.this.headYaw = PhantomEntity.this.bodyYaw;
        PhantomEntity.this.bodyYaw = PhantomEntity.this.getYaw();
    }
}
