/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

class PhantomEntity.PhantomMoveControl
extends MoveControl {
    private float targetSpeed;

    public PhantomEntity.PhantomMoveControl(MobEntity owner) {
        super(owner);
        this.targetSpeed = 0.1f;
    }

    @Override
    public void tick() {
        if (PhantomEntity.this.horizontalCollision) {
            PhantomEntity.this.setYaw(PhantomEntity.this.getYaw() + 180.0f);
            this.targetSpeed = 0.1f;
        }
        double d = PhantomEntity.this.targetPosition.x - PhantomEntity.this.getX();
        double e = PhantomEntity.this.targetPosition.y - PhantomEntity.this.getY();
        double f = PhantomEntity.this.targetPosition.z - PhantomEntity.this.getZ();
        double g = Math.sqrt(d * d + f * f);
        if (Math.abs(g) > (double)1.0E-5f) {
            double h = 1.0 - Math.abs(e * (double)0.7f) / g;
            g = Math.sqrt((d *= h) * d + (f *= h) * f);
            double i = Math.sqrt(d * d + f * f + e * e);
            float j = PhantomEntity.this.getYaw();
            float k = (float)MathHelper.atan2(f, d);
            float l = MathHelper.wrapDegrees(PhantomEntity.this.getYaw() + 90.0f);
            float m = MathHelper.wrapDegrees(k * 57.295776f);
            PhantomEntity.this.setYaw(MathHelper.stepUnwrappedAngleTowards(l, m, 4.0f) - 90.0f);
            PhantomEntity.this.bodyYaw = PhantomEntity.this.getYaw();
            this.targetSpeed = MathHelper.angleBetween(j, PhantomEntity.this.getYaw()) < 3.0f ? MathHelper.stepTowards(this.targetSpeed, 1.8f, 0.005f * (1.8f / this.targetSpeed)) : MathHelper.stepTowards(this.targetSpeed, 0.2f, 0.025f);
            float n = (float)(-(MathHelper.atan2(-e, g) * 57.2957763671875));
            PhantomEntity.this.setPitch(n);
            float o = PhantomEntity.this.getYaw() + 90.0f;
            double p = (double)(this.targetSpeed * MathHelper.cos(o * ((float)Math.PI / 180))) * Math.abs(d / i);
            double q = (double)(this.targetSpeed * MathHelper.sin(o * ((float)Math.PI / 180))) * Math.abs(f / i);
            double r = (double)(this.targetSpeed * MathHelper.sin(n * ((float)Math.PI / 180))) * Math.abs(e / i);
            Vec3d vec3d = PhantomEntity.this.getVelocity();
            PhantomEntity.this.setVelocity(vec3d.add(new Vec3d(p, r, q).subtract(vec3d).multiply(0.2)));
        }
    }
}
