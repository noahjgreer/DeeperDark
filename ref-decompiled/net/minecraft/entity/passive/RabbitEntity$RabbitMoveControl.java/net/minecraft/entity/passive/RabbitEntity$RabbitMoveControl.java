/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.passive.RabbitEntity;

static class RabbitEntity.RabbitMoveControl
extends MoveControl {
    private final RabbitEntity rabbit;
    private double rabbitSpeed;

    public RabbitEntity.RabbitMoveControl(RabbitEntity owner) {
        super(owner);
        this.rabbit = owner;
    }

    @Override
    public void tick() {
        if (this.rabbit.isOnGround() && !this.rabbit.jumping && !((RabbitEntity.RabbitJumpControl)this.rabbit.jumpControl).isActive()) {
            this.rabbit.setSpeed(0.0);
        } else if (this.isMoving() || this.state == MoveControl.State.JUMPING) {
            this.rabbit.setSpeed(this.rabbitSpeed);
        }
        super.tick();
    }

    @Override
    public void moveTo(double x, double y, double z, double speed) {
        if (this.rabbit.isTouchingWater()) {
            speed = 1.5;
        }
        super.moveTo(x, y, z, speed);
        if (speed > 0.0) {
            this.rabbitSpeed = speed;
        }
    }
}
