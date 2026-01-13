/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.control.MoveControl;

class CamelEntity.CamelMoveControl
extends MoveControl {
    public CamelEntity.CamelMoveControl() {
        super(CamelEntity.this);
    }

    @Override
    public void tick() {
        if (this.state == MoveControl.State.MOVE_TO && !CamelEntity.this.isLeashed() && CamelEntity.this.isSitting() && !CamelEntity.this.isChangingPose() && CamelEntity.this.canChangePose()) {
            CamelEntity.this.startStanding();
        }
        super.tick();
    }
}
