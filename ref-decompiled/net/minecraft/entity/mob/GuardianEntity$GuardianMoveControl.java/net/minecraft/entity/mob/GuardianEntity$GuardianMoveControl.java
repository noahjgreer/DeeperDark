/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

static class GuardianEntity.GuardianMoveControl
extends MoveControl {
    private final GuardianEntity guardian;

    public GuardianEntity.GuardianMoveControl(GuardianEntity guardian) {
        super(guardian);
        this.guardian = guardian;
    }

    @Override
    public void tick() {
        if (this.state != MoveControl.State.MOVE_TO || this.guardian.getNavigation().isIdle()) {
            this.guardian.setMovementSpeed(0.0f);
            this.guardian.setSpikesRetracted(false);
            return;
        }
        Vec3d vec3d = new Vec3d(this.targetX - this.guardian.getX(), this.targetY - this.guardian.getY(), this.targetZ - this.guardian.getZ());
        double d = vec3d.length();
        double e = vec3d.x / d;
        double f = vec3d.y / d;
        double g = vec3d.z / d;
        float h = (float)(MathHelper.atan2(vec3d.z, vec3d.x) * 57.2957763671875) - 90.0f;
        this.guardian.setYaw(this.wrapDegrees(this.guardian.getYaw(), h, 90.0f));
        this.guardian.bodyYaw = this.guardian.getYaw();
        float i = (float)(this.speed * this.guardian.getAttributeValue(EntityAttributes.MOVEMENT_SPEED));
        float j = MathHelper.lerp(0.125f, this.guardian.getMovementSpeed(), i);
        this.guardian.setMovementSpeed(j);
        double k = Math.sin((double)(this.guardian.age + this.guardian.getId()) * 0.5) * 0.05;
        double l = Math.cos(this.guardian.getYaw() * ((float)Math.PI / 180));
        double m = Math.sin(this.guardian.getYaw() * ((float)Math.PI / 180));
        double n = Math.sin((double)(this.guardian.age + this.guardian.getId()) * 0.75) * 0.05;
        this.guardian.setVelocity(this.guardian.getVelocity().add(k * l, n * (m + l) * 0.25 + (double)j * f * 0.1, k * m));
        LookControl lookControl = this.guardian.getLookControl();
        double o = this.guardian.getX() + e * 2.0;
        double p = this.guardian.getEyeY() + f / d;
        double q = this.guardian.getZ() + g * 2.0;
        double r = lookControl.getLookX();
        double s = lookControl.getLookY();
        double t = lookControl.getLookZ();
        if (!lookControl.isLookingAtSpecificPosition()) {
            r = o;
            s = p;
            t = q;
        }
        this.guardian.getLookControl().lookAt(MathHelper.lerp(0.125, r, o), MathHelper.lerp(0.125, s, p), MathHelper.lerp(0.125, t, q), 10.0f, 40.0f);
        this.guardian.setSpikesRetracted(true);
    }
}
