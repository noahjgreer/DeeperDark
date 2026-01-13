/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.control.BodyControl;

class HappyGhastEntity.HappyGhastBodyControl
extends BodyControl {
    public HappyGhastEntity.HappyGhastBodyControl() {
        super(HappyGhastEntity.this);
    }

    @Override
    public void tick() {
        if (HappyGhastEntity.this.hasPassengers()) {
            HappyGhastEntity.this.bodyYaw = HappyGhastEntity.this.headYaw = HappyGhastEntity.this.getYaw();
        }
        super.tick();
    }
}
