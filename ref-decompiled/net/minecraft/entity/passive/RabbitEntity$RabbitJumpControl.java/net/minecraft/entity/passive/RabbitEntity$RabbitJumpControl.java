/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.control.JumpControl;
import net.minecraft.entity.passive.RabbitEntity;

public static class RabbitEntity.RabbitJumpControl
extends JumpControl {
    private final RabbitEntity rabbit;
    private boolean canJump;

    public RabbitEntity.RabbitJumpControl(RabbitEntity rabbit) {
        super(rabbit);
        this.rabbit = rabbit;
    }

    public boolean isActive() {
        return this.active;
    }

    public boolean canJump() {
        return this.canJump;
    }

    public void setCanJump(boolean canJump) {
        this.canJump = canJump;
    }

    @Override
    public void tick() {
        if (this.active) {
            this.rabbit.startJump();
            this.active = false;
        }
    }
}
