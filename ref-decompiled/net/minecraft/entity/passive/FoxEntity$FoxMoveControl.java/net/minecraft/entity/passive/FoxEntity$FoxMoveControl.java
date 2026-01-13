/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.control.MoveControl;

class FoxEntity.FoxMoveControl
extends MoveControl {
    public FoxEntity.FoxMoveControl() {
        super(FoxEntity.this);
    }

    @Override
    public void tick() {
        if (FoxEntity.this.wantsToPickupItem()) {
            super.tick();
        }
    }
}
