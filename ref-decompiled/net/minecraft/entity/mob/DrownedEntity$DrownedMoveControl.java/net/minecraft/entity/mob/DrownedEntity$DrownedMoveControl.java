/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.util.math.MathHelper;

static class DrownedEntity.DrownedMoveControl
extends MoveControl {
    private final DrownedEntity drowned;

    public DrownedEntity.DrownedMoveControl(DrownedEntity drowned) {
        super(drowned);
        this.drowned = drowned;
    }

    @Override
    public void tick() {
        LivingEntity livingEntity = this.drowned.getTarget();
        if (this.drowned.isTargetingUnderwater() && this.drowned.isTouchingWater()) {
            if (livingEntity != null && livingEntity.getY() > this.drowned.getY() || this.drowned.targetingUnderwater) {
                this.drowned.setVelocity(this.drowned.getVelocity().add(0.0, 0.002, 0.0));
            }
            if (this.state != MoveControl.State.MOVE_TO || this.drowned.getNavigation().isIdle()) {
                this.drowned.setMovementSpeed(0.0f);
                return;
            }
            double d = this.targetX - this.drowned.getX();
            double e = this.targetY - this.drowned.getY();
            double f = this.targetZ - this.drowned.getZ();
            double g = Math.sqrt(d * d + e * e + f * f);
            e /= g;
            float h = (float)(MathHelper.atan2(f, d) * 57.2957763671875) - 90.0f;
            this.drowned.setYaw(this.wrapDegrees(this.drowned.getYaw(), h, 90.0f));
            this.drowned.bodyYaw = this.drowned.getYaw();
            float i = (float)(this.speed * this.drowned.getAttributeValue(EntityAttributes.MOVEMENT_SPEED));
            float j = MathHelper.lerp(0.125f, this.drowned.getMovementSpeed(), i);
            this.drowned.setMovementSpeed(j);
            this.drowned.setVelocity(this.drowned.getVelocity().add((double)j * d * 0.005, (double)j * e * 0.1, (double)j * f * 0.005));
        } else {
            if (!this.drowned.isOnGround()) {
                this.drowned.setVelocity(this.drowned.getVelocity().add(0.0, -0.008, 0.0));
            }
            super.tick();
        }
    }
}
