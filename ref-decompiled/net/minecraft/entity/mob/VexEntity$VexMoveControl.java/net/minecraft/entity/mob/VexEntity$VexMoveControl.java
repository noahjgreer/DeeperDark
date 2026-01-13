/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

class VexEntity.VexMoveControl
extends MoveControl {
    public VexEntity.VexMoveControl(VexEntity owner) {
        super(owner);
    }

    @Override
    public void tick() {
        if (this.state != MoveControl.State.MOVE_TO) {
            return;
        }
        Vec3d vec3d = new Vec3d(this.targetX - VexEntity.this.getX(), this.targetY - VexEntity.this.getY(), this.targetZ - VexEntity.this.getZ());
        double d = vec3d.length();
        if (d < VexEntity.this.getBoundingBox().getAverageSideLength()) {
            this.state = MoveControl.State.WAIT;
            VexEntity.this.setVelocity(VexEntity.this.getVelocity().multiply(0.5));
        } else {
            VexEntity.this.setVelocity(VexEntity.this.getVelocity().add(vec3d.multiply(this.speed * 0.05 / d)));
            if (VexEntity.this.getTarget() == null) {
                Vec3d vec3d2 = VexEntity.this.getVelocity();
                VexEntity.this.setYaw(-((float)MathHelper.atan2(vec3d2.x, vec3d2.z)) * 57.295776f);
                VexEntity.this.bodyYaw = VexEntity.this.getYaw();
            } else {
                double e = VexEntity.this.getTarget().getX() - VexEntity.this.getX();
                double f = VexEntity.this.getTarget().getZ() - VexEntity.this.getZ();
                VexEntity.this.setYaw(-((float)MathHelper.atan2(e, f)) * 57.295776f);
                VexEntity.this.bodyYaw = VexEntity.this.getYaw();
            }
        }
    }
}
