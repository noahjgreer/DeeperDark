/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.util.math.MathHelper;

class HappyGhastEntity.HappyGhastLookControl
extends LookControl {
    HappyGhastEntity.HappyGhastLookControl() {
        super(HappyGhastEntity.this);
    }

    @Override
    public void tick() {
        if (HappyGhastEntity.this.isStill()) {
            float f = HappyGhastEntity.HappyGhastLookControl.getYawToSubtract(HappyGhastEntity.this.getYaw());
            HappyGhastEntity.this.setYaw(HappyGhastEntity.this.getYaw() - f);
            HappyGhastEntity.this.setHeadYaw(HappyGhastEntity.this.getYaw());
            return;
        }
        if (this.lookAtTimer > 0) {
            --this.lookAtTimer;
            double d = this.x - HappyGhastEntity.this.getX();
            double e = this.z - HappyGhastEntity.this.getZ();
            HappyGhastEntity.this.setYaw(-((float)MathHelper.atan2(d, e)) * 57.295776f);
            HappyGhastEntity.this.headYaw = HappyGhastEntity.this.bodyYaw = HappyGhastEntity.this.getYaw();
            return;
        }
        GhastEntity.updateYaw(this.entity);
    }

    public static float getYawToSubtract(float yaw) {
        float f = yaw % 90.0f;
        if (f >= 45.0f) {
            f -= 90.0f;
        }
        if (f < -45.0f) {
            f += 90.0f;
        }
        return f;
    }
}
