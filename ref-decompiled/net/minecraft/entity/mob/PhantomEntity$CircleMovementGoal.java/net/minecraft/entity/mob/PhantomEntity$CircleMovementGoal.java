/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

class PhantomEntity.CircleMovementGoal
extends PhantomEntity.MovementGoal {
    private float angle;
    private float radius;
    private float yOffset;
    private float circlingDirection;

    PhantomEntity.CircleMovementGoal() {
        super(PhantomEntity.this);
    }

    @Override
    public boolean canStart() {
        return PhantomEntity.this.getTarget() == null || PhantomEntity.this.movementType == PhantomEntity.PhantomMovementType.CIRCLE;
    }

    @Override
    public void start() {
        this.radius = 5.0f + PhantomEntity.this.random.nextFloat() * 10.0f;
        this.yOffset = -4.0f + PhantomEntity.this.random.nextFloat() * 9.0f;
        this.circlingDirection = PhantomEntity.this.random.nextBoolean() ? 1.0f : -1.0f;
        this.adjustDirection();
    }

    @Override
    public void tick() {
        if (PhantomEntity.this.random.nextInt(this.getTickCount(350)) == 0) {
            this.yOffset = -4.0f + PhantomEntity.this.random.nextFloat() * 9.0f;
        }
        if (PhantomEntity.this.random.nextInt(this.getTickCount(250)) == 0) {
            this.radius += 1.0f;
            if (this.radius > 15.0f) {
                this.radius = 5.0f;
                this.circlingDirection = -this.circlingDirection;
            }
        }
        if (PhantomEntity.this.random.nextInt(this.getTickCount(450)) == 0) {
            this.angle = PhantomEntity.this.random.nextFloat() * 2.0f * (float)Math.PI;
            this.adjustDirection();
        }
        if (this.isNearTarget()) {
            this.adjustDirection();
        }
        if (PhantomEntity.this.targetPosition.y < PhantomEntity.this.getY() && !PhantomEntity.this.getEntityWorld().isAir(PhantomEntity.this.getBlockPos().down(1))) {
            this.yOffset = Math.max(1.0f, this.yOffset);
            this.adjustDirection();
        }
        if (PhantomEntity.this.targetPosition.y > PhantomEntity.this.getY() && !PhantomEntity.this.getEntityWorld().isAir(PhantomEntity.this.getBlockPos().up(1))) {
            this.yOffset = Math.min(-1.0f, this.yOffset);
            this.adjustDirection();
        }
    }

    private void adjustDirection() {
        if (PhantomEntity.this.circlingCenter == null) {
            PhantomEntity.this.circlingCenter = PhantomEntity.this.getBlockPos();
        }
        this.angle += this.circlingDirection * 15.0f * ((float)Math.PI / 180);
        PhantomEntity.this.targetPosition = Vec3d.of(PhantomEntity.this.circlingCenter).add(this.radius * MathHelper.cos(this.angle), -4.0f + this.yOffset, this.radius * MathHelper.sin(this.angle));
    }
}
