/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.math.MathHelper;

static class TurtleEntity.TurtleMoveControl
extends MoveControl {
    private final TurtleEntity turtle;

    TurtleEntity.TurtleMoveControl(TurtleEntity turtle) {
        super(turtle);
        this.turtle = turtle;
    }

    private void updateVelocity() {
        if (this.turtle.isTouchingWater()) {
            this.turtle.setVelocity(this.turtle.getVelocity().add(0.0, 0.005, 0.0));
            if (!this.turtle.homePos.isWithinDistance(this.turtle.getEntityPos(), 16.0)) {
                this.turtle.setMovementSpeed(Math.max(this.turtle.getMovementSpeed() / 2.0f, 0.08f));
            }
            if (this.turtle.isBaby()) {
                this.turtle.setMovementSpeed(Math.max(this.turtle.getMovementSpeed() / 3.0f, 0.06f));
            }
        } else if (this.turtle.isOnGround()) {
            this.turtle.setMovementSpeed(Math.max(this.turtle.getMovementSpeed() / 2.0f, 0.06f));
        }
    }

    @Override
    public void tick() {
        double f;
        double e;
        this.updateVelocity();
        if (this.state != MoveControl.State.MOVE_TO || this.turtle.getNavigation().isIdle()) {
            this.turtle.setMovementSpeed(0.0f);
            return;
        }
        double d = this.targetX - this.turtle.getX();
        double g = Math.sqrt(d * d + (e = this.targetY - this.turtle.getY()) * e + (f = this.targetZ - this.turtle.getZ()) * f);
        if (g < (double)1.0E-5f) {
            this.entity.setMovementSpeed(0.0f);
            return;
        }
        e /= g;
        float h = (float)(MathHelper.atan2(f, d) * 57.2957763671875) - 90.0f;
        this.turtle.setYaw(this.wrapDegrees(this.turtle.getYaw(), h, 90.0f));
        this.turtle.bodyYaw = this.turtle.getYaw();
        float i = (float)(this.speed * this.turtle.getAttributeValue(EntityAttributes.MOVEMENT_SPEED));
        this.turtle.setMovementSpeed(MathHelper.lerp(0.125f, this.turtle.getMovementSpeed(), i));
        this.turtle.setVelocity(this.turtle.getVelocity().add(0.0, (double)this.turtle.getMovementSpeed() * e * 0.1, 0.0));
    }
}
