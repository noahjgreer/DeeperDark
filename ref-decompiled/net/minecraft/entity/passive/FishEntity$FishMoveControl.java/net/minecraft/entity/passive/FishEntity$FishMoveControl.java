/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.MathHelper;

static class FishEntity.FishMoveControl
extends MoveControl {
    private final FishEntity fish;

    FishEntity.FishMoveControl(FishEntity owner) {
        super(owner);
        this.fish = owner;
    }

    @Override
    public void tick() {
        if (this.fish.isSubmergedIn(FluidTags.WATER)) {
            this.fish.setVelocity(this.fish.getVelocity().add(0.0, 0.005, 0.0));
        }
        if (this.state != MoveControl.State.MOVE_TO || this.fish.getNavigation().isIdle()) {
            this.fish.setMovementSpeed(0.0f);
            return;
        }
        float f = (float)(this.speed * this.fish.getAttributeValue(EntityAttributes.MOVEMENT_SPEED));
        this.fish.setMovementSpeed(MathHelper.lerp(0.125f, this.fish.getMovementSpeed(), f));
        double d = this.targetX - this.fish.getX();
        double e = this.targetY - this.fish.getY();
        double g = this.targetZ - this.fish.getZ();
        if (e != 0.0) {
            double h = Math.sqrt(d * d + e * e + g * g);
            this.fish.setVelocity(this.fish.getVelocity().add(0.0, (double)this.fish.getMovementSpeed() * (e / h) * 0.1, 0.0));
        }
        if (d != 0.0 || g != 0.0) {
            float i = (float)(MathHelper.atan2(g, d) * 57.2957763671875) - 90.0f;
            this.fish.setYaw(this.wrapDegrees(this.fish.getYaw(), i, 90.0f));
            this.fish.bodyYaw = this.fish.getYaw();
        }
    }
}
